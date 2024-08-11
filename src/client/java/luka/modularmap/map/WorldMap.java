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

import luka.modularmap.config.ConfigManager;
import luka.modularmap.world.ChunkProcessingManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class WorldMap {
    private final java.util.Map<ChunkPos, MapChunk> _chunkMap = new ConcurrentHashMap<>();
    private final ChunkProcessingManager _chunkProcessingManager;

    public WorldMap() {
        _chunkProcessingManager = new ChunkProcessingManager(ConfigManager.getConfig().chunkProcessingThreads);
    }

    public void addChunk(@NotNull Chunk chunk) {
        _chunkProcessingManager.addChunkToQueue(chunk, this);
    }

    public MapChunk getChunk(@NotNull ChunkPos pos) {
        return _chunkMap.getOrDefault(pos, null);
    }

    public MapChunk getChunk(int x, int z) {
        return getChunk(new ChunkPos(x, z));
    }

    public Vector<MapChunk> getChunks(@NotNull ChunkPos start, @NotNull ChunkPos end) {
        return getChunks(start.x, start.z, end.x, end.z);
    }

    public Vector<MapChunk> getChunks(int chunkStartX, int chunkStartZ, int chunkEndX, int chunkEndZ) {
        var chunks = new Vector<MapChunk>();

        for (int x = chunkStartX; x < chunkEndX; x++)
            for (int z = chunkStartZ; z < chunkEndZ; z++)
                chunks.add(getChunk(x, z));

        return chunks;
    }

    public void setChunk(MapChunk chunk) {
        _chunkMap.put(chunk.getChunkPos(), chunk);
    }

    public Set<java.util.Map.Entry<ChunkPos, MapChunk>> getChunkEntries() {
        return _chunkMap.entrySet();
    }
}
