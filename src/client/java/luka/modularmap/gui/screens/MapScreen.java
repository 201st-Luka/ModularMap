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

import com.mojang.blaze3d.systems.RenderSystem;
import luka.modularmap.ModularMapClient;
import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.map.MapChunk;
import luka.modularmap.map.WorldMap;
import luka.modularmap.world.CompressedBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MapScreen extends BaseScreen {
    private static final int GRID_COLOR = 0x40000000;
    private final WorldMap worldMap;
    private final ClientPlayerEntity player;
    private double scale;
    private double centerX, centerZ;
    private boolean isInitialized = false;

    public MapScreen() {
        super("Map Screen");

        player = MinecraftClient.getInstance().player;

        worldMap = modularMapClient.modularMap$getWorldMap();

        scale = 1;
    }

    @Override
    protected void init() {
        if (!isInitialized) {
            isInitialized = true;

            centerX = (double) width / 2;
            centerZ = (double) height / 2;
        }

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
                ),
                closeButton = new TexturedButtonWidget(
                        width - FRAME_SPACING - BUTTON_SIZE, FRAME_SPACING,
                        BUTTON_SIZE, BUTTON_SIZE,
                        new ButtonTextures(
                                Identifier.ofVanilla("widget/cross_button"),
                                Identifier.ofVanilla("widget/cross_button_highlighted")
                        ),
                        button -> client.setScreen(null),
                        Text.literal("Close")
                );

        addDrawableChild(configButton);
        addDrawableChild(waypointButton);
        addDrawableChild(closeButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode)) {
            player.closeHandledScreen();
            return true;
        } else if (KeyInputHandler.openWaypointsKey.matchesKey(keyCode, scanCode)) {
            client.setScreen(new WaypointScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int calculatePlayerRelChunkX() {
        return player.getBlockX() >= 0 ? player.getBlockX() % 16 : 16 + player.getBlockX() % 16;
    }

    private int calculatePlayerRelChunkZ() {
        return player.getBlockZ() >= 0 ? player.getBlockZ() % 16 : 16 + player.getBlockZ() % 16;
    }

    private int calculateGridX(int x, int playerRelX) {
        return (int) ((x * 16 - playerRelX) * scale + centerX % (16 * scale));
    }

    private int calculateGridZ(int z, int playerRelZ) {
        return (int) ((z * 16 - playerRelZ) * scale + centerZ % (16 * scale));
    }

    private void drawChunk(BufferBuilder buffer, Matrix4f transformationMatrix, MapChunk chunk) {
        if (chunk != null) {
            for (CompressedBlock[] blocks : chunk.getBlocks()) {
                for (CompressedBlock block : blocks) {
                    if (block == null) {
                        continue;
                    }
                    BlockPos pos = block.getBlockPos();
                    int blockColor = block.getColor();

                    buffer.vertex(transformationMatrix, pos.getX(), pos.getZ(), 5).color(blockColor);
                    buffer.vertex(transformationMatrix, pos.getX(), pos.getZ() + 1, 5).color(blockColor);
                    buffer.vertex(transformationMatrix, pos.getX() + 1, pos.getZ() + 1, 5).color(blockColor);
                    buffer.vertex(transformationMatrix, pos.getX() + 1, pos.getZ(), 5).color(blockColor);
                }
            }
        }
    }

    private void renderChunks(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(centerX, centerZ, 0);
        matrices.scale((float) scale, (float) scale, 1);

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (MapChunk chunk : worldMap.getChunks(-20, -20, 40, 40))
            drawChunk(buffer, transformationMatrix, chunk);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();
    }

    private void renderGrid(DrawContext context) {
        if (scale >= 0.7) {
            int playerRelChunkX = calculatePlayerRelChunkX(),
                    playerRelChunkZ = calculatePlayerRelChunkZ();

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(centerX, 0, 0);
            matrices.scale((float) scale, 1, 1);

            RenderSystem.lineWidth(10);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            // vertical lines
            buffer.vertex(transformationMatrix, -playerRelChunkX, 0, 10).color(GRID_COLOR);
            buffer.vertex(transformationMatrix, -playerRelChunkX, client.currentScreen.height, 10).color(GRID_COLOR);

            BufferRenderer.drawWithGlobalProgram(buffer.end());

            matrices.pop();
        }
    }

    private void renderPlayer(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(centerX, centerZ, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(player.getYaw() + 180));

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, 0, -5, 5).color(0xFFFF0000);
        buffer.vertex(transformationMatrix, -5, 5, 5).color(0xFFFF6666);
        buffer.vertex(transformationMatrix, 0, 3, 5).color(0xFFFF0000);
        buffer.vertex(transformationMatrix, 5, 5, 5).color(0xFFFF6666);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();
    }

    @SuppressWarnings("unused")
    private void renderMap(DrawContext context, int mouseX, int mouseY, float delta) {
        // todo: fix player not being placed at the right position
        renderChunks(context);

        renderGrid(context);

        renderPlayer(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // background
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, ConfigManager.getConfig().backgroundColor.getValue());

        // map
        renderMap(context, mouseX, mouseY, delta);

        // widgets (buttons, ...)
        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }


    @SuppressWarnings("unused")
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        centerX += deltaX;
        centerZ += deltaY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double change = verticalAmount * 0.1;

        if (0.1 < scale + change && scale + change < 8) {
            scale += change;

            double deltaX = centerX - mouseX,
                    deltaZ = centerZ - mouseY;

            centerX += deltaX * change / scale;
            centerZ += deltaZ * change / scale;
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isLeftClickButton(button)) {
            onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        } else {
            return false;
        }
    }
}
