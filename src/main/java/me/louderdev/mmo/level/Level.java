package me.louderdev.mmo.level;

import lombok.Getter;
import lombok.Setter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Level {

    private int currentLevel, currentXP, currentMaxXP, minGainXP, maxGainXP; //min-max exps
    private String displayName, keyName, beginItem; //Name of this type of levels;
    private Sound soundFail;
    private ActionType actionType; //Action type of this level
    private List<LevelProps> levelProps; //levelProps
    @Getter private static List<Level> allLevels = new ArrayList<>();;

    public Level(String keyName, FileConfiguration configuration) {
        this.keyName = keyName;
        this.levelProps = new ArrayList<>();

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
            this.currentMaxXP = 20;

            this.levelProps = others.getLevelProps();
        } else {
            throw new NullPointerException("Could not find level with that keyname (" +
                    otherKeyName + ")");
        }
    }

    public static void init() {
        ConfigFile config = MmoCore.getInstance().getConfigFile();;
        ConfigurationSection section = config.getConfigurationSection("MMOCORE");

        allLevels = new ArrayList<>();

        for(String key : section.getKeys(false)) {
            System.out.println("WKWKWKWK: " + key);

            Level toAdd = new Level(key, config);
            allLevels.add(toAdd);
        }

    }

    private void load(FileConfiguration c) {
        System.out.println("Loading " + keyName);
        ConfigurationSection section = c.getConfigurationSection("MMOCORE." + keyName);


        this.displayName = section.getString("DISPLAY_NAME");
        this.beginItem = section.getString("BEGIN_CUSTOM_ITEM");
        this.actionType = ActionType.valueOf(section.getString("TYPE"));
        this.soundFail = Sound.valueOf(section.getString("SOUND_FAIL"));
        /*
        switch (actionType) {
            case MINING:
                Bukkit.broadcastMessage("Doing this ");
                this.startMaterial = new StartMaterial(CustomBlock.getInstance(section.getString("BEGIN_CUSTOM_ITEM")));
                break;
            default:
                Bukkit.broadcastMessage("Doing that ");
                this.startMaterial = new StartMaterial(CustomStack.getInstance(section.getString("BEGIN_CUSTOM_ITEM")));
                break;
        }
         */

        //Set min-max exp section
        if(section.contains("EXP_GAIN")) {
            ConfigurationSection expSection = section.getConfigurationSection("EXP_GAIN");
            this.minGainXP = expSection.getInt("MIN");
            this.maxGainXP = expSection.getInt("MAX");
        }

        ConfigurationSection requiresSection = section.getConfigurationSection("REQUIREMENT");

        if (section.contains("REQUIREMENT")) {
            for (String key : requiresSection.getKeys(false)) {
                Bukkit.getConsoleSender().sendMessage("key: " + key);

                ConfigurationSection requireSection = requiresSection.getConfigurationSection(key);
                Bukkit.getConsoleSender().sendMessage("Level: " + requireSection.getInt("LEVEL"));
                Bukkit.getConsoleSender().sendMessage("ITEM_ALLOWED: " + requireSection.getInt("ITEM_ALLOWED"));

                levelProps.add(new LevelProps(requireSection.getInt("LEVEL"),
                        requireSection.getString("ITEM_ALLOWED"), requireSection.getStringList("CMDS").stream().toList(),
                        requireSection.getInt("CHANCE"),
                        requireSection.getString("RARITY")));
            }
        }

        System.out.println("Done loading:");
        System.out.println(this);
    }

    private void save() {
       ConfigurationSection section = MmoCore.getInstance().getConfigFile().getConfigurationSection("MMOCORE");

        for(Level level : getAllLevels()) {
            ConfigurationSection levelSection = section.createSection(level.getKeyName());

            levelSection.set("DISPLAY_NAME", level.getDisplayName());
            levelSection.set("TYPE", level.getActionType().name());
            levelSection.set("SOUND_FAIL", level.getSoundFail().name());
            levelSection.set("BEGIN_CUSTOM_ITEM", level.getBeginItem());

            /*
            ConfigurationSection itemSection = levelSection.createSection("ITEM");
            switch (actionType) {
                case MINING: {
                    itemSection.set("CUSTOM_ITEM", startMaterial.getCustomBlock().getNamespacedID());
                    break;
                }
                default: {
                    itemSection.set("CUSTOM_ITEM", startMaterial.getCustomStack().getNamespacedID());
                }
            }

             */

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

    public void handleAddExp(Player player) {
        int receivedXP = ThreadLocalRandom.current().nextInt(minGainXP, maxGainXP + 10);

        this.currentXP += receivedXP;

        //If not level up we gonna send receive xp message;
        if(!hasLevelUp(player)) {
            Msg.EARN_XP.sendMessage(player, new Object[]{ "", receivedXP , displayName,
                    currentXP, currentMaxXP, currentLevel});
        }
    }

    public boolean hasLevelUp(Player player) {
        if(currentXP >= currentMaxXP) {
            while (currentXP >=  currentMaxXP) {
                int newMaxXP = ThreadLocalRandom.current().nextInt(currentMaxXP, currentMaxXP + (currentLevel * 100));

                currentXP = currentXP - currentMaxXP;
                currentMaxXP = newMaxXP;

                handleLevelUp(player);
                Msg.LEVEL_UP.sendMessage(player, new Object[]{
                        "",
                        getActionType().getName(),
                        currentLevel, ++currentLevel
                });
            }
            return true;
        }

        return false;
    }

    public void handleLevelUp(Player player) {;
        for(LevelProps props : levelProps) {
            if(props.getRequiredLevel() == (currentLevel + 1) && props.getCmds().size() > 0) {
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

    public Level getByKeyName(String keyName) {
        for(Level level : allLevels) {
            if(level.getKeyName().equalsIgnoreCase(keyName)) {
                return level;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Level{" +
                "minXP=" + minGainXP +
                ", maxXP=" + maxGainXP +
                ", displayName='" + displayName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", beginItem=" + beginItem +
                ", actionType=" + actionType +
                ", levelProps=" + levelProps +
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
                .toString();
    }

}
