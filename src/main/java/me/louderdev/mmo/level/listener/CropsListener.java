package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomCrop;
import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.CropsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CropsListener implements Listener {
//   @EventHandler
//   public void onPlace(PlayerInteractEvent e) {
//        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.FARMLAND)) {
//            return;
//        }
//
//        Player player = e.getPlayer();
//        CustomStack customStack = CustomStack.byItemStack(player.getItemInHand());
//        if(customStack != null) {
//
//            User user = User.getByUuid(player.getUniqueId());
//
//
//            //prevent player from breaking block above their levels
//            if(!CropsUtils.canGrowSeeds(user, player, customStack.getNamespacedID())) {
//                e.setCancelled(true);
//                return;
//            }
//
//            CropsUtils.handleCrops(player, user, customStack.getNamespacedID());
//
//        }
//   }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            CustomCrop customCrop = CustomCrop.byAlreadyPlaced(e.getClickedBlock());
            if (customCrop != null && customCrop.isFullyGrown()) {
                User user = User.getByUuid(player.getUniqueId());

                //prevent player from breaking block above their levels
                if (!CropsUtils.canGrowSeeds(user, player, customCrop.getSeed().getNamespacedID())) {
                    e.setCancelled(true);
                    return;
                }

                CropsUtils.handleCrops(player, user, customCrop.getSeed().getNamespacedID());
                return;

            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        CustomCrop customCrop = CustomCrop.byAlreadyPlaced(e.getBlock());
        if(customCrop != null && customCrop.isFullyGrown()) {
            User user = User.getByUuid(player.getUniqueId());

            //prevent player from breaking block above their levels
            if(!CropsUtils.canGrowSeeds(user, player, customCrop.getSeed().getNamespacedID())) {
                e.setCancelled(true);
                return;
            }

            CropsUtils.handleCrops(player, user, customCrop.getSeed().getNamespacedID());
            return;
        }

        //Check if this is growable bloc
        if (isGrowable(e.getBlock())) {
            Ageable ageable = (Ageable) e.getBlock().getBlockData();

            if (ageable.getAge() == ageable.getMaximumAge()) {
                User user = User.getByUuid(player.getUniqueId());

                //prevent player from breaking block above their levels
                if (!CropsUtils.canGrowSeeds(user, player, customCrop.getSeed().getNamespacedID())) {
                    e.setCancelled(true);
                    return;
                }

                CropsUtils.handleCrops(player, user, customCrop.getSeed().getNamespacedID());
            }
        }
    }

    private boolean isGrowable(Block block) {
        Material type = block.getType();
        switch (type) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case NETHER_WART:
                return true;
            // Add more growable types as needed
            default:
                return false;
        }
    }

}
