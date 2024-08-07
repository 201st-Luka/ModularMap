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

package luka.modularmap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import luka.modularmap.ModularMapClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final String CONFIG_FOLDER = "config",
            MOD_CONFIG_FOLDER = ModularMapClient.MOD_ID,
            MOD_CONFIG_PATH = CONFIG_FOLDER + "/" + MOD_CONFIG_FOLDER,
            CONFIG_FILE_NAME = ModularMapClient.MOD_ID + "_client.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final File configFile = new File(MOD_CONFIG_PATH + "/" + CONFIG_FILE_NAME);
    private static ModularMapConfig config;

    public static void loadConfig() {
        ModularMapClient.LOGGER.debug("Loading config file: {} ...", configFile.getPath());

        if (!configFile.exists()) {
            config = new ModularMapConfig();
            saveConfig();

        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, ModularMapConfig.class);

            } catch (IOException e) {
                ModularMapClient.LOGGER.error("Failed to load config file: {}", configFile.getAbsolutePath(), e);
            }
        }

        ModularMapClient.LOGGER.info("Config loaded.");
    }

    public static void saveConfig() {
        ModularMapClient.LOGGER.debug("Saving config file: {} ...", configFile.getPath());

        new File(MOD_CONFIG_PATH).mkdirs();

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);

        } catch (IOException e) {
            ModularMapClient.LOGGER.error("Failed to save config file: {}", configFile.getAbsolutePath(), e);
        }

        ModularMapClient.LOGGER.info("Config saved.");
    }

    public static ModularMapConfig getConfig() {
        return config;
    }

    public static void updateConfig(ModularMapConfig config) {
        if (ConfigManager.config == config)
            ModularMapClient.LOGGER.warn("Config is the same object as the current config.");

        ConfigManager.config = config;
        saveConfig();
    }


}
