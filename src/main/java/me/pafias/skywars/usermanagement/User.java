package me.pafias.skywars.usermanagement;

import me.pafias.skywars.config.UserConfig;
import org.bukkit.entity.Player;

public class User {

    private Player player;
    private UserConfig config;
    private int kills;
    private int totalKills;
    private int totalDeaths;
    private int totalWins;
    private boolean inGame;

    public User(Player player) {
        this.player = player;
        this.config = new UserConfig(player.getUniqueId());
        this.kills = 0;
        this.totalKills = config.getConfig().getInt("totalKills");
        this.totalDeaths = config.getConfig().getInt("totalDeaths");
        this.totalWins = config.getConfig().getInt("totalWins");
    }

    public Player getPlayer() {
        return player;
    }

    public UserConfig getConfig() {
        return config;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        setKills(getKills() + 1);
        setTotalKills(getTotalKills() + 1);
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
        config.getConfig().set("totalKills", totalKills);
        config.saveConfig();
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
        config.getConfig().set("totalDeaths", totalKills);
        config.saveConfig();
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
        config.getConfig().set("totalWins", totalKills);
        config.saveConfig();
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

}
