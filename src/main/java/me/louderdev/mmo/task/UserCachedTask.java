package me.louderdev.mmo.task;

import me.louderdev.mmo.user.User;
import org.bukkit.Bukkit;

public class UserCachedTask implements Runnable {

    @Override
    public void run() {
        User.getAllUsers().keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }
}
