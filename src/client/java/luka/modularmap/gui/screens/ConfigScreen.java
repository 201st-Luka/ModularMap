/*
 * ModularMap
 * Copyright (c) 2024 201st-Luka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package luka.modularmap.gui.screens;

import luka.modularmap.ModularMapClient;
import luka.modularmap.config.Color;
import luka.modularmap.config.ConfigManager;
import luka.modularmap.config.ModularMapConfig;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.gui.widgets.ColorDisplayWidget;
import luka.modularmap.gui.widgets.LockableTexturedButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends MenuScreen {
    protected ModularMapConfig config,
            defaultConfig = new ModularMapConfig();

    public ConfigScreen(Screen parent) {
        super("ModularMap Config", parent);

        config = ConfigManager.getConfig().clone();
    }

    @Override
    public void init() {
        super.init();

        int row = 0,
                spacingWidth = calculateSpacingWidth(),
                columnWidth = calculateColumnWidth();

        for (Field field : ModularMapConfig.class.getDeclaredFields()) {
            int contentStartHeight = calculateHeaderHeight() + PADDING;

            addDrawable(new TextWidget(
                    spacingWidth, contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                    TEXT_WIDTH, TEXT_HEIGHT,
                    Text.literal(field.getName()), textRenderer
            ));

            AtomicBoolean modified = new AtomicBoolean(false);
            try {
                modified.set(!field.get(config).equals(field.get(defaultConfig)));
            } catch (IllegalAccessException e) {
                client.getToastManager().add(SystemToast.create(
                        client,
                        SystemToast.Type.WORLD_ACCESS_FAILURE,
                        Text.of("IllegalAccessException"),
                        Text.of("Failed to compare field value: " + field.getName())
                ));
                ModularMapClient.LOGGER.error("Failed to compare field value: {}", field.getName(), e);
                modified.set(false);
            }

            Object value = null;
            try {
                value = field.get(config);
            } catch (IllegalAccessException e) {
                client.getToastManager().add(SystemToast.create(
                        client,
                        SystemToast.Type.WORLD_ACCESS_FAILURE,
                        Text.of("IllegalAccessException"),
                        Text.of("Failed to get field value: " + field.getName())
                ));
                ModularMapClient.LOGGER.error("Failed to get field value: {}", field.getName(), e);
            }
            EditBoxWidget editBox;
            if (value == null) {
                editBox = new EditBoxWidget(
                        textRenderer,
                        spacingWidth + columnWidth - TEXT_WIDTH - PADDING - BUTTON_SIZE,
                        contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                        TEXT_WIDTH, TEXT_HEIGHT,
                        Text.literal("null"),
                        Text.literal("")
                );
                addDrawable(editBox);
            } else if (value instanceof Color color) {
                try {
                    editBox = new EditBoxWidget(
                            textRenderer,
                            spacingWidth + columnWidth - TEXT_WIDTH - PADDING - BUTTON_SIZE,
                            contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                            TEXT_WIDTH - PADDING - BUTTON_SIZE, TEXT_HEIGHT,
                            Text.literal(((Color) field.get(defaultConfig)).toHex()),
                            Text.literal("Hex color")
                    );
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                editBox.setText(color.toHex());
                editBox.setMaxLength(8);
                editBox.setChangeListener(s -> {
                    try {
                        color.setHex(s);
                        modified.set(!color.equals(field.get(defaultConfig)));
                    } catch (NumberFormatException e) {
                        client.getToastManager().add(SystemToast.create(
                                client,
                                SystemToast.Type.WORLD_ACCESS_FAILURE,
                                Text.of("NumberFormatException"),
                                Text.of("Failed to set color: " + s)
                        ));
                        ModularMapClient.LOGGER.error("Failed to set color: {}", s, e);
                    } catch (IllegalAccessException e) {
                        client.getToastManager().add(SystemToast.create(
                                client,
                                SystemToast.Type.WORLD_ACCESS_FAILURE,
                                Text.of("IllegalAccessException"),
                                Text.of("Failed to set field value: " + field.getName())
                        ));
                        ModularMapClient.LOGGER.error("Failed to set field value: {}", field.getName(), e);
                    }
                });
                addDrawableChild(editBox);
                addDrawable(new ColorDisplayWidget(
                        spacingWidth + columnWidth - PADDING - BUTTON_SIZE * 2,
                        contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                        BUTTON_SIZE, BUTTON_SIZE,
                        color
                ));
            } else {
                editBox = new EditBoxWidget(
                        textRenderer,
                        spacingWidth + columnWidth - TEXT_WIDTH - PADDING - BUTTON_SIZE,
                        contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                        TEXT_WIDTH, TEXT_HEIGHT,
                        Text.literal(value.toString()),
                        Text.literal("")
                );
                addDrawableChild(editBox);
            }

            // TODO make the button update on edit box change
            addDrawable(new LockableTexturedButtonWidget(
                    spacingWidth + columnWidth - BUTTON_SIZE, contentStartHeight + row++ * (TEXT_HEIGHT + PADDING),
                    BUTTON_SIZE, BUTTON_SIZE,
                    new ButtonTextures(
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset"),
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset_disabled"),
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset_highlighted")
                    ),
                    button -> {
                        try {
                            editBox.setText(field.get(defaultConfig).toString());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    Text.literal("Reset"),
                    modified.get()
            ));
        }

        addDrawableChild(
                ButtonWidget.builder(Text.literal("Cancel"), button -> close())
                        .dimensions(spacingWidth + columnWidth / 2 - TEXT_WIDTH - PADDING / 2,
                                calculateHeaderHeight() + calculateContentHeight() + PADDING,
                                TEXT_WIDTH, TEXT_HEIGHT)
                        .tooltip(Tooltip.of(Text.literal("Discard changes")))
                        .build()
        );
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Apply"), button -> {
                            ConfigManager.updateConfig(config);
                            close();
                        })
                        .dimensions(spacingWidth + columnWidth / 2 + PADDING / 2,
                                calculateHeaderHeight() + calculateContentHeight() + PADDING,
                                TEXT_WIDTH, TEXT_HEIGHT)
                        .tooltip(Tooltip.of(Text.literal("Apply changes")))
                        .build()
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode) && parent instanceof MapScreen) {
            client.player.closeHandledScreen();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
