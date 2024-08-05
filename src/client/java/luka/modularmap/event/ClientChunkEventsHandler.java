package luka.modularmap.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;

public class ClientChunkEventsHandler {
    public static void onChunkLoad(ClientWorld world, Chunk chunk) {
        // Handle chunk load
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        System.out.println("Chunk loaded at: " + chunkX + ", " + chunkZ);
    }

    public static void onChunkUnload(ClientWorld world, Chunk chunk) {
        // Handle chunk unload
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        System.out.println("Chunk unloaded at: " + chunkX + ", " + chunkZ);
    }

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(ClientChunkEventsHandler::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(ClientChunkEventsHandler::onChunkUnload);
    }
}
