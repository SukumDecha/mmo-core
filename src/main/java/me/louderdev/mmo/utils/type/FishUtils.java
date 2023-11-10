package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;

import java.util.List;

public class FishUtils {


    //Handler add exp when fishing
    public static void handleFishing(Player player, User user, String nameSpacedId) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.FISHING && handleAddExp(player, user, lastestLevel, nameSpacedId)) return;

        List<Level> fishingLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : fishingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, nameSpacedId)) {
                user.setLastestLevel(level);
                //remove return for multiple level support
                return;
            }
        }
    }

    public static boolean canFish(User user, Player player, String nameSpacedId) {
        if(player.isOp()) return true;

        List<Level> fishingLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : fishingLevels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel())
                    .filter(l -> l.getAllowedAsString().equalsIgnoreCase(nameSpacedId)).findFirst().orElse(null);

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
        if (level.getFromOthers().containsKey(nameSpacedId)) {
            Level other = user.getLevelByName(level.getFromOthers().get(nameSpacedId));

            if (other.getCurrentLevel() < other.getPropByString(nameSpacedId).getRequiredLevel()) {
                return false;
            } else {
                level.handleAddExp(player);
                return true;
            }

        }

        if (!level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            return false;
        }

        level.handleAddExp(player);
        return true;
    }
}
