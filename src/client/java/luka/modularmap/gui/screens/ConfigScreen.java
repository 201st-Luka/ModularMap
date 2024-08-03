package luka.modularmap.gui.screens;

import luka.modularmap.ModularMapClient;
import luka.modularmap.config.ConfigManager;
import luka.modularmap.config.ModularMapConfig;
import luka.modularmap.event.KeyInputHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends MenuScreen {
    public ConfigScreen(Screen parent) {
        super("ModularMap Config", parent);
    }

    @Override
    public void init() {
        super.init();

        int row = 0,
                spacingWidth = calculateSpacingWidth();
        int secondColumnX = width - spacingWidth - TEXT_WIDTH;

        ModularMapConfig config = ConfigManager.getConfig();

        for (Field field : config.getClass().getDeclaredFields()) {
            int contentStartHeight = calculateHeaderHeight() + PADDING;

            addDrawable(new TextWidget(
                    spacingWidth, contentStartHeight + row * (TEXT_HEIGHT + PADDING),
                    TEXT_WIDTH, TEXT_HEIGHT,
                    Text.literal(field.getName()), textRenderer
            ));

            Object value = null;
            try {
                value = field.get(config);
            } catch (IllegalAccessException e) {
                this.client.getToastManager().add(SystemToast.create(
                        this.client,
                        SystemToast.Type.WORLD_ACCESS_FAILURE,
                        Text.of("IllegalAccessException"),
                        Text.of("Failed to get field value: " + field.getName())
                ));
                ModularMapClient.LOGGER.error("Failed to get field value: {}", field.getName(), e);
            }

            addDrawable(new EditBoxWidget(
                    textRenderer,
                    secondColumnX, contentStartHeight + row++ * (TEXT_HEIGHT + PADDING),
                    TEXT_WIDTH, TEXT_HEIGHT,
                    Text.literal(""),
                    Text.literal(value != null ? value.toString() : "null")
            ));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode) && parent instanceof MapScreen) {
            this.client.player.closeHandledScreen();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
