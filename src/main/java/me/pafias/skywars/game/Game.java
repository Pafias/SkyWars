package me.pafias.skywars.game;

import me.pafias.skywars.RollbackHandler;
import me.pafias.skywars.ScoreboardManagement;
import me.pafias.skywars.SkyWars;
import me.pafias.skywars.config.GameConfig;
import me.pafias.skywars.config.chests.BasicChestsConfig;
import me.pafias.skywars.config.chests.NormalChestsConfig;
import me.pafias.skywars.config.chests.OpChestsConfig;
import me.pafias.skywars.usermanagement.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class Game {

    public boolean started;
    int time;
    int taskID;
    private BukkitScheduler scheduler = SkyWars.getInstance().getServer().getScheduler();
    private SkyWars instance = SkyWars.getInstance();
    private String id;
    private GameConfig config;
    private String name;
    private World world;
    private GameState state;
    private int minPlayers;
    private int maxPlayers;
    private List<User> players;
    private List<Location> spawnpoints;
    private ChestType chesttype;

    public Game(String id, World world) {
        this.id = id;
        this.config = new GameConfig(world.getName().split("_")[0]);
        this.name = config.getConfig().getString("name");
        this.world = world;
        this.state = GameState.LOBBY;
        this.minPlayers = config.getConfig().getInt("minPlayers");
        this.maxPlayers = config.getConfig().getInt("maxPlayers");
        this.players = new ArrayList<>();
        this.spawnpoints = loadSpawnpoints();
        this.chesttype = ChestType.OVERPOWERED;
    }

    public void start(boolean force) {
        if (!started) {
            if (players.size() >= minPlayers || force) {
                setGameState(GameState.PREGAME);
                time = 30;
                taskID = scheduler.scheduleSyncRepeatingTask(instance, () -> {
                    if (time == 0) {
                        scheduler.cancelTask(taskID);
                        setGameState(GameState.INGAME);
                        broadcast(ChatColor.GOLD + "The game has started!");
                        handleChests();
                        handleCages();
                        for (User u : players) {
                            u.getPlayer().playSound(u.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                            u.getPlayer().setLevel(0);
                            u.getPlayer().setExp(0);
                            u.getPlayer().setGameMode(GameMode.SURVIVAL);
                            u.getPlayer().setHealth(u.getPlayer().getMaxHealth());
                            u.getPlayer().setFoodLevel(20);
                        }
                        scheduler.runTaskLater(instance, () -> {
                            for (User u : players)
                                u.getPlayer().setInvulnerable(false);
                        }, (5 * 20));
                    }
                    for (User u : players) {
                        u.getPlayer().setLevel(time);
                        u.getPlayer().setExp(time / (float) 30);
                    }
                    if (time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                        for (User u : players)
                            u.getPlayer().playSound(u.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                        broadcast(ChatColor.RED + "The game will start in " + ChatColor.GRAY + time + ChatColor.RED + " seconds.");
                    }
                    time--;
                }, 2, 20);
            }
        }
    }

    private void handleCages() {
        for (User u : getPlayers()) {
            Location loc = u.getPlayer().getLocation();
            int r = 4;
            for (int x = (r * -1); x <= r; x++) {
                for (int y = (r * -1); y <= r; y++) {
                    for (int z = (r * -1); z <= r; z++) {
                        Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y,
                                loc.getBlockZ() + z);
                        if (b.getType() == Material.GLASS || b.getType() == Material.BARRIER)
                            b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void handleChests() {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState entities : chunk.getTileEntities()) {
                if (entities instanceof Chest) {
                    Inventory inv = ((Chest) entities).getInventory();
                    fillChests(inv);
                }
            }
        }
    }

    private void fillChests(Inventory inv) {
        inv.clear();
        Map<ItemStack, Double> items = new HashMap<>();
        FileConfiguration config;
        Set<String> list;
        switch (chesttype) {
            case BASIC:
                config = BasicChestsConfig.getConfig();
                list = BasicChestsConfig.getConfig().getConfigurationSection("items").getKeys(false);
                break;
            case OVERPOWERED:
                config = OpChestsConfig.getConfig();
                list = OpChestsConfig.getConfig().getConfigurationSection("items").getKeys(false);
                break;
            default:
                config = NormalChestsConfig.getConfig();
                list = NormalChestsConfig.getConfig().getConfigurationSection("items").getKeys(false);
                break;
        }
        for (String s : list) {
            double chance = config.getInt("items." + s + ".chance");
            int amount;
            String r;
            try {
                amount = Integer.parseInt(config.getString("items." + s + ".amount"));
            } catch (NumberFormatException ex) {
                r = config.getString("items." + s + ".amount");
                int low = Integer.parseInt(r.split("-")[0]);
                int high = Integer.parseInt(r.split("-")[1]);
                amount = new Random().nextInt(high - low) + low;
            }
            ItemStack item = new ItemStack(Material.valueOf(s.toUpperCase()), amount);
            ItemMeta meta = item.getItemMeta();
            if (config.getConfigurationSection("items." + s + ".enchantments") != null && config.getConfigurationSection("items." + s + ".enchantments").getKeys(false) != null && !config.getConfigurationSection("items." + s + ".enchantments").getKeys(false).isEmpty()) {
                for (String es : config.getConfigurationSection("items." + s + ".enchantments").getKeys(false)) {
                    if (config.getString("items." + s + ".enchantments." + es + ".chance") != null && new Random().nextInt(100) < config.getInt("items." + s + ".enchantments." + es + ".chance")) {
                        Enchantment enchantment = Enchantment.getByName(es);
                        int level;
                        String l;
                        try {
                            level = Integer.parseInt(config.getString("items." + s + ".enchantments." + es + ".level"));
                        } catch (NumberFormatException ex) {
                            l = config.getString("items." + s + ".enchantments." + es + ".level");
                            int low = Integer.parseInt(l.split("-")[0]);
                            int high = Integer.parseInt(l.split("-")[1]);
                            level = new Random().nextInt(high - low) + low;
                        }
                        meta.addEnchant(enchantment, level, true);
                    }
                }
            }
            item.setItemMeta(meta);
            if (!items.containsKey(item))
                items.put(item, chance);
        }
        for (int i = 0; i < (inv.getSize() / 2); i++) {
            Random random = new Random();
            List<ItemStack> itemsA = new ArrayList<>(items.keySet());
            ItemStack item = itemsA.get(random.nextInt(itemsA.size()));
            if (random.nextInt(100) < items.get(item)) {
                int slot = random.nextInt(inv.getSize());
                inv.setItem(slot, item);
            }
        }
    }

    public void handleTeleport(User user) {
        List<Location> list = spawnpoints;
        for (User u : players) {
            int i = new Random().nextInt(list.size());
            if (Math.round(list.get(i).getX()) == Math.round(u.getPlayer().getLocation().getX()) && Math.round(list.get(i).getZ()) == Math.round(u.getPlayer().getLocation().getZ()))
                list.remove(i);
        }
        user.getPlayer().teleport(list.get(new Random().nextInt(list.size())));
    }

    private List<Location> loadSpawnpoints() {
        List<Location> list = new ArrayList<>();
        for (String s : config.getConfig().getStringList("spawnpoints")) {
            double x = Double.parseDouble(s.split(",")[0]);
            double y = Double.parseDouble(s.split(",")[1]);
            double z = Double.parseDouble(s.split(",")[2]);
            float yaw = Float.parseFloat(s.split(",")[3]);
            float pitch = Float.parseFloat(s.split(",")[4]);
            Location loc = new Location(world, x, y, z, yaw, pitch);
            list.add(loc);
        }
        return list;
    }

    public void stop() {
        setGameState(GameState.POSTGAME);
        for (User user : players) {
            user.getPlayer().getInventory().clear();
            user.getPlayer().setExp(0);
            user.getPlayer().setLevel(0);
            user.getPlayer().setGameMode(GameMode.SURVIVAL);
            user.getPlayer().setInvulnerable(false);
            user.setInGame(false);
            user.getPlayer().showPlayer(user.getPlayer());
            user.getPlayer().teleport(SkyWars.getInstance().getLobby());
            ScoreboardManagement.resetScoreboard(user.getPlayer());
            user.getPlayer().setPlayerListName(user.getPlayer().getDisplayName());
        }
        world.getEntities().clear();
        Bukkit.getScheduler().cancelTasks(SkyWars.getInstance());
        scheduler.runTaskLater(instance, () -> RollbackHandler.delete(world.getName()), 60);
        GameManager.removeGame(this);
    }

    public void broadcast(String message) {
        for (User u : players)
            u.getPlayer().sendMessage(message);
    }

    public String getID() {
        return id;
    }

    public GameConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public List<Location> getSpawnpoints() {
        return spawnpoints;
    }

    public ChestType getChestType() {
        return chesttype;
    }

    public void setChestType(ChestType chesttype) {
        this.chesttype = chesttype;
    }

}
