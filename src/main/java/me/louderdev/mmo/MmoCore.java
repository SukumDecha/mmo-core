package me.louderdev.mmo;

import lombok.Getter;
import lombok.SneakyThrows;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.listener.MiningListener;
import me.louderdev.mmo.user.UserListener;
import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MmoCore extends JavaPlugin {

    @Getter
    private static MmoCore instance;

    private ConfigFile configFile, messageFile, dataFile;

    @Override
    public void onEnable() {
        instance = this;

        loadFiles();
        loadImportants();
        loadListeners();
    }

    @Override
    public void onDisable() {

    }

    @SneakyThrows
    public void loadFiles() {
        this.configFile = new ConfigFile(this, "config.yml");
        this.messageFile = new ConfigFile(this, "message.yml");
    }

    private void loadImportants() {
        Level.init();
    }
    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new UserListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MiningListener(), this);
    }

    public void saveFiles() {
        this.configFile.save();
        this.messageFile.save();
    }

}
