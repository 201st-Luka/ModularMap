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

package luka.modularmap.map.renderer;

import luka.modularmap.map.MapController;
import luka.modularmap.rendering.DebugLineRenderingHelper;
import luka.modularmap.rendering.TriangleFanRenderingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public abstract class AbstractMapRenderer {
    protected static final int GRID_COLOR = 0x40000000;
    protected final MapController _mapController;

    public AbstractMapRenderer(MapController map) {
        _mapController = map;
    }

    protected abstract void renderChunks(@NotNull final DrawContext context,
                                         @NotNull final Vector2d shift,
                                         @NotNull final BlockPos playerPos,
                                         final double scale,
                                         @NotNull final Vector2i chunkStart, @NotNull final Vector2i chunkEnd);

    private void renderVerticalGridLines(@NotNull DrawContext context,
                                         @NotNull final Vector2d shift,
                                         final double scale,
                                         @NotNull final Vector2i chunkStart, @NotNull final Vector2i chunkEnd,
                                         @NotNull final BlockPos playerPos,
                                         final int screenHeight) {
        var debugLineRenderingHelper = new DebugLineRenderingHelper(
                context,
                new Vector3d(shift.x, 0, 0),
                new Vector3f((float) scale, 1, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        // vertical lines
        for (int x = chunkStart.x; x <= chunkEnd.x; x++)
            debugLineRenderingHelper.drawLine(x * 16 - playerPos.getX(), 0, x * 16 - playerPos.getX(), screenHeight + 1,
                    GRID_COLOR);


        debugLineRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }

    private void renderHorizontalGridLines(@NotNull final DrawContext context,
                                           @NotNull final Vector2d shift,
                                           final double scale,
                                           @NotNull final Vector2i chunkStart, @NotNull final Vector2i chunkEnd,
                                           @NotNull final BlockPos playerPos,
                                           final int screenWidth) {
        var debugLineRenderingHelper = new DebugLineRenderingHelper(
                context,
                new Vector3d(0, shift.y, 0),
                new Vector3f(1, (float) scale, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        // vertical lines
        for (int z = chunkStart.y; z <= chunkEnd.y; z++)
            debugLineRenderingHelper.drawLine(0, z * 16 - playerPos.getZ(), screenWidth, z * 16 - playerPos.getZ(),
                    GRID_COLOR);

        debugLineRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }

    protected void renderGrid(@NotNull final DrawContext context,
                              @NotNull final Vector2d shift,
                              final double scale,
                              @NotNull final Vector2i chunkStart, @NotNull final Vector2i chunkEnd,
                              @NotNull final BlockPos playerPos,
                              final int screenWidth, final int screenHeight) {
        if (scale >= 0.5) {
            renderHorizontalGridLines(context, shift, scale, chunkStart, chunkEnd, playerPos, screenWidth);
            renderVerticalGridLines(context, shift, scale, chunkStart, chunkEnd, playerPos, screenHeight);
        }
    }

    protected void renderPlayer(@NotNull final DrawContext context,
                                @NotNull final Vector2d shift,
                                @NotNull final ClientPlayerEntity player) {
        var triangleFanRenderingHelper = new TriangleFanRenderingHelper(
                context,
                new Vector3d(shift.x, shift.y, 0),
                RotationAxis.POSITIVE_Z.rotationDegrees(player.getYaw() + 180),
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

    public void render(@NotNull final DrawContext context,
                       @NotNull final ClientPlayerEntity player,
                       @NotNull final Vector2d shift,
                       @NotNull final Vector2i chunkStart, Vector2i chunkEnd,
                       final double scale,
                       final int screenWidth, final int screenHeight) {
        final var playerBlockPos = player.getBlockPos();

        renderChunks(context, shift, playerBlockPos, scale, chunkStart, chunkEnd);

        renderGrid(context, shift, scale, chunkStart, chunkEnd, playerBlockPos, screenWidth, screenHeight);

        renderPlayer(context, shift, player);
    }
}
