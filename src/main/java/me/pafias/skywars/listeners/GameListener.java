package me.pafias.skywars.listeners;

import me.pafias.skywars.SkyWars;
import me.pafias.skywars.game.Game;
import me.pafias.skywars.game.GameManager;
import me.pafias.skywars.game.GameState;
import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameListener implements Listener {

    int i = 5;

    private void handleDeath(User user, Game game) {
        user.getPlayer().setHealth(20);
        if (user.getPlayer().getKiller() != null) {
            user.getPlayer().teleport(user.getPlayer().getKiller().getLocation());
            Users.getUser(user.getPlayer().getKiller()).addKill();
        } else
            user.getPlayer().teleport(game.getWorld().getSpawnLocation());
        user.getPlayer().setGameMode(GameMode.SPECTATOR);
        user.setTotalDeaths(user.getTotalDeaths() + 1);
        List<User> alive = new ArrayList<>();
        for (User u : game.getPlayers())
            if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                alive.add(u);
        if (alive.size() == 1) {
            User winner = alive.get(0);
            game.broadcast("");
            game.broadcast(ChatColor.GRAY + winner.getPlayer().getName() + ChatColor.GOLD + " won the game!");
            winner.setTotalWins(winner.getTotalWins() + 1);
            game.broadcast("");
            for (User u : game.getPlayers())
                for (PotionEffect pe : u.getPlayer().getActivePotionEffects())
                    u.getPlayer().removePotionEffect(pe.getType());
            game.setGameState(GameState.POSTGAME);
            SkyWars.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(SkyWars.getInstance(),
                    () -> {
                        SkyWars.getInstance().getServer().getScheduler().runTaskLater(SkyWars.getInstance(),
                                () -> spawnFirework(winner), 20);
                        i = i - 1;
                    }, 0, 20);
            SkyWars.getInstance().getServer().getScheduler().runTaskLater(SkyWars.getInstance(), () -> game.stop(), (7 * 20));
        } else if (alive.isEmpty()) {
            game.broadcast("");
            game.broadcast(ChatColor.GOLD + "Nobody won the game.");
            game.broadcast("");
            SkyWars.getInstance().getServer().getScheduler().runTaskLater(SkyWars.getInstance(), () -> game.stop(), (7 * 20));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        User user = Users.getUser(event.getEntity());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            handleDeath(user, game);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = Users.getUser((Player) event.getEntity());
            if (user.isInGame()) {
                Game game = GameManager.getGame(user);
                if (game.getGameState() != GameState.INGAME)
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (user.getPlayer().getLocation().getY() <= game.getConfig().getConfig().getDouble("death-y"))
                handleDeath(user, game);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (game.getGameState() != GameState.INGAME)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (game.getGameState() != GameState.INGAME)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (game.getGameState() != GameState.INGAME)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame()) {
            Game game = GameManager.getGame(user);
            if (game.getGameState() != GameState.INGAME)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = Users.getUser((Player) event.getEntity());
            if (user.isInGame()) {
                Game game = GameManager.getGame(user);
                if (game.getGameState() != GameState.INGAME)
                    event.setCancelled(true);
            }
        }
    }


    private void spawnFirework(User user) {
        final Firework firework = (Firework) user.getPlayer().getLocation().getWorld()
                .spawnEntity(user.getPlayer().getLocation().add(0.5, 0.5, 0.5), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;
        if (rt == 1)
            type = Type.BALL;
        if (rt == 2)
            type = Type.BALL_LARGE;
        if (rt == 3)
            type = Type.BURST;
        if (rt == 4)
            type = Type.CREEPER;
        if (rt == 5)
            type = Type.STAR;
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
                .trail(r.nextBoolean()).build();
        meta.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        meta.setPower(rp);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(SkyWars.getInstance(), firework::detonate, 2);
    }

    private Color getColor(int i) {
        Color c = null;
        if (i == 1) {
            c = Color.AQUA;
        }
        if (i == 2) {
            c = Color.BLACK;
        }
        if (i == 3) {
            c = Color.BLUE;
        }
        if (i == 4) {
            c = Color.FUCHSIA;
        }
        if (i == 5) {
            c = Color.GRAY;
        }
        if (i == 6) {
            c = Color.GREEN;
        }
        if (i == 7) {
            c = Color.LIME;
        }
        if (i == 8) {
            c = Color.MAROON;
        }
        if (i == 9) {
            c = Color.NAVY;
        }
        if (i == 10) {
            c = Color.OLIVE;
        }
        if (i == 11) {
            c = Color.ORANGE;
        }
        if (i == 12) {
            c = Color.PURPLE;
        }
        if (i == 13) {
            c = Color.RED;
        }
        if (i == 14) {
            c = Color.SILVER;
        }
        if (i == 15) {
            c = Color.TEAL;
        }
        if (i == 16) {
            c = Color.WHITE;
        }
        if (i == 17) {
            c = Color.YELLOW;
        }

        return c;
    }
}