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

package luka.modularmap.map;

import luka.modularmap.world.CompressedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class MapChunk {
    public static final byte CHUNK_SIZE = 16;
    private final CompressedBlock[][] blocks = new CompressedBlock[CHUNK_SIZE][CHUNK_SIZE];
    private final ChunkPos chunkPos;

    public MapChunk(Chunk chunk) {
        chunkPos = chunk.getPos();

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                for (int y = chunk.getTopY() - 1; y >= chunk.getBottomY(); y--) {
                    BlockPos pos = new BlockPos(chunkPos.x * 16 + x, y, chunkPos.z * 16 + z);

                    BlockState blockState = chunk.getBlockState(pos);

                    if (!blockState.isAir() && (blockState.isOpaque() || blockState.getFluidState().getFluid() != null)) {
                        blocks[x][z] = new CompressedBlock(blockState.getBlock(), pos);
                        break;
                    }
                }
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public CompressedBlock[][] getBlocks() {
        return blocks;
    }
}

