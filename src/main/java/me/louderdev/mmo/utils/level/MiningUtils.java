package me.louderdev.mmo.utils.level;

import dev.lone.itemsadder.api.CustomBlock;
import me.louderdev.mmo.level.ActionType;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.level.LevelProps;
import me.louderdev.mmo.level.StartMaterial;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.Msg;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MiningUtils {


    public static boolean handleAddExp(Block block, Player player, Level level) {
        StartMaterial startMats = level.getStartMaterial();
        if(startMats.isCustom()) {
            CustomBlock targetBlock = CustomBlock.byAlreadyPlaced(block);
            if(startMats.getCustomBlock().equals(targetBlock)) {
                level.handleAddExp(player);

                return true;
            }
        }

        if(startMats.getItemStack().getType() == block.getType()) {
            level.handleAddExp(player);
            return true;
        }

        return false;
    }

    public static void handleMining(Block block, Player player, User user) {
        Level lastestLevel = user.getLastestLevel();
        if(lastestLevel != null && lastestLevel.getActionType() == ActionType.MINING) {
            if(!handleAddExp(block, player, lastestLevel)) {
                List<Level> miningLevels = user.getLevelByAction(ActionType.MINING);

                for(Level level : miningLevels) {
                    handleAddExp(block, player, level);
                }
            }
        } else {
            List<Level> miningLevels = user.getLevelByAction(ActionType.MINING);

            for(Level level : miningLevels) {
                handleAddExp(block, player, level);
            }
        }
    }

    public static boolean handleFilterMining(Block block, User user, Player player) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);

        boolean toReturn = true;

        List<Level> miningLevels = user.getLevelByAction(ActionType.MINING);

        for(Level level : miningLevels) {
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
