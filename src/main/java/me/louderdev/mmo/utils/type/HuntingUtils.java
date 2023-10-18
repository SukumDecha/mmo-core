package me.louderdev.mmo.utils.type;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HuntingUtils {

    //Handler add exp when mining
    public static void handleKilled(Player player, User user, String nameSpacedId) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.HUNTING && handleAddExp(player, lastestLevel, nameSpacedId)) return;

        List<Level> huntingLevels = user.getLevelByAction(ActionType.HUNTING);

        for(Level level : huntingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, level, nameSpacedId)) {
                user.setLastestLevel(level);

                List<LevelProps> levelProps = level.getLevelProps().stream()
                        .filter(l -> level.getCurrentLevel() >= l.getRequiredLevel())
                        .filter(l -> l.getChance() > 0).collect(Collectors.toList());

                handleDrop(lastestLevel, levelProps, player);
                return;
            }
        }
    }

    private static void handleDrop(Level level, List<LevelProps> props, Player player) {

        for(LevelProps prop : props) {
            if(ThreadLocalRandom.current().nextInt(0, 100) < prop.getChance()) {
                CustomStack stack = CustomStack.getInstance(prop.getAllowedAsString());

                player.getInventory().addItem(stack.getItemStack());
                Msg.ITEM_DROP.sendMessage(player, "", prop.getRarity(), stack.getDisplayName(), level.getDisplayName());
            }
        }
    }

    public static boolean handleAddExp(Player player, Level level, String nameSpacedId) {
        if(level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            level.handleAddExp(player);
            return true;
        }

        return false;
    }
}
