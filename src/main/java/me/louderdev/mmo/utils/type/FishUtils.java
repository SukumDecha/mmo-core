package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.enums.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.props.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FishUtils {

    public static boolean canCaught(Player player, User user, String keyName) {
        if(player.isOp()) return true;

        List<Level> userLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : userLevels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel())
                    .filter(l -> l.getAllowedAsString().equalsIgnoreCase(keyName)).findFirst().orElse(null);

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

    //Handler add exp when mining
    public static void handleCaught(Player player, User user, String nameSpacedID) {

        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.FISHING && handleAddExp(player, user, lastestLevel, nameSpacedID)){
            return;
        }

        List<Level> fishingLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : fishingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, nameSpacedID)) {
                user.setLastestLevel(level);

               return;
            }
        }
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
