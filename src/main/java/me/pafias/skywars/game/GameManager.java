package me.pafias.skywars.game;

import me.pafias.skywars.RollbackHandler;
import me.pafias.skywars.ScoreboardManagement;
import me.pafias.skywars.SkyWars;
import me.pafias.skywars.usermanagement.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static Map<String, Game> games = new HashMap<>();

    public static Map<String, Game> getGames() {
        return games;
    }

    public static Game getGame(User user) {
        for (Game game : games.values())
            if (game.getPlayers().contains(user))
                return game;
        return null;
    }

    public static String createGame(String world) {
        String id = SkyWars.randomAlphaNumeric(5);
        RollbackHandler.copy(id, world);
        games.put(id, new Game(id, Bukkit.getWorld(world + "_" + id)));
        return id;
    }

    public static void joinGame(User user, String id) {
        Game game = games.get(id);
        if (game.getGameState() == GameState.INGAME || game.getGameState() == GameState.POSTGAME) {
            user.getPlayer().sendMessage(ChatColor.RED + "This game has already started.");
            return;
        }
        if (game.getPlayers().size() >= game.getMaxPlayers()) {
            user.getPlayer().sendMessage(ChatColor.RED + "This game is full.");
            return;
        }
        game.getPlayers().add(user);
        user.setInGame(true);
        game.handleTeleport(user);
        user.getPlayer().getInventory().clear();
        user.getPlayer().setGameMode(GameMode.SURVIVAL);
        user.getPlayer().setFoodLevel(20);
        user.getPlayer().setHealth(20);
        user.getPlayer().setInvulnerable(true);
        for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
            user.getPlayer().removePotionEffect(pe.getType());
        game.broadcast(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + " joined the game! " + ChatColor.GREEN + game.getPlayers().size() + "/" + game.getMaxPlayers());
        game.start(false);
    }

    public static void leaveGame(User user) {
        Game game = getGame(user);
        game.getPlayers().remove(user);
        user.setInGame(false);
        user.getPlayer().getInventory().clear();
        user.getPlayer().setExp(0);
        user.getPlayer().setLevel(0);
        user.getPlayer().setGameMode(GameMode.SURVIVAL);
        user.getPlayer().setInvulnerable(false);
        user.getPlayer().showPlayer(user.getPlayer());
        for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
            user.getPlayer().removePotionEffect(pe.getType());
        ScoreboardManagement.resetScoreboard(user.getPlayer());
        user.getPlayer().setPlayerListName(user.getPlayer().getDisplayName());
        user.getPlayer().teleport(SkyWars.getInstance().getLobby());
        game.broadcast(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + " left the game! " + (!game.started ? game.getPlayers().size() + "/" + game.getMaxPlayers() : ""));
        game.start(false);
    }

    public static void removeGame(Game game) {
        games.remove(game.getID());
    }

}