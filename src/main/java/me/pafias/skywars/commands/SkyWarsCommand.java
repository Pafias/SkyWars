package me.pafias.skywars.commands;

import me.pafias.skywars.config.UserConfig;
import me.pafias.skywars.game.Game;
import me.pafias.skywars.game.GameManager;
import me.pafias.skywars.usermanagement.User;
import me.pafias.skywars.usermanagement.Users;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyWarsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "------------ SkyWars ------------");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw create <world>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw stats [player]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw games");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw join <game id>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw stop [game id]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw forcestart");
            sender.sendMessage(ChatColor.DARK_AQUA + "/sw leave");
            sender.sendMessage(ChatColor.GOLD + "---------------------------------");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stats")) {
                User user = Users.getUser((Player) sender);
                user.getPlayer().sendMessage(ChatColor.GOLD + "------------ SkyWars ------------");
                user.getPlayer().sendMessage(ChatColor.GOLD + "Your total kills: " + ChatColor.LIGHT_PURPLE + user.getTotalKills());
                user.getPlayer().sendMessage(ChatColor.GOLD + "Your total deaths: " + ChatColor.LIGHT_PURPLE + user.getTotalDeaths());
                user.getPlayer().sendMessage(ChatColor.GOLD + "Your total wins: " + ChatColor.LIGHT_PURPLE + user.getTotalWins());

                user.getPlayer().sendMessage(ChatColor.GOLD + "---------------------------------");
            } else if (args[0].equalsIgnoreCase("games")) {
                if (GameManager.getGames().values().isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No games available at the moment.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "Join a game by clicking on the game you want below or by using /pm join <ID>");
                for (Game game : GameManager.getGames().values())
                    sender.spigot().sendMessage(new ComponentBuilder(ChatColor.GOLD + "ID: " + ChatColor.AQUA + game.getID() + ChatColor.GOLD + " Map: " + ChatColor.DARK_PURPLE + game.getName()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sw join " + game.getID())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "/sw join " + game.getID()).create())).create());
            } else if (args[0].equalsIgnoreCase("forcestart") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(Users.getUser((Player) sender));
                    game.start(true);
                    sender.sendMessage(ChatColor.GREEN + "You force started the game.");
                }
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(user);
                    game.stop();
                    sender.sendMessage(ChatColor.GOLD + "Game stopped.");
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                User user = Users.getUser((Player) sender);
                if (user.isInGame())
                    GameManager.leaveGame(user);
                else
                    sender.sendMessage(ChatColor.RED + "You are not in a game!");
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") && sender.isOp()) {
                String game = GameManager.createGame(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Game with ID " + ChatColor.LIGHT_PURPLE + game + ChatColor.GREEN + " created!");
            } else if (args[0].equalsIgnoreCase("stats")) {
                if (Bukkit.getPlayer(args[1]) != null) {
                    User user = Users.getUser(Bukkit.getPlayer(args[1]));
                    sender.sendMessage(ChatColor.GOLD + "------------ SkyWars ------------");
                    sender.sendMessage(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "'" : "'s") + " total kills: " + ChatColor.LIGHT_PURPLE + user.getTotalKills());
                    sender.sendMessage(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "'" : "'s") + " total deaths: " + ChatColor.LIGHT_PURPLE + user.getTotalDeaths());
                    sender.sendMessage(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "'" : "'s") + " total wins: " + ChatColor.LIGHT_PURPLE + user.getTotalWins());
                    sender.sendMessage(ChatColor.GOLD + "---------------------------------");
                } else {
                    UserConfig config = new UserConfig(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    sender.sendMessage(ChatColor.GRAY + config.getConfig().getString("name") + ChatColor.GOLD + (config.getConfig().getString("name").endsWith("s") ? "'" : "'s") + " total kills: " + ChatColor.LIGHT_PURPLE + config.getConfig().getInt("totalKills"));
                    sender.sendMessage(ChatColor.GRAY + config.getConfig().getString("name") + ChatColor.GOLD + (config.getConfig().getString("name").endsWith("s") ? "'" : "'s") + " total deaths: " + ChatColor.LIGHT_PURPLE + config.getConfig().getInt("totalDeaths"));
                    sender.sendMessage(ChatColor.GRAY + config.getConfig().getString("name") + ChatColor.GOLD + (config.getConfig().getString("name").endsWith("s") ? "'" : "'s") + " total wins: " + ChatColor.LIGHT_PURPLE + config.getConfig().getInt("totalWins"));
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                User user = Users.getUser((Player) sender);
                if (GameManager.getGames().containsKey(args[1]))
                    if (!user.isInGame())
                        GameManager.joinGame(Users.getUser((Player) sender), args[1]);
                    else
                        sender.sendMessage(ChatColor.RED + "You are already in a game!");
                else
                    sender.sendMessage(ChatColor.RED + "Game not found");
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                if (!GameManager.getGames().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "Game not found!");
                    return true;
                }
                Game game = GameManager.getGames().get(args[1]);
                game.stop();
                sender.sendMessage(ChatColor.GOLD + "Game stopped.");
            }
            return true;
        }
        return true;
    }

}
