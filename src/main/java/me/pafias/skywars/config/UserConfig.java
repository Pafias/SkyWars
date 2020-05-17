package me.pafias.skywars.config;

import me.pafias.skywars.SkyWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserConfig {

    UUID uuid;
    File file;
    FileConfiguration config;

    public UserConfig(UUID uuid) {
        this.uuid = uuid;
        this.file = new File(SkyWars.getInstance().getDataFolder() + "//playerdata//", uuid.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void createConfig(Player player) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("name", player.getName());
                config.set("totalKills", 0);
                config.set("totalDeaths", 0);
                config.set("totalWins", 0);
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
