package me.pafias.skywars;

import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerlistManagement implements Listener {

    public static void updateTabList(Player player) {
        try {
            User user = Users.getUser(player);
            player.setPlayerListName(user.getPlayer().getName() + ChatColor.YELLOW + " [" + user.getKills() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetTabList(Player player) {
        try {
            player.setPlayerListName(player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        resetTabList(event.getPlayer());
    }

}