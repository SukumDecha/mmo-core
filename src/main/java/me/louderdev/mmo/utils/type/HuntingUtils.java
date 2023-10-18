package me.louderdev.mmo.utils.type;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HuntingUtils {

    //Handler add exp when mining
    public static void handleKilled(Player player, User user, String nameSpacedId, Location loc) {
        Level lastestLevel = user.getLastestLevel();

        //Cached method for perfomance
        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.HUNTING ){
            if(handleAddExp(player, user, lastestLevel, nameSpacedId)) {
                List<LevelProps> levelProps = lastestLevel.getLevelProps().stream()
                        .filter(l -> lastestLevel.getCurrentLevel() >= l.getRequiredLevel())
                        .filter(l -> l.getChance() > 0).collect(Collectors.toList());

                handleDrop(lastestLevel, levelProps, player, loc);
                return;
            }
        }

        List<Level> huntingLevels = user.getLevelByAction(ActionType.HUNTING);

        for(Level level : huntingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, user, level, nameSpacedId)) {
                user.setLastestLevel(level);

                List<LevelProps> levelProps = level.getLevelProps().stream()
                        .filter(l -> level.getCurrentLevel() >= l.getRequiredLevel())
                        .filter(l -> l.getChance() > 0).collect(Collectors.toList());

                if(levelProps.size() > 0)  handleDrop(level, levelProps, player, loc);

                //remove return for multiple level support
                return;
            }
        }
    }

    private static void handleDrop(Level level, List<LevelProps> props, Player player, Location loc) {
        for(LevelProps prop : props) {
            if(ThreadLocalRandom.current().nextInt(0, 100) < prop.getChance()) {
                CustomStack stack = CustomStack.getInstance(prop.getAllowedAsString());

                ItemStack toDrop = stack == null ? new ItemStack(Material.valueOf(prop.getAllowedAsString()), 1) :
                        stack.getItemStack();

                player.getWorld().dropItem(loc.clone().add(0, 2, 0), toDrop);
                Msg.ITEM_DROP.sendMessage(player, "", prop.getRarity(), stack == null ? prop.getAllowedAsString() : stack.getDisplayName(), level.getDisplayName()
                        , prop.getChance());
            }
        }
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
