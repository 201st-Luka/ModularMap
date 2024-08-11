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
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Vector;

@Environment(EnvType.CLIENT)
public class MapScreen extends BaseScreen {
    private static final int GRID_COLOR = 0x40000000;
    private final WorldMap _worldMap;
    private final ClientPlayerEntity _player;
    private int _zoom = 0;
    private double _scale = 1 / Math.pow(2, (double) _zoom / 4);
    private double _shiftX, _shiftZ;
    private boolean _isInitialized = false;
    private double _scrollBuffer = 0;

    public MapScreen() {
        super("Map Screen");

        _player = MinecraftClient.getInstance().player;

        _worldMap = _modularMapClient.modularMap$getWorldMap();
    }

    @Override
    protected void init() {
        if (!_isInitialized) {
            _isInitialized = true;

            _shiftX = (double) (width - FRAME_SPACING * 2 - BUTTON_SIZE) / 2;
            _shiftZ = (double) height / 2;
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
        //todo: add zoom slider
//        SliderWidget zoomSlider = new SliderWidget(
//                FRAME_SPACING, height - FRAME_SPACING - BUTTON_SIZE,
//                width - FRAME_SPACING * 2 - BUTTON_SIZE, BUTTON_SIZE,
//                Text.literal("Zoom: "), Text.literal(""),
//                0, 32, zoom, false, true,
//                slider -> zoom(slider, (double) width / 2, (double) height / 2)
//        );

        addDrawableChild(configButton);
        addDrawableChild(waypointButton);
        addDrawableChild(closeButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert client != null;

        if (KeyInputHandler.openMapKeyBinding.matchesKey(keyCode, scanCode)) {
            _player.closeHandledScreen();
            return true;
        } else if (KeyInputHandler.openWaypointsKeyBinding.matchesKey(keyCode, scanCode)) {
            client.setScreen(new WaypointScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawChunk(@NotNull BufferBuilder buffer,
                           @NotNull Matrix4f transformationMatrix,
                           @Nullable MapChunk chunk) {
        if (chunk != null)
            for (CompressedBlock[] blocks : chunk.getBlocks())
                for (CompressedBlock block : blocks) {
                    int blockColor = block.getColor(),
                            blockX = block.getBlockX(),
                            blockZ = block.getBlockZ();

                    buffer.vertex(transformationMatrix, blockX, blockZ, 0).color(blockColor);
                    buffer.vertex(transformationMatrix, blockX, blockZ + 1, 0).color(blockColor);
                    buffer.vertex(transformationMatrix, blockX + 1, blockZ + 1, 0).color(blockColor);
                    buffer.vertex(transformationMatrix, blockX + 1, blockZ, 0).color(blockColor);
                }
    }

    private void renderChunks(@NotNull DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(_shiftX - _player.getBlockX() * _scale, _shiftZ - _player.getBlockZ() * _scale, 0);
        matrices.scale((float) _scale, (float) _scale, 1);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        var tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // only render chunks that are visible on the map screen
        int chunkStartX = (int) xToBlockX(0) / 16 - 1,
                chunkStartZ = (int) zToBlockZ(0) / 16 - 1,
                chunkEndX = (int) xToBlockX(width) / 16 + 1,
                chunkEndZ = (int) zToBlockZ(height) / 16 + 1;
        Vector<MapChunk> chunks = _worldMap.getChunks(chunkStartX, chunkStartZ, chunkEndX, chunkEndZ);

        for (MapChunk chunk : chunks)
            drawChunk(buffer, transformationMatrix, chunk);

        try {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } catch (IllegalStateException ignored) {
            // buffer is empty, nothing to render/draw
        }

        matrices.pop();
    }

    private void renderVerticalGridLines(@NotNull DrawContext context) {
        int chunkStartX = (int) xToBlockX(0) / 16,
                chunkEndX = (int) xToBlockX(width) / 16 + 2;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(_shiftX, 0, 0);
        matrices.scale((float) _scale, 1, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        var tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // vertical lines
        for (int x = chunkStartX; x <= chunkEndX; x++) {
            buffer.vertex(transformationMatrix, x * 16, 0, 0)
                    .color(GRID_COLOR);
            buffer.vertex(transformationMatrix, x * 16, height + 1, 0)
                    .color(GRID_COLOR);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();
    }

    private void renderHorizontalGridLines(@NotNull DrawContext context) {
        int chunkStartZ = (int) zToBlockZ(0) / 16 - 1,
                chunkEndZ = (int) zToBlockZ(height) / 16;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0, _shiftZ, 0);
        matrices.scale(1, (float) _scale, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        var tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // vertical lines
        for (int z = chunkStartZ; z <= chunkEndZ; z++) {
            buffer.vertex(transformationMatrix, 0, z * 16, 0)
                    .color(GRID_COLOR);
            buffer.vertex(transformationMatrix, width, z * 16, 0)
                    .color(GRID_COLOR);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();
    }

    private void renderGrid(@NotNull DrawContext context) {
        if (_scale >= 0.5) {
            renderVerticalGridLines(context);
            renderHorizontalGridLines(context);
        }
    }

    private void renderPlayer(@NotNull DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(_shiftX, _shiftZ, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(_player.getYaw() + 180));

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        var tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, 0, -5, 0).color(0xFFAA0000);
        buffer.vertex(transformationMatrix, -5, 5, 0).color(0xFFFF6666);
        buffer.vertex(transformationMatrix, 0, 3, 0).color(0xFFAA0000);
        buffer.vertex(transformationMatrix, 5, 5, 0).color(0xFFFF6666);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();
    }

    private void renderMap(@NotNull DrawContext context) {
        renderChunks(context);

        renderGrid(context);

        renderPlayer(context);
    }

    private double xToBlockX(double x) {
        return (x - _shiftX) / _scale + _player.getBlockX();
    }

    private double zToBlockZ(double z) {
        return (z - _shiftZ) / _scale + _player.getBlockZ();
    }

    private void zoom(int amount, double zoomCenterX, double zoomCenterZ) {
        if (-16 < _zoom - amount && _zoom - amount < 16) {
            double blockXBeforeZoom = xToBlockX(zoomCenterX);
            double blockZBeforeZoom = zToBlockZ(zoomCenterZ);

            _zoom -= amount;
            _scale = 1 / Math.pow(2, (double) _zoom / 4);

            double blockXAfterZoom = xToBlockX(zoomCenterX);
            double blockZAfterZoom = zToBlockZ(zoomCenterZ);

            _shiftX += (blockXAfterZoom - blockXBeforeZoom) * _scale;
            _shiftZ += (blockZAfterZoom - blockZBeforeZoom) * _scale;
        }
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, ConfigManager.getConfig().backgroundColor.getValue());

        renderMap(context);

        context.fill(width - FRAME_SPACING * 2 - BUTTON_SIZE, 0, width, height, 0x80000000);

        // widgets (buttons, ...)
        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }

    protected void onDrag(double deltaX, double deltaY) {
        _shiftX += deltaX;
        _shiftZ += deltaY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        _scrollBuffer += verticalAmount;
        int amount = (int) Math.round(_scrollBuffer);
        _scrollBuffer -= amount;

        zoom(amount, mouseX, mouseY);

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isLeftClickButton(button)) {
            onDrag(deltaX, deltaY);
            return true;
        } else {
            return false;
        }
    }
}
