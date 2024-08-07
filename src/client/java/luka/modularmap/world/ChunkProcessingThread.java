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
import luka.modularmap.map.MapManager;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChunkProcessingThread extends Thread {
    private static final int QUEUE_CAPACITY = 1000; // you are crazy if you surpass this limit (or you have a very beefy computer)
    private static ChunkProcessingThread INSTANCE;
    private final BlockingQueue<QueueBundle> chunkQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final ThreadPoolExecutor executor;
    private volatile boolean running = true;

    private ChunkProcessingThread(int threadCount) {
        executor = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public static void startProcessing() {
        if (INSTANCE == null) {
            INSTANCE = new ChunkProcessingThread(1);
            INSTANCE.start();
        }
    }

    public static void stopProcessing() {
        if (INSTANCE != null) {
            INSTANCE.running = false;
            INSTANCE.interrupt();
        }
    }

    public static void addChunkToQueue(QueueBundle bundle) throws InterruptedException {
        if (INSTANCE == null) {
            throw new IllegalStateException("Chunk processing thread not started!");
        }
        if (INSTANCE.chunkQueue.size() >= QUEUE_CAPACITY) {
            ModularMapClient.LOGGER.warn("Chunk processing queue is full!");
        }
        INSTANCE.chunkQueue.put(bundle); // This should always be successful and not throw a null pointer exception
    }

    @Override
    public void run() {
        while (running) {
            try {
                QueueBundle bundle = chunkQueue.take();
                executor.execute(() -> processChunk(bundle));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public void processChunk(QueueBundle bundle) {
        Chunk chunk = bundle.chunk;
        MapManager mapManager = bundle.mapManager;
        mapManager.getChunkMap().put(chunk.getPos(), new CompressedChunk(chunk));
    }

    public record QueueBundle(Chunk chunk, MapManager mapManager) {
    }
}
