package me.louderdev.mmo.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;
import me.louderdev.mmo.*;
public class TaskUtils {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(MmoCore plugin, Callable callable) {
        plugin.getServer().getScheduler().runTask(plugin, callable::call);
    }

    public static void runAsync(MmoCore plugin, Callable callable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, callable::call);
    }

    public static void runLater(MmoCore plugin, Callable callable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, callable::call, delay);
    }

    public static void runAsyncLater(MmoCore plugin, Callable callable, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, callable::call, delay);
    }

    public static void runTimer(MmoCore plugin, Callable callable, long delay, long interval) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, callable::call, delay, interval);
    }

    public static void runAsyncTimer(MmoCore plugin, Callable callable, long delay, long interval) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }

}