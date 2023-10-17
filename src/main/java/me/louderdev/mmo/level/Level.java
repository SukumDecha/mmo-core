package me.louderdev.mmo.level;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import lombok.Setter;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.utils.Msg;
import me.louderdev.mmo.utils.file.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Level {

    private double currentLevel, currentXP, currentMaxXP, minXP, maxXP; //min-max exps
    private String displayName, keyName; //Name of this type of levels;
    private StartMaterial startMaterial; //Material to use when start counting level
    private ActionType actionType; //Action type of this level

    private List<LevelProps> levelProps; //levelProps
    @Getter private static List<Level> allLevels = new ArrayList<>();

    public Level(String keyName, FileConfiguration configuration) {
        this.keyName = keyName;
        this.levelProps = new ArrayList<>();

        this.load(configuration);
    }

    public Level(String otherKeyName) {
        Optional<Level> optionalLevel = getByKeyName(otherKeyName);

        if (optionalLevel.isPresent()) {
            Level others = optionalLevel.get();

            this.keyName = others.getKeyName();
            this.displayName = others.getDisplayName();
            this.startMaterial = others.getStartMaterial();
            this.actionType = others.getActionType();

            this.minXP = others.getMinXP();
            this.maxXP = others.getMaxXP();

            this.levelProps = others.getLevelProps();
        } else {
            throw new NullPointerException("Could not find level with that keyname (" +
                    otherKeyName + ")");
        }
    }

    public static void init() {
        ConfigFile config = MmoCore.getInstance().getConfigFile();;

        ConfigurationSection section = config.getConfigurationSection("MMOCORE");

        for(String key : section.getKeys(false)) {
            new Level(key, config);
        }

    }

    private void load(FileConfiguration c) {
        ConfigurationSection section = c.getConfigurationSection("MMOCORE." + keyName);

        this.displayName = section.getString("DISPLAY_NAME");
        this.actionType = ActionType.valueOf(section.getString("TYPE"));

        //Set startMaterial section
        if(c.contains("ITEM")) {
            ConfigurationSection itemSection = section.getConfigurationSection("ITEM");
            StartMaterial startMaterial = new StartMaterial(itemSection.getBoolean("USE_CUSTOM_ITEM"));

            if(startMaterial.isCustom()) {
                switch (actionType) {
                    case MINING: {
                        startMaterial.setCustomBlock(CustomBlock.getInstance(
                                itemSection.getString("CUSTOM_ITEM")
                        ));
                        break;
                    }
                    default:{
                        startMaterial.setCustomStack(CustomStack.getInstance(
                                itemSection.getString("CUSTOM_ITEM")
                        ));
                    }
                }
            } else {
                ConfigurationSection defaultStack = itemSection.getConfigurationSection("DEFAULT_ITEM");
                ItemStack toReturn = new ItemStack(Material.valueOf(defaultStack.getString(
                        "TYPE"
                )), 1);
                toReturn.setDurability((byte) defaultStack.getInt("DATA"));

                startMaterial.setItemStack(toReturn);
            }
        }

        //Set min-max exp section
        if(c.contains("EXP")) {
            ConfigurationSection expSection = section.getConfigurationSection("EXP");
            this.minXP = expSection.getDouble("MIN");
            this.maxXP = expSection.getDouble("MAX");
        }

        ConfigurationSection requiresSection = section.getConfigurationSection("REQUIREMENT");

        if (section.contains("REQUIREMENT")) {
            for (String key : requiresSection.getKeys(false)) {
                ConfigurationSection requireSection = requiresSection.getConfigurationSection(key);

                getLevelProps().add(new LevelProps(requireSection.getInt("LEVEL"),
                        requireSection.getString("ITEM__ALLOWED"), actionType));
            }
        }

        //Add this level into all level;
        allLevels.add(this);
    }

    private void save() {
       ConfigurationSection section = MmoCore.getInstance().getConfigFile().getConfigurationSection("MMOCORE");

        for(Level level : getAllLevels()) {
            ConfigurationSection levelSection = section.createSection(level.getKeyName());

            levelSection.set("DISPLAY_NAME", level.getDisplayName());
            levelSection.set("TYPE", level.getActionType().name());

            ConfigurationSection itemSection = levelSection.createSection("ITEM");
            itemSection.set("USE_CUSTOM_ITEM", startMaterial.isCustom());
            if(startMaterial.isCustom()) {
                switch (actionType) {
                    case MINING: {
                        itemSection.set("CUSTOM_ITEM", startMaterial.getCustomBlock().getNamespacedID());
                         break;
                    }
                    default: {
                        itemSection.set("CUSTOM_ITEM", startMaterial.getCustomStack().getNamespacedID());
                    }
                }
            } else {
                ConfigurationSection defaultSection = itemSection.createSection("DEFAULT_ITEM");
                defaultSection.set("TYPE", startMaterial.getItemStack().getType().name());
                defaultSection.set("DATA", startMaterial.getItemStack().getDurability());
            }

            ConfigurationSection expSection = levelSection.createSection("EXP_GAIN");
            expSection.set("MIN", level.getMinXP());
            expSection.set("MAX", level.getMaxXP());

            ConfigurationSection requiresSection = levelSection.createSection("REQUIREMENT");
            for (int i = 0; i < level.getLevelProps().size(); i++) {
                LevelProps props = level.getLevelProps().get(i);

                ConfigurationSection requireSection = requiresSection.createSection(String.valueOf(i));
                requireSection.set("LEVEL", props);
                requireSection.set("ITEM_ALLOWED", props.getAllowedItem().getNamespacedID());
            }
        }
    }

    public LevelProps filterByCurrentLevel(double currentLevel) {
        return getLevelProps().stream()
                .filter(l -> currentLevel < l.getRequiredLevel()).findFirst().orElse(null);
    }

    public void handleAddExp(Player player) {
        double receivedXP = ThreadLocalRandom.current().nextDouble(minXP, maxXP);
        this.currentXP += receivedXP;

        Msg.EARN_XP.sendMessage(player, receivedXP, getActionType().getName(),
                getCurrentXP(), getCurrentMaxXP());

        handleCheckExp(player);
    }

    public void handleCheckExp(Player player) {
        if(currentXP >= currentMaxXP) {
            currentXP -= currentMaxXP;
            double nextLevel = currentLevel + 1;
            Msg.EARN_XP.sendMessage( player, getActionType().getName(),
                    currentLevel++, nextLevel);
        }
        handleCheckExp(player);
    }

    public Optional getByKeyName(String keyName) {
        for(Level level : allLevels) {
            if(level.getKeyName().equals(keyName)) {
                return Optional.of(level);
            }
        }

        return Optional.of(null);
    }

}
