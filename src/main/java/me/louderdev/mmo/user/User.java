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
    private static ConfigFile data = MmoCore.getInstance().getDataFile();

    private String name;
    private UUID uuid;
    private Level lastestLevel;
    private List<Level> allLevels = new ArrayList<>();
    private List<String> ownLevels = new ArrayList<>();
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
        this.allLevels = new ArrayList<>();
        this.ownLevels = new ArrayList<>();

        TaskUtils.runAsync(() -> {
            ConfigurationSection keySection = data.getConfigurationSection("players." + name);

            if (keySection == null) {
                fillOthersLevel();
                this.saveAsync();
                return;
            }

            System.out.println("Loading User");

            for (String key : keySection.getKeys(false)) {
                System.out.println("Key: " + key);
                ConfigurationSection section = keySection.getConfigurationSection(key);

                if(Level.getByKeyName(key) == null) {
                    data.set(section.getCurrentPath(), null);
                    continue;
                }

                Level level = new Level(key);

                level.setCurrentLevel(section.getInt("CURRENT_LEVEL"));
                level.setCurrentXP(section.getInt("CURRENT_XP"));
                level.setCurrentMaxXP(section.getInt("CURRENT_MAX_XP"));

                ownLevels.add(key);
                allLevels.add(level);
            }

            fillOthersLevel();

        });
    }

    public void save() {
        ConfigurationSection section = data.getConfigurationSection("players");

        ConfigurationSection nameSection = section.createSection(name);
        for(Level level : allLevels) {
            ConfigurationSection levelSection = nameSection.createSection(level.getKeyName());

            levelSection.set("CURRENT_LEVEL", level.getCurrentLevel());
            levelSection.set("CURRENT_XP", level.getCurrentXP());
            levelSection.set("CURRENT_MAX_XP", level.getCurrentMaxXP());
        }

        data.save();
        data.reload();
    }
    public void saveAsync() {
        TaskUtils.runAsync(() -> {
            save();
        });
    }

    private void fillOthersLevel() {
        List<Level> toAdd = new ArrayList<>();

        if(allLevels.size() > 0) {
            for(Level others : Level.getAllLevels()) {
                if(!ownLevels.contains(others.getKeyName())) {
                    toAdd.add(new Level(others.getKeyName()));
                }
            }
        } else {
            for(Level others : Level.getAllLevels()) {
                toAdd.add(new Level(others.getKeyName()));
            }
        }

        System.out.println("toAdd:" + toAdd);

        allLevels.addAll(toAdd);

    }
    public static User getByUuid(UUID uuid) {
        User toReturn = allUsers.get(uuid);

        return toReturn == null ? new User(uuid) : toReturn;
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

    public Level getLevelByName(String keyName) {

        for(Level level : allLevels) {
            if(level.getKeyName().equals(keyName)) {
                return level;
            }
        }

        return null;
    }
    public static void updateCached() {
        for(User user : allUsers.values()) {
            user.loadAsync();
        }

        data = MmoCore.getInstance().getDataFile();
    }
}
