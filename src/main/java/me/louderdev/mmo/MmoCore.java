package me.louderdev.mmo;

import lombok.Getter;
import lombok.SneakyThrows;
import me.louderdev.mmo.command.impl.AdminCommand;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.listener.*;
import me.louderdev.mmo.task.UserCachedTask;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.user.UserListener;

import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class MmoCore extends JavaPlugin {

    @Getter
    private static MmoCore instance;

    private ConfigFile configFile, messageFile, dataFile;

    @Override
    public void onEnable() {
        instance = this;

        loadFiles();
        loadImportants();
        loadListeners();
        loadCommands();
        loadRunnables();

        for(Player player : Bukkit.getOnlinePlayers()) {
            User user = User.getByUuid(player.getUniqueId());
            user.loadAsync();
        }
    }

    @Override
    public void onDisable() {
        this.saveData();
    }

    @SneakyThrows
    public void loadFiles() {
        this.configFile = new ConfigFile(this, "config.yml");
        this.messageFile = new ConfigFile(this, "message.yml");
        this.dataFile = new ConfigFile(this, "data.yml");
    }

    private void loadCommands() {
        new AdminCommand();
    }
    private void loadImportants() {
        Level.init();
    }
    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new UserListener(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new HuntingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FishingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CraftListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CropsListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MiningListener(), this);
    }

    private void loadRunnables() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new UserCachedTask(), 20L, 20 * 60 * 3L);
    }
    public void saveData() {
        for(User user : User.getAllUsers().values()) {
            user.save();
        }
    }

    public void reload() throws IOException, InvalidConfigurationException {
        this.configFile = new ConfigFile(this, "config.yml");
        this.messageFile = new ConfigFile(this, "message.yml");
        this.dataFile = new ConfigFile(this, "data.yml");

        Level.init();
        User.updateCached();
    }


}
