package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;

import java.util.List;

public class CropsUtils {



    //Handler add exp when mining
    public static void handleCrops(Player player, User user, String nameSpacedId) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.PLANTING && handleAddExp(player, lastestLevel, nameSpacedId)) return;

        List<Level> cropsLevels = user.getLevelByAction(ActionType.PLANTING);

        for(Level level : cropsLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, level, nameSpacedId)) {
                user.setLastestLevel(level);
                return;
            }
        }
    }

    public static boolean canGrowSeeds(User user, Player player, String nameSpacedId) {

        List<Level> cropsLevels = user.getLevelByAction(ActionType.PLANTING);

        for(Level level : cropsLevels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel())
                    .filter(l -> l.getAllowedAsString().equalsIgnoreCase(nameSpacedId)).findFirst().orElse(null);

            if(notAllowedProps != null) {
                Msg.REQUIRED_MORE_LEVEL.sendMessage(player, new Object[]{ "",
                        level.getDisplayName(), notAllowedProps.getRequiredLevel(), level.getCurrentLevel()
                });

                player.playSound(player.getLocation(), level.getSoundFail(), 0.5f, 0.5f);
                return false;
            }
        }

        return true;
    }

    public static boolean handleAddExp(Player player, Level level, String nameSpacedId) {
        if(level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            level.handleAddExp(player);
            return true;
        }

        return false;
    }
}
