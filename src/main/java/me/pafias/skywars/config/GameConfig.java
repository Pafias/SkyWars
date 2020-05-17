package me.pafias.skywars.config;

import me.pafias.skywars.SkyWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class GameConfig {

    String world;
    File file;
    FileConfiguration config;

    public GameConfig(String world) {
        this.world = world;
        this.file = new File(SkyWars.getInstance().getDataFolder() + "//gamedata//", world + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void createConfig(Player player) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("name", world);
                config.save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
