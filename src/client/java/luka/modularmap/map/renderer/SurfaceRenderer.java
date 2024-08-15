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

import luka.modularmap.map.MapChunk;
import luka.modularmap.map.MapController;
import luka.modularmap.rendering.PixelRenderingHelper;
import luka.modularmap.world.CompressedBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.Vector;

public class SurfaceRenderer extends AbstractMapRenderer {
    public SurfaceRenderer(MapController map) {
        super(map);
    }

    private void drawChunk(@NotNull PixelRenderingHelper renderer,
                           @Nullable MapChunk chunk) {
        if (chunk != null)
            for (CompressedBlock[] blocks : chunk.getBlocks())
                for (CompressedBlock block : blocks)
                    renderer.drawPixel(block.getBlockX(), block.getBlockZ(), block.getColor());
    }

    @Override
    protected void renderChunks(@NotNull final DrawContext context,
                                @NotNull final Vector2d shift,
                                @NotNull final BlockPos playerPos,
                                final double scale,
                                @NotNull final Vector2i chunkStart, @NotNull final Vector2i chunkEnd) {
        var pixelRenderingHelper = new PixelRenderingHelper(
                context,
                new Vector3d(shift.x - playerPos.getX() * scale, shift.y - playerPos.getZ() * scale, 0),
                new Vector3f((float) scale, (float) scale, 1),
                GameRenderer::getPositionColorProgram,
                new Vector4f(1.0F, 1.0F, 1.0F, 1.0F)
        );

        // only render chunks that are visible on the map screen
        Vector<MapChunk> chunks = _mapController.getChunks(
                chunkStart.x - 1, chunkStart.y - 1,
                chunkEnd.x + 1, chunkEnd.y + 1
        );

        for (MapChunk chunk : chunks)
            drawChunk(pixelRenderingHelper, chunk);

        pixelRenderingHelper.render(BufferRenderer::drawWithGlobalProgram);
    }
}
