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

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class WorldMap {
    private final java.util.Map<ChunkPos, MapChunk> chunkMap = new ConcurrentHashMap<>();
    private final ChunkProcessingManager chunkProcessingManager;

    public WorldMap() {
        this.chunkProcessingManager = new ChunkProcessingManager(ConfigManager.getConfig().chunkProcessingThreads);
    }

    public void addChunk(Chunk chunk) {
        chunkProcessingManager.addChunkToQueue(chunk, this);
    }

    public MapChunk getChunk(ChunkPos pos) {
        return chunkMap.getOrDefault(pos, null);
    }

    public MapChunk getChunk(int x, int z) {
        return getChunk(new ChunkPos(x, z));
    }

    public Vector<MapChunk> getChunks(ChunkPos start, ChunkPos end) {
        return getChunks(start.x, start.z, end.x - start.x, end.z - start.z);
    }

    public Vector<MapChunk> getChunks(int chunkX, int chunkZ, int width, int height) {
        Vector<MapChunk> chunks = new Vector<>();

        int endX = chunkX + width,
                endZ = chunkZ + height;

        for (int x = chunkX; x < endX; x++)
            for (int z = chunkZ; z < endZ; z++)
                chunks.add(getChunk(new ChunkPos(x, z)));

        return chunks;
    }

    public void setChunk(MapChunk chunk) {
        chunkMap.put(chunk.getChunkPos(), chunk);
    }

    public Set<java.util.Map.Entry<ChunkPos, MapChunk>> getChunkEntries() {
        return chunkMap.entrySet();
    }
}
