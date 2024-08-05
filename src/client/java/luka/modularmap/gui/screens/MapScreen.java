package luka.modularmap.gui.screens;

import luka.modularmap.ModularMapClient;
import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.gui.widgets.ChunksWidget;
import luka.modularmap.map.ChunkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MapScreen extends BaseScreen {
    public MapScreen() {
        super("Map Screen");
    }

    @Override
    protected void init() {
        ChunkManager chunkManager = ModularMapClient.getInstance().getChunkManager();
        ClientPlayerEntity player = client.player;
        addDrawableChild(new ChunksWidget(
                player,
                chunkManager.getLoadedChunks(), chunkManager.getUnloadedChunks(),
                width - PADDING - FRAME_SPACING - BUTTON_SIZE, width, height
        ));

        ButtonWidget configButton = new TexturedButtonWidget(
                width - FRAME_SPACING - BUTTON_SIZE, height - FRAME_SPACING - BUTTON_SIZE,
                BUTTON_SIZE, BUTTON_SIZE,
                new ButtonTextures(
                    Identifier.of(ModularMapClient.MOD_ID, "map/buttons/config"),
                    Identifier.of(ModularMapClient.MOD_ID, "map/buttons/config_highlighted")
                ),
                button -> client.setScreen(new ConfigScreen(this)),
                Text.literal("Configuration")
        ),
                waypointButton = new TexturedButtonWidget(
                        width - FRAME_SPACING - BUTTON_SIZE, height - FRAME_SPACING - BUTTON_SIZE * 2 - PADDING,
                        BUTTON_SIZE, BUTTON_SIZE,
                        new ButtonTextures(
                                Identifier.of(ModularMapClient.MOD_ID, "map/buttons/waypoints"),
                                Identifier.of(ModularMapClient.MOD_ID, "map/buttons/waypoints_highlighted")
                        ),
                        button -> client.setScreen(new WaypointScreen(this)),
                        Text.literal("Waypoints")
                );

        addDrawableChild(configButton);
        addDrawableChild(waypointButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode)) {
            client.player.closeHandledScreen();
            return true;
        } else if (KeyInputHandler.openWaypointsKey.matchesKey(keyCode, scanCode)) {
            client.setScreen(new WaypointScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // background
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, ConfigManager.getConfig().backgroundColor.getValue());

        // render map
        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }
}
