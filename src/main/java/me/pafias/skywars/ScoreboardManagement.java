package me.pafias.skywars;

import me.pafias.skywars.game.Game;
import me.pafias.skywars.game.GameManager;
import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManagement {

    public static void setScoreboard(Player player) {
        try {
            User user = Users.getUser(player);
            Game game = GameManager.getGame(user);
            if (user != null && game != null) {
                final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
                final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
                final Objective scoreboardobjective = scoreboard.registerNewObjective("SkyWars", "dummy");
                scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SkyWars");
                scoreboardobjective.getScore("  ").setScore(10);
                scoreboardobjective.getScore(ChatColor.DARK_PURPLE + "Map:").setScore(9);
                scoreboardobjective.getScore(ChatColor.GREEN + game.getWorld().getName().split("_")[0]).setScore(8);
                scoreboardobjective.getScore(" ").setScore(7);
                scoreboardobjective.getScore(ChatColor.DARK_PURPLE + "Players:").setScore(6);
                scoreboardobjective.getScore(ChatColor.GREEN + "" + game.getPlayers().size() + "/" + game.getMaxPlayers()).setScore(5);
                scoreboardobjective.getScore(ChatColor.DARK_PURPLE + "Alive:").setScore(4);
                List<User> alive = new ArrayList<User>();
                for (User u : game.getPlayers())
                    if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                        alive.add(u);
                scoreboardobjective.getScore(ChatColor.GREEN + "" + alive.size() + "/" + game.getPlayers().size()).setScore(3);
                scoreboardobjective.getScore("").setScore(2);
                scoreboardobjective.getScore("not cubecraft.net").setScore(1);
                player.setScoreboard(scoreboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetScoreboard(Player player) {
        try {
            final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
            final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
            final Objective scoreboardobjective = scoreboard.registerNewObjective("SkyWars", "dummy");
            scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SkyWars");
            scoreboardobjective.unregister();
            player.setScoreboard(scoreboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}