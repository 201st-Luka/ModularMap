package luka.modularmap.map;

import net.minecraft.util.math.ChunkPos;
import java.util.HashSet;
import java.util.Set;

public class ChunkManager {
    private final Set<ChunkPos> loadedChunks = new HashSet<>(), unloadedChunks = new HashSet<>();

    public void loadChunk(ChunkPos chunkPos) {
        loadedChunks.add(chunkPos);
        unloadedChunks.remove(chunkPos);
    }

    public void unloadChunk(ChunkPos chunkPos) {
        unloadedChunks.add(chunkPos);
        loadedChunks.remove(chunkPos);
    }

    public Set<ChunkPos> getLoadedChunks() {
        return loadedChunks;
    }

    public Set<ChunkPos> getUnloadedChunks() {
        return unloadedChunks;
    }

    public void clear() {
        loadedChunks.clear();
        unloadedChunks.clear();
    }
}
