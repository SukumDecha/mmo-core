package me.louderdev.mmo.utils.type;

import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;

import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;

import java.util.List;

public class MiningUtils {


    //Handler add exp when mining
    public static void handleMining(Player player, User user, String nameSpacedId) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.MINING && handleAddExp(player, lastestLevel, nameSpacedId)) return;

        List<Level> miningLevels = user.getLevelByAction(ActionType.MINING);

        for(Level level : miningLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, level, nameSpacedId)) {
                user.setLastestLevel(level);
                return;
            }
        }
    }

    public static boolean canBreakBlock(User user, Player player, String nameSpacedId) {

        List<Level> miningLevels = user.getLevelByAction(ActionType.MINING);

        for(Level level : miningLevels) {
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
