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
import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.map.MapChunk;
import luka.modularmap.map.MapController;
import luka.modularmap.rendering.components.DebugLineRenderingHelper;
import luka.modularmap.rendering.components.PixelRenderingHelper;
import luka.modularmap.rendering.components.TriangleFanRenderingHelper;
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
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Vector;

@Environment(EnvType.CLIENT)
public class MapScreen extends BaseScreen {
    private static final int GRID_COLOR = 0x40000000;
    private final MapController _mapController;
    private final ClientPlayerEntity _player;
    private int _zoom = 0;
    private double _scale = 1 / Math.pow(2, (double) _zoom / 4);
    private double _shiftX, _shiftZ;
    private boolean _isInitialized = false;
    private double _scrollBuffer = 0;

    public MapScreen() {
        super("Map Screen");

        _player = MinecraftClient.getInstance().player;

        _mapController = _modularMapClient.modularMap$getMapController();
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

    private void drawChunk(@NotNull PixelRenderingHelper renderer,
                           @Nullable MapChunk chunk) {
        if (chunk != null)
            for (CompressedBlock[] blocks : chunk.getBlocks())
                for (CompressedBlock block : blocks)
                    renderer.drawPixel(block.getBlockX(), block.getBlockZ(), block.getColor());

    }

    private void renderChunks(@NotNull DrawContext context) {
        var pixelRenderingHelper = new PixelRenderingHelper(
                context,
                new Vector3d(_shiftX - _player.getBlockX() * _scale, _shiftZ - _player.getBlockZ() * _scale, 0),
                new Vector3f((float) _scale, (float) _scale, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        // only render chunks that are visible on the map screen
        int chunkStartX = (int) xToBlockX(0) / 16 - 1,
                chunkStartZ = (int) zToBlockZ(0) / 16 - 1,
                chunkEndX = (int) xToBlockX(width) / 16 + 1,
                chunkEndZ = (int) zToBlockZ(height) / 16 + 1;
        Vector<MapChunk> chunks = _mapController.getChunks(chunkStartX, chunkStartZ, chunkEndX, chunkEndZ);

        for (MapChunk chunk : chunks)
            drawChunk(pixelRenderingHelper, chunk);

        pixelRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }

    private void renderVerticalGridLines(@NotNull DrawContext context) {
        var debugLineRenderingHelper = new DebugLineRenderingHelper(
                context,
                new Vector3d(_shiftX, 0, 0),
                new Vector3f((float) _scale, 1, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        int chunkStartX = (int) xToBlockX(0) / 16,
                chunkEndX = (int) xToBlockX(width) / 16 + 2;

        // vertical lines
        for (int x = chunkStartX; x <= chunkEndX; x++)
            debugLineRenderingHelper.drawLine(x * 16 - _player.getBlockX(), 0, x * 16 - _player.getBlockX(), height + 1, GRID_COLOR);


        debugLineRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }

    private void renderHorizontalGridLines(@NotNull DrawContext context) {
        var debugLineRenderingHelper = new DebugLineRenderingHelper(
                context,
                new Vector3d(0, _shiftZ, 0),
                new Vector3f(1, (float) _scale, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        int chunkStartZ = (int) zToBlockZ(0) / 16 - 1,
                chunkEndZ = (int) zToBlockZ(height) / 16 + 1;

        // vertical lines
        for (int z = chunkStartZ; z <= chunkEndZ; z++)
            debugLineRenderingHelper.drawLine(0, z * 16 - _player.getBlockZ(), width, z * 16 - _player.getBlockZ(), GRID_COLOR);

        debugLineRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }

    private void renderGrid(@NotNull DrawContext context) {
        if (_scale >= 0.5) {
            renderVerticalGridLines(context);
            renderHorizontalGridLines(context);
        }
    }

    private void renderPlayer(@NotNull DrawContext context) {
        var triangleFanRenderingHelper = new TriangleFanRenderingHelper(
                context,
                new Vector3d(_shiftX, _shiftZ, 0),
                RotationAxis.POSITIVE_Z.rotationDegrees(_player.getYaw() + 180),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        triangleFanRenderingHelper.drawFirstTriangleFan(
                new Vector3f(0, -5, 0), 0xFFAA0000,
                new Vector3f(-5, 5, 0), 0xFFFF6666,
                new Vector3f(0, 3, 0), 0xFFAA0000
        );
        triangleFanRenderingHelper.drawTriangleFan(new Vector3f(5, 5, 0), 0xFFFF6666);

        triangleFanRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
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
