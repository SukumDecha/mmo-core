package me.louderdev.mmo.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class ServerUtil {

    public ServerUtil() {
        throw new RuntimeException("Cannot init the utils class");
    }

    public static void executeCommand(String cmd) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.getServer().dispatchCommand(console, cmd);
        Bukkit.getConsoleSender().sendMessage("Running command via console: " + cmd);
    }
}
