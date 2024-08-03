package luka.modularmap;

import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModularMapClient implements ClientModInitializer {
	public static final String MOD_ID = "modularmap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "_client");

	@Override
	public void onInitializeClient() {
		ConfigManager.loadConfig();
		KeyInputHandler.register();

		LOGGER.info("ModularMap clientside initialized!");
	}
}