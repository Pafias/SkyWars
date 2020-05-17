package me.pafias.skywars.listeners;

import me.pafias.skywars.config.UserConfig;
import me.pafias.skywars.game.GameManager;
import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UserConfig config = new UserConfig(event.getPlayer().getUniqueId());
        config.createConfig(event.getPlayer());
        config.getConfig().set("name", event.getPlayer().getName());
        config.saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Users.addUser(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame())
            GameManager.leaveGame(user);
        Users.removeUser(event.getPlayer());
    }

}
