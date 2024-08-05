package luka.modularmap;

import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.map.ChunkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class ModularMapClient implements ClientModInitializer {
	public static final String MOD_ID = "modularmap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "_client");

	private ChunkManager chunkManager;
    private static ModularMapClient instance;
    private static final Vector<Chunk> chunkCache = new Vector<>();

    public ModularMapClient() {
        instance = this;
    }

	@Override
	public void onInitializeClient() {
		ConfigManager.loadConfig();

		KeyInputHandler.register();
//		ClientChunkEventsHandler.register();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.world != null && chunkManager == null) {
                onWorldJoin(client.world);
            } else if (client.world == null && chunkManager != null) {
                onWorldLeave();
            }
        });

        ClientChunkEvents.CHUNK_LOAD.register(this::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(this::onChunkUnload);

		LOGGER.info("ModularMap clientside initialized!");
	}

	private void onWorldJoin(ClientWorld world) {
        chunkManager = new ChunkManager();
    }

    private void onWorldLeave() {
        chunkManager = null;
    }

    private void onChunkLoad(ClientWorld world, Chunk chunk) {
        if (chunkManager != null) {
            if (!chunkCache.isEmpty()) {
                for (Chunk cachedChunk : chunkCache)
                    chunkManager.loadChunk(cachedChunk.getPos());
                chunkCache.clear();
            }
            chunkManager.loadChunk(chunk.getPos());
        } else {
            chunkCache.add(chunk);
        }
    }

    private void onChunkUnload(ClientWorld world, Chunk chunk) {
        chunkManager.unloadChunk(chunk.getPos());
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public static ModularMapClient getInstance() {
        return instance;
    }
}