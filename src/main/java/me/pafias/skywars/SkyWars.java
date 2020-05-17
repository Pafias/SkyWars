package me.pafias.skywars;

import me.pafias.skywars.commands.SkyWarsCommand;
import me.pafias.skywars.listeners.GameListener;
import me.pafias.skywars.listeners.JoinAndQuitListener;
import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyWars extends JavaPlugin {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static SkyWars instance;

    public static SkyWars getInstance() {
        return instance;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    public void onEnable() {
        instance = this;
        for (Player p : getServer().getOnlinePlayers())
            Users.addUser(p);
        registerCommands();
        registerListeners();
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                User user = Users.getUser(p);
                if (user.isInGame()) {
                    PlayerlistManagement.updateTabList(user.getPlayer());
                    getServer().getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), () -> {
                        ScoreboardManagement.setScoreboard(user.getPlayer());
                    }, 2);
                }
            }
        }, 20, 100);
    }

    private void registerCommands() {
        getCommand("skywars").setExecutor(new SkyWarsCommand());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinAndQuitListener(), this);
        pm.registerEvents(new GameListener(), this);
        pm.registerEvents(new PlayerlistManagement(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public Location getLobby() {
        return new Location(getServer().getWorld(getConfig().getString("lobby.world")), getConfig().getDouble("lobby.x"), getConfig().getDouble("lobby.y"), getConfig().getDouble("lobby.z"), (float) getConfig().getDouble("lobby.yaw"), (float) getConfig().getDouble("lobby.pitch"));
    }
}
