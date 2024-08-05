package luka.modularmap.event;

import luka.modularmap.map.ChunkManager;
import luka.modularmap.util.IModularMapClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;

public class ClientChunkEventsHandler {
    public static void onChunkLoad(ClientWorld world, Chunk chunk) {
        IModularMapClient client = (IModularMapClient) MinecraftClient.getInstance();
        ChunkManager chunkManager = client.modularMap$getChunkManager();
        chunkManager.loadChunk(chunk.getPos());
    }

    public static void onChunkUnload(ClientWorld world, Chunk chunk) {
        IModularMapClient client = (IModularMapClient) MinecraftClient.getInstance();
        ChunkManager chunkManager = client.modularMap$getChunkManager();
        chunkManager.unloadChunk(chunk.getPos());
    }

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(ClientChunkEventsHandler::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(ClientChunkEventsHandler::onChunkUnload);
    }
}
