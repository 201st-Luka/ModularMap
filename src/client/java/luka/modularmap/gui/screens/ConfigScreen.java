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
import net.minecraft.client.gui.widget.*;
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
            int contentStartHeight = calculateHeaderHeight() + padding;

            addDrawable(new TextWidget(
                    spacingWidth, contentStartHeight + row * (textHeight + padding),
                    textWidth, textHeight,
                    Text.literal(field.getName()), textRenderer
            ));

            AtomicBoolean modified = new AtomicBoolean(false);
            try {
                modified.set(field.get(config) == field.get(ConfigManager.getConfig()));
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
            if (value == null) {
                addDrawable(new EditBoxWidget(
                        textRenderer,
                        spacingWidth + columnWidth - textWidth - padding - buttonSize,
                        contentStartHeight + row * (textHeight + padding),
                        textWidth, textHeight,
                        Text.literal("null"),
                        Text.literal("")
                ));
            } else if (value instanceof Color color) {
                EditBoxWidget editBox;
                try {
                    editBox = new EditBoxWidget(
                            textRenderer,
                            spacingWidth + columnWidth - textWidth - padding - buttonSize,
                            contentStartHeight + row * (textHeight + padding),
                            textWidth - padding - buttonSize, textHeight,
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
                        if (color != field.get(ConfigManager.getConfig()))
                            modified.set(true);
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
                        spacingWidth + columnWidth - padding - buttonSize * 2,
                        contentStartHeight + row * (textHeight + padding),
                        buttonSize, buttonSize,
                        color
                ));
            } else {
                addDrawable(new EditBoxWidget(
                        textRenderer,
                        spacingWidth + columnWidth - textWidth - padding - buttonSize,
                        contentStartHeight + row * (textHeight + padding),
                        textWidth, textHeight,
                        Text.literal(value.toString()),
                        Text.literal("")
                ));
            }

            addDrawable(new LockableTexturedButtonWidget(
                    spacingWidth + columnWidth - buttonSize, contentStartHeight + row++ * (textHeight + padding),
                    buttonSize, buttonSize,
                    new ButtonTextures(
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset"),
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset_disabled"),
                            Identifier.of(ModularMapClient.MOD_ID, "map/buttons/reset_highlighted")
                    ),
                    button -> {
                        try {
                            field.set(config, field.get(defaultConfig));
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
                        .dimensions(spacingWidth + columnWidth / 2 - textWidth - padding / 2,
                                calculateHeaderHeight() + calculateContentHeight() + padding,
                                textWidth, textHeight)
                        .tooltip(Tooltip.of(Text.literal("Discard changes")))
                        .build()
        );
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Apply"), button -> {
                                ConfigManager.updateConfig(config);
                                close();
                        })
                        .dimensions(spacingWidth + columnWidth / 2 + padding / 2,
                                calculateHeaderHeight() + calculateContentHeight() + padding,
                                textWidth, textHeight)
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
