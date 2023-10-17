package me.louderdev.mmo.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;
import me.louderdev.mmo.*;
public class TaskUtils {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Callable callable) {
        MmoCore.getInstance().getServer().getScheduler().runTask(MmoCore.getInstance(), callable::call);
    }

    public static void runAsync( Callable callable) {
        MmoCore.getInstance().getServer().getScheduler().runTaskAsynchronously(MmoCore.getInstance(), callable::call);
    }

    public static void runLater( Callable callable, long delay) {
        MmoCore.getInstance().getServer().getScheduler().runTaskLater(MmoCore.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater( Callable callable, long delay) {
        MmoCore.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(MmoCore.getInstance(), callable::call, delay);
    }

    public static void runTimer( Callable callable, long delay, long interval) {
        MmoCore.getInstance().getServer().getScheduler().runTaskTimer(MmoCore.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer( Callable callable, long delay, long interval) {
        MmoCore.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(MmoCore.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }

}