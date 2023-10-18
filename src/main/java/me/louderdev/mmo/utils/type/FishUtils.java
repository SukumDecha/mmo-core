package me.louderdev.mmo.utils.type;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FishUtils {


    //Handler add exp when mining
    public static void handleCaught(Player player, User user, String nameSpacedID) {
        Level lastestLevel = user.getLastestLevel();

        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.FISHING && handleAddExp(player, lastestLevel, nameSpacedID)) return;

        List<Level> fishingLevels = user.getLevelByAction(ActionType.FISHING);

        for(Level level : fishingLevels) {
            //no need to loop all levels;
            if(handleAddExp(player, level, nameSpacedID)) {
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

                /*
                Item item = player.getWorld().dropItemNaturally(player.getLocation(), stack);

                item.setMetadata("fishing", new FixedMetadataValue(MmoCore.getInstance(),
                        player.getUniqueId()));

                 */
                player.getInventory().addItem(stack.getItemStack());
                Msg.ITEM_DROP.sendMessage(player, "", prop.getRarity(), stack.getDisplayName(), level.getDisplayName());
            }
        }
    }

    private static boolean handleAddExp(Player player, Level level, String nameSpacedId) {
        if(level.getBeginItem().equalsIgnoreCase(nameSpacedId)) {
            level.handleAddExp(player);
            return true;
        }

        return false;
    }
}
