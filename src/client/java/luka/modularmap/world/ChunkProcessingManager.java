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

package luka.modularmap.world;


import luka.modularmap.ModularMapClient;
import luka.modularmap.map.MapChunk;
import luka.modularmap.map.WorldMap;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkProcessingManager {
    private final ExecutorService _executor;

    public ChunkProcessingManager(int threadCount) {
        assert threadCount > 0;

        _executor = Executors.newFixedThreadPool(threadCount);
    }

    public void addChunkToQueue(@NotNull Chunk chunk, @NotNull WorldMap worldMap) {
        _executor.submit(() -> {
            try {
                var mapChunk = new MapChunk(chunk);

                worldMap.setChunk(mapChunk);

            } catch (Exception e) {
                ModularMapClient.LOGGER.error("Error processing chunk: {}", chunk.getPos(), e);
            }
        });
    }
}
