package me.louderdev.mmo.user;

import lombok.Getter;
import lombok.Setter;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.utils.TaskUtils;
import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class User {

    //All user in cached should be online;
    @Getter private static Map<UUID, User> allUsers = new HashMap<>();
    private final ConfigFile data = MmoCore.getInstance().getDataFile();

    private String name;
    private UUID uuid;
    private Level lastestLevel;
    private List<Level> allLevels = new ArrayList<>();
    public User(UUID uuid) {
        this.name = Bukkit.getPlayer(uuid).getName();
        this.uuid = uuid;

        allUsers.put(uuid, this);
    }

    public User(String name) {
        this.name = name;
        this.uuid = Bukkit.getOfflinePlayer(name).getUniqueId();

        allUsers.put(uuid, this);
    }

    public void loadAsync() {
        TaskUtils.runAsync(MmoCore.getInstance(), () -> {
            ConfigurationSection keySection = data.getConfigurationSection("players." + uuid.toString());

            if (keySection == null) {
                this.saveAsync();
                return;
            }

            for (String key : keySection.getKeys(false)) {
                ConfigurationSection section = keySection.getConfigurationSection(key);

                Level level = new Level(key);
                level.setCurrentLevel(section.getDouble("CURRENT_LEVEL"));
                level.setCurrentXP(section.getDouble("CURRENT_XP"));
                level.setCurrentMaxXP(section.getDouble("CURRENT_MAX_XP"));
                allLevels.add(level);
            }
        });
    }

    public void saveAsync() {
        TaskUtils.runAsync(MmoCore.getInstance(), () -> {
            ConfigurationSection section = data.getConfigurationSection("players");
            ConfigurationSection uuidSection = section.createSection(uuid.toString());
            for(Level level : getAllLevels()) {
                ConfigurationSection levelSection = uuidSection.createSection(level.getKeyName());

                levelSection.set("DISPLAY_NAME", level.getDisplayName());

                ConfigurationSection expSection = levelSection.createSection("EXP");
                expSection.set("CURRENT_LEVEL", level.getCurrentLevel());
                expSection.set("CURRENT_XP", level.getCurrentXP());
                expSection.set("CURRENT_MAX_XP", level.getCurrentMaxXP());
            }
        });

        data.save();
        data.reload();
    }
    public static User getByUuid(UUID uuid) {

        return allUsers.computeIfAbsent(uuid, User::new);
    }

    public static User getByName(String name) {
        Player player = Bukkit.getPlayer(name);

        return player == null ? new User(name) : allUsers.get(player.getUniqueId());
    }

    public List<Level> getLevelByAction(ActionType type) {
        List<Level> toReturn = new ArrayList<>();

        for(Level level : allLevels) {
            if(level.getActionType() == type) {
                toReturn.add(level);
            }
        }

        return toReturn;
    }
}
