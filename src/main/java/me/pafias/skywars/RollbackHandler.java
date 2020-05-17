package me.pafias.skywars;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.*;

public class RollbackHandler {

    public static void delete(String world) {
        for (Player player : Bukkit.getWorld(world).getPlayers())
            player.teleport(SkyWars.getInstance().getLobby());
        String rootDirectory = SkyWars.getInstance().getServer().getWorldContainer().getAbsolutePath();
        File worldFolder = new File(rootDirectory + "/" + world);
        Bukkit.unloadWorld(Bukkit.getWorld(world), false);
        deleteFolder(worldFolder);
        SkyWars.getInstance().getServer().getScheduler().runTaskLater(SkyWars.getInstance(), () -> Bukkit.createWorld(new WorldCreator(world.split("_")[0])), 60);
    }

    public static void copy(String id, String world) {
        for (Player player : Bukkit.getWorld(world).getPlayers())
            player.teleport(SkyWars.getInstance().getLobby());
        String rootDirectory = SkyWars.getInstance().getServer().getWorldContainer().getAbsolutePath();
        File worldFolder = new File(rootDirectory + "/" + world);
        File worldFolderOriginal = new File(rootDirectory + "/" + world + "_" + id);
        Bukkit.unloadWorld(Bukkit.getWorld(world), true);
        try {
            copyFolder(worldFolder, worldFolderOriginal);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.createWorld(new WorldCreator(world + "_" + id));
    }

    private static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] files = src.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    private static void deleteFolder(File src) {
        if (src.isDirectory()) {
            String[] files = src.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(src, file);
                    deleteFolder(srcFile);
                }
                src.delete();
            }
        }
        src.delete();
    }

}