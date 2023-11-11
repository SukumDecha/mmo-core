package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.enums.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.props.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;

import java.util.List;

public class CropsUtils {



    //Handler add exp when mining
    public static void handleCrops(Player player, User user, String nameSpacedId) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.PLANTING && handleAddExp(player, user, lastestLevel, nameSpacedId)) return;

        List<Level> cropsLevels = user.getLevelByAction(ActionType.PLANTING);

        for(Level level : cropsLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, nameSpacedId)) {
                user.setLastestLevel(level);
                //remove return for multiple level support
                return;
            }
        }
    }

    public static boolean canGrowSeeds(User user, Player player, String nameSpacedId) {
        if(player.isOp()) return true;

        List<Level> cropsLevels = user.getLevelByAction(ActionType.PLANTING);

        for(Level level : cropsLevels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel())
                    .filter(l -> l.getAllowedAsString().equalsIgnoreCase(nameSpacedId)).findFirst().orElse(null);

            if(notAllowedProps != null) {
                Msg.REQUIRED_MORE_LEVEL.sendMessage(player, "",
                        level.getDisplayName(), level.getActionType().getName(), notAllowedProps.getRequiredLevel(), level.getCurrentLevel()
                );

                player.playSound(player.getLocation(), level.getSoundFail(), 0.5f, 0.5f);
                return false;
            }
        }

        return true;
    }

    public static boolean handleAddExp(Player player, User user, Level level, String nameSpacedId) {
        if(level.getFromOthers().containsKey(nameSpacedId)) {
            Level other = user.getLevelByName(level.getFromOthers().get(nameSpacedId));

            if(other.getCurrentLevel() < other.getPropByString(nameSpacedId).getRequiredLevel() ) {
                return false;
            } else {
                level.addXp(player);
                return true;
            }

        }

        if(!level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            return false;
        }

        level.addXp(player);
        return true;
    }
}
