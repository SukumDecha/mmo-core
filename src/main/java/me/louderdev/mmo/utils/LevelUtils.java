package me.louderdev.mmo.utils;

import dev.lone.itemsadder.api.CustomBlock;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
public class LevelUtils {


    /**
     * method to handleMining when player break block.
     *
     * @param levels The list of levels that's it's action are mining.
     * @param block  broken block.
     *
     * @return true if it passed the check.
     */
    public static void handleMining(List<Level> levels, Block block, Player player) {
        for(Level level : levels) {
            ItemStack toCheck = new ItemStack(block.getType(), block.getData());
            if(level.getStartMaterial().equals(toCheck)) {
                level.handleAddExp(player);

                //since this should do only 1 times
                return;
            }
        }

    }

    /**
     * method to handleMining when player break block.
     *
     * @param levels The list of levels that's it's action are mining.
     * @param block  broken block.
     *
     * @return true if it passed the check.
     */
    public static boolean handleFilterMining(List<Level> levels, Block block, Player player) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);

        boolean toReturn = true;

        for(Level level : levels) {
            LevelProps notAllowedProps = level.getLevelProps().stream()
                    .filter(l -> level.getCurrentLevel() < l.getRequiredLevel()).findFirst().orElse(null);
            if(notAllowedProps.getAllowedBlock() == customBlock) {
                Msg.REQUIRED_MORE_LEVEL.sendMessage(player, level.getActionType().getName(),
                        level.getDisplayName(), level.getCurrentLevel());
                toReturn = false;
            }
        }

        return toReturn;
    }
}
