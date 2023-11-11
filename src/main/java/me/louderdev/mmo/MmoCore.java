package me.louderdev.mmo;

import lombok.Getter;
import lombok.SneakyThrows;
import me.louderdev.mmo.command.impl.AdminCommand;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.listener.*;
import me.louderdev.mmo.task.UserCachedTask;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.user.listener.UserListener;

import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MmoCore extends JavaPlugin {

    @Getter
    private static MmoCore instance;

    private ConfigFile configFile, dataFile, cachedFile,
    craftFile, fishFile, huntFile, mineFile, plantFile;

    private List<ConfigFile> levelFiles = new ArrayList<>();

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
        this.dataFile = new ConfigFile(this, "data.yml");
        this.cachedFile = new ConfigFile(this, "cached.yml");


        this.craftFile = new ConfigFile(this, "crafts.yml");
        this.fishFile = new ConfigFile(this, "fishs.yml");
        this.huntFile = new ConfigFile(this, "hunts.yml");
        this.mineFile = new ConfigFile(this, "mines.yml");
        this.plantFile = new ConfigFile(this, "plants.yml");

        System.out.println("Creating the levels files");
        levelFiles.add(this.craftFile);
        levelFiles.add(this.fishFile);
        levelFiles.add(this.huntFile);
        levelFiles.add(this.mineFile);
        levelFiles.add(this.plantFile);
    }

    private void loadCommands() {
        new AdminCommand();
    }
    private void loadImportants() {
        Level.init();
    }

    @SneakyThrows
    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new UserListener(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new HuntingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FishingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CraftListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CropsListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MiningListener(cachedFile), this);
    }

    private void loadRunnables() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new UserCachedTask(), 20L, 20 * 60 * 3L);
    }
    public void saveData() {
        for(User user : User.getAllUsers().values()) {
            user.save();
        }

        cachedFile.set("placed_blocks", MiningListener.getPlacedBlock());
        cachedFile.save();
    }

    public void reload() throws IOException, InvalidConfigurationException {
        this.configFile = new ConfigFile(this, "config.yml");
        this.dataFile = new ConfigFile(this, "data.yml");
        this.cachedFile = new ConfigFile(this, "cached.yml");

        this.craftFile = new ConfigFile(this, "crafts.yml");
        this.fishFile = new ConfigFile(this, "fishs.yml");
        this.huntFile = new ConfigFile(this, "hunts.yml");
        this.mineFile = new ConfigFile(this, "mines.yml");
        this.plantFile = new ConfigFile(this, "plants.yml");

        System.out.println("Creating the levels files");
        levelFiles.add(this.craftFile);
        levelFiles.add(this.fishFile);
        levelFiles.add(this.huntFile);
        levelFiles.add(this.mineFile);
        levelFiles.add(this.plantFile);
        Level.init();
        User.updateCached();
    }


}
