package me.louderdev.mmo.level;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.utils.Msg;
import me.louderdev.mmo.utils.ServerUtil;
import me.louderdev.mmo.utils.TaskUtils;
import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class Level {

    private int currentLevel, currentXP, currentMaxXP, minGainXP, maxGainXP; //min-max exps
    private String displayName, keyName, beginItem; //Name of this type of levels;
    private Sound soundFail;
    private ActionType actionType; //Action type of this level
    private List<LevelProps> levelProps; //levelProps
    private List<RewardProps> rewardProps;
    private Map<String, String> fromOthers;

    private double x, y;
    @Getter private static List<Level> allLevels = new ArrayList<>();;

    @Getter private static Map<String, String> duplicateLevel = new HashMap<>();

    private static int maxLevel;

    public Level(String keyName, FileConfiguration configuration) {
        this.keyName = keyName;
        this.rewardProps = new ArrayList<>();
        this.levelProps = new ArrayList<>();
        this.fromOthers = new HashMap<>();

        this.load(configuration);
    }

    public Level(String otherKeyName) {
        Level others = getByKeyName(otherKeyName);

        if (others != null) {

            this.keyName = others.getKeyName();
            this.displayName = others.getDisplayName();
            this.soundFail = others.getSoundFail();
            this.beginItem = others.getBeginItem();
            this.actionType = others.getActionType();

            this.minGainXP = others.getMinGainXP();
            this.maxGainXP = others.getMaxGainXP();

            this.currentLevel = 1;
            this.currentMaxXP = 45;

            this.x = others.getX();
            this.y = others.getY();

            this.rewardProps = others.getRewardProps();
            this.levelProps = others.getLevelProps();
            this.fromOthers = others.getFromOthers();
        } else {
            System.out.println("Could not find level with that keyname (" +
                    otherKeyName + ")");
        }
    }

    @SneakyThrows
    public static void init() {
        allLevels = new ArrayList<>();
        maxLevel = MmoCore.getInstance().getConfigFile().getInt("CONFIG.HIGHEST_LEVEL");

        for(ConfigFile config : MmoCore.getInstance().getLevelFiles()) {
            //ConfigFile config = new ConfigFile(MmoCore.getInstance(), type);
            ConfigurationSection section = config.getConfigurationSection("MMOCORE");

            for(String key : section.getKeys(false)) {
                Level toAdd = new Level(key, config);
                allLevels.add(toAdd);
            }
        }
    }

    private void load(FileConfiguration c) {
        ConfigFile mainConfig = MmoCore.getInstance().getConfigFile();;

        System.out.println("Loading " + keyName);
        ConfigurationSection section = c.getConfigurationSection("MMOCORE." + keyName);

        this.displayName = section.getString("DISPLAY_NAME");
        this.beginItem = section.getString("BEGIN_CUSTOM_ITEM");
        this.actionType = ActionType.valueOf(section.getString("TYPE"));
        this.soundFail = section.getString("SOUND_FAIL")
    == null ? Sound.UI_BUTTON_CLICK : Sound.valueOf(section.getString("SOUND_FAIL"));

        //Set min-max exp section
        if(section.contains("EXP_GAIN")) {
            ConfigurationSection expSection = section.getConfigurationSection("EXP_GAIN");
            this.minGainXP = expSection.getInt("MIN");
            this.maxGainXP = expSection.getInt("MAX");
        }

        if(section.contains("REWARDS")) {
            ConfigurationSection rewardsSection = section.getConfigurationSection("REWARDS");

            for (String key : rewardsSection.getKeys(false)) {

                RewardProps rewardProps = new RewardProps(Integer.valueOf(key),
                        rewardsSection.getStringList(key));

                getRewardProps().add(rewardProps);
            }
        }


        ConfigurationSection requiresSection = section.getConfigurationSection("REQUIREMENT");

        if (section.contains("REQUIREMENT")) {
            for (String key : requiresSection.getKeys(false)) {

                ConfigurationSection requireSection = requiresSection.getConfigurationSection(key);

                LevelProps props = new LevelProps(requireSection.getInt("LEVEL"),
                        requireSection.getString("ITEM_ALLOWED"), requireSection.getStringList("CMDS").stream().toList(),
                        requireSection.getInt("CHANCE"),
                        requireSection.getString("RARITY"));

                levelProps.add(props);
                duplicateLevel.put(props.getAllowedAsString(), keyName);
            }
        }


        if(duplicateLevel.containsKey(beginItem)) {
            //current & owner
            fromOthers.put(beginItem, duplicateLevel.get(beginItem));
        }
;
        String type = "";
        switch (actionType) {
            case CRAFTING: {
                type = "CRAFTS";
                break;
            }
            case MINING: {
                type = "MINES";
                break;
            }
            case FISHING: {
                type = "FISHS";
                break;
            }
            case PLANTING: {
                type = "PLANTS";
                break;
            }
            case HUNTING: {
                type = "HUNTS";
                break;
            }
        }

        this.x = mainConfig.getDouble("CONFIG.MAX_LV." + type + ".X");
        this.y = mainConfig.getDouble("CONFIG.MAX_LV." + type + ".Y");

        System.out.println("Done loading:");
        //System.out.println(this);
    }

    private void save() {
       ConfigurationSection section = MmoCore.getInstance().getConfigFile().getConfigurationSection("MMOCORE");

        for(Level level : getAllLevels()) {
            ConfigurationSection levelSection = section.createSection(level.getKeyName());

            levelSection.set("DISPLAY_NAME", level.getDisplayName());
            levelSection.set("TYPE", level.getActionType().name());
            levelSection.set("SOUND_FAIL", level.getSoundFail().name());
            levelSection.set("BEGIN_CUSTOM_ITEM", level.getBeginItem());

            ConfigurationSection expSection = levelSection.createSection("EXP_GAIN");
            expSection.set("MIN", level.getMinGainXP());
            expSection.set("MAX", level.getMaxGainXP());

            ConfigurationSection requiresSection = levelSection.createSection("REQUIREMENT");
            for (int i = 0; i < level.getLevelProps().size(); i++) {
                Bukkit.getConsoleSender().sendMessage("I: " + i);

                LevelProps props = level.getLevelProps().get(i);

                ConfigurationSection requireSection = requiresSection.createSection(String.valueOf(i));

                requireSection.set("LEVEL", props);
                requireSection.set("ITEM_ALLOWED", props.getAllowedAsString());
            }
        }
    }

    public LevelProps filterByCurrentLevel(double currentLevel) {
        return getLevelProps().stream()
                .filter(l -> currentLevel < l.getRequiredLevel()).findFirst().orElse(null);
    }

    public boolean isMaxLevel() {
        return currentLevel >= maxLevel;
    }

    public void handleAddExp(Player player) {
        if(isMaxLevel()) return;

        int receivedXP = ThreadLocalRandom.current().nextInt(minGainXP, maxGainXP + 10);
        this.currentXP += receivedXP;

        //If not level up we gonna send receive xp message;
        if(!hasLevelUp(player)) {
            Msg.EARN_XP.sendMessage(player, new Object[]{ "", receivedXP , displayName,
                    currentXP, currentMaxXP, currentLevel});
        }
    }

    public boolean hasLevelUp(Player player) {
        if(isMaxLevel()) return false;

        if(currentXP >= currentMaxXP) {
            while (currentXP >=  currentMaxXP) {

                double newMaxXP = Math.pow((currentLevel / this.x), this.y);

                currentXP = currentXP - currentMaxXP;
                currentMaxXP = (int) newMaxXP;

                Msg.LEVEL_UP.sendMessage(player, new Object[]{
                        "",
                        displayName,
                        currentLevel, ++currentLevel
                });

                handleLevelUp(player);
            }
            return true;
        }

        return false;
    }

    public void handleLevelUp(Player player) {;
        if(isMaxLevel()) return;

        if(getRewardProps().size() > 0) {
            for(RewardProps props : rewardProps) {
                if(props.getRequiredLevel() == currentLevel && props.getRewardCmds().size() > 0) {
                    TaskUtils.run(() -> {
                        for(String cmd : props.getRewardCmds()) {
                            cmd = cmd.replace("%player%", player.getName());
                            cmd = cmd.replace("%level%", currentLevel + "");
                            ServerUtil.executeCommand(cmd);
                        }
                    });
                }
            }
        }

        for(LevelProps props : levelProps) {
            if(props.getRequiredLevel() == currentLevel && props.getCmds().size() > 0) {
                TaskUtils.run(() -> {
                    for(String cmd : props.getCmds()) {
                        cmd = cmd.replace("%player%", player.getName());
                        cmd = cmd.replace("%level%", currentLevel + "");
                        ServerUtil.executeCommand(cmd);
                    }
                });
            }
        }
    }

    public static Level getByKeyName(String keyName) {
        for(Level level : allLevels) {
            if(level.getKeyName().equalsIgnoreCase(keyName)) {
                return level;
            }
        }

        return null;
    }



    public LevelProps getPropByString(String allowedAsString) {
        for(LevelProps prop : levelProps) {
            if(prop.getAllowedAsString().equals(allowedAsString)) {
                return prop;
            }
        }

        return null;
    }


    @Override
    public String toString() {
        return "Level{" +
                "currentLevel=" + currentLevel +
                ", currentXP=" + currentXP +
                ", currentMaxXP=" + currentMaxXP +
                ", minGainXP=" + minGainXP +
                ", maxGainXP=" + maxGainXP +
                ", displayName='" + displayName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", beginItem='" + beginItem + '\'' +
                ", soundFail=" + soundFail +
                ", actionType=" + actionType +
                ", levelProps=" + levelProps +
                ", rewardProps=" + rewardProps +
                '}';
    }

    public String coloredToString() {
        return new StringBuilder("&2Name: &b").append(displayName).append("\n")
                .append("  &eEXP Gain: \n").append("    &eminXP: ").append(minGainXP).append("\n")
                .append("    &emaxXP: ").append(maxGainXP).append("\n")
                .append("  &6Begin Item: &e").append(beginItem).append("\n")
                .append("  &2Action: &a").append(actionType.getName()).append("\n")
                .append("  &5Level: &r\n")
                .append("    &5current Level: &d").append(currentLevel)
                .append("    &5current XP: &d").append(currentXP)
                .append("    &5current MaxXP: &d").append(currentMaxXP)
                .append("  &5Level Props: &r\n")
                .append(levelProps.toString())
                .toString();
    }

}
