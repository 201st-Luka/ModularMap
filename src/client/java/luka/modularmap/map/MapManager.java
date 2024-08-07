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

import luka.modularmap.ModularMapClient;
import luka.modularmap.world.ChunkProcessingThread;
import luka.modularmap.world.CompressedChunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    //    private final Set<Chunk> loadedChunks = new HashSet<>(), unloadedChunks = new HashSet<>();
    private final Map<ChunkPos, CompressedChunk> chunkMap = new HashMap<>();

    public void loadChunk(Chunk chunk) {
//        if (loadedChunks.add(chunk))
//            chunkMap.put(chunk.getPos(), new CompressedChunk(chunk));
//        unloadedChunks.remove(chunk);
        try {
            ChunkProcessingThread.addChunkToQueue(new ChunkProcessingThread.QueueBundle(chunk, this));
        } catch (InterruptedException e) {
            ModularMapClient.LOGGER.error("Failed to add chunk to queue because the processing thread is " +
                    "interrupted, this should never happen without the game crashing.", e);
        }
    }

    public void unloadChunk(Chunk chunk) {
//        unloadedChunks.add(chunk);
//        loadedChunks.remove(chunk);
    }

//    public Set<Chunk> getLoadedChunks() {
//        return loadedChunks;
//    }
//
//    public Set<Chunk> getUnloadedChunks() {
//        return unloadedChunks;
//    }

    public Map<ChunkPos, CompressedChunk> getChunkMap() {
        return chunkMap;
    }

    public void clear() {
//        loadedChunks.clear();
//        unloadedChunks.clear();
        chunkMap.clear();
    }
}
