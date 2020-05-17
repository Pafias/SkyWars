package me.pafias.skywars.config.chests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class NormalChestsConfig {

    public static YamlConfiguration config = null;
    public static File configFile = null;
    File configYml = new File("plugins//SkyWars//normal-chests.yml");
    FileConfiguration configConfig = YamlConfiguration.loadConfiguration(configYml);

    public static void reloadConfig() {
        if (configFile == null) {
            configFile = new File("plugins//SkyWars//normal-chests.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defConfigStream = Bukkit.getPluginManager().getPlugin("SkyWars").getResource("normal-chests.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            if (!configFile.exists() || configFile.length() == 0L) {
                config.setDefaults(defConfig);
            }
        }

    }

    public static FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public static void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config " + configFile, e);
        }
    }

    public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}