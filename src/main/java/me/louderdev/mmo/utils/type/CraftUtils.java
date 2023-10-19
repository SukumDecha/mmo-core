package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftUtils {



    //Handler add exp when mining


    public static void handleCraft(Player player, User user, String keySpace) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.CRAFTING &&
                handleAddExp(player, user, lastestLevel, keySpace)) {
            return;
        }

        List<Level> craftingLevels = user.getLevelByAction(ActionType.CRAFTING);

        for(Level level : craftingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, keySpace)) {
                user.setLastestLevel(level);
                //remove return for multiple level support
                return;
            }
        }
    }

    public static boolean canCraft(Player player, User user, String keyName) {
        if(player.isOp()) return true;

        List<Level> craftingLevels = user.getLevelByAction(ActionType.CRAFTING);

        for(Level level : craftingLevels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel())
                    .filter(l -> l.getAllowedAsString().equalsIgnoreCase(keyName)).findFirst().orElse(null);

            if(notAllowedProps != null) {
                Msg.REQUIRED_MORE_LEVEL.sendMessage(player, new Object[]{ "",
                        level.getDisplayName(), level.getActionType().getName(), notAllowedProps.getRequiredLevel(), level.getCurrentLevel()
                });

                player.playSound(player.getLocation(), level.getSoundFail(), 0.5f, 0.5f);
                return false;
            }
        }

        return true;
    }


    public static boolean handleAddExp(Player player, User user, Level level, String nameSpacedId) {
        if(level.getFromOthers().containsKey(nameSpacedId)) {
//            Bukkit.broadcastMessage("Found the valid");
//            Bukkit.broadcastMessage("Input level: " + level.getKeyName());
//
//            Bukkit.broadcastMessage("Other level: " + other.getKeyName());

            Level other = user.getLevelByName(level.getFromOthers().get(nameSpacedId));

            if(other.getCurrentLevel() < other.getPropByString(nameSpacedId).getRequiredLevel() ) {
                return false;
            } else {
                level.handleAddExp(player);
                return true;
            }

        }

        if(!level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            return false;
        }

        level.handleAddExp(player);
        return true;
    }
}
