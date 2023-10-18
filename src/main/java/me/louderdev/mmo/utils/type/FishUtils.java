package me.louderdev.mmo.utils.type;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FishUtils {


    //Handler add exp when mining
    public static void handleCaught(Player player, User user, String nameSpacedID) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.FISHING){
            if(handleAddExp(player, user, lastestLevel, nameSpacedID)) {

                List<LevelProps> levelProps = lastestLevel.getLevelProps().stream()
                        .filter(l -> lastestLevel.getCurrentLevel() >= l.getRequiredLevel())
                        .filter(l -> l.getChance() > 0).collect(Collectors.toList());

                handleDrop(lastestLevel, levelProps, player);
            }
        }

        List<Level> fishingLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : fishingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, nameSpacedID)) {
                user.setLastestLevel(level);

                List<LevelProps> levelProps = level.getLevelProps().stream()
                        .filter(l -> level.getCurrentLevel() >= l.getRequiredLevel())
                        .filter(l -> l.getChance() > 0).collect(Collectors.toList());

                if(levelProps.size() > 0)   {
                    handleDrop(level, levelProps, player);
                }

                //remove return for multiple level support
                return;
            }
        }
    }

    private static void handleDrop(Level level, List<LevelProps> props, Player player) {

        for(LevelProps prop : props) {

            if(ThreadLocalRandom.current().nextInt(1, 10) < prop.getChance()) {
                CustomStack stack = CustomStack.getInstance(prop.getAllowedAsString());

                /*
                Item item = player.getWorld().dropItemNaturally(player.getLocation(), stack);

                item.setMetadata("fishing", new FixedMetadataValue(MmoCore.getInstance(),
                        player.getUniqueId()));

                 */
                player.getInventory().addItem(stack.getItemStack());
                Msg.ITEM_DROP.sendMessage(player, "", prop.getRarity(), (stack == null
                ? prop.getAllowedAsString() : stack.getDisplayName()), level.getDisplayName(), prop.getChance());
            } else {
                Bukkit.broadcastMessage("Naj");
            }

            Bukkit.broadcastMessage("Passed");
        }

        player.updateInventory();
    }

    public static boolean handleAddExp(Player player, User user, Level level, String nameSpacedId) {
        if(level.getFromOthers().containsKey(nameSpacedId)) {
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
