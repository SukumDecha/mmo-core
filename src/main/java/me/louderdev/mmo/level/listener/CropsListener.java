package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.CropsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CropsListener implements Listener {
   @EventHandler
   public void onPlace(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.FARMLAND)) {
            return;
        }

        Player player = e.getPlayer();
        CustomStack customStack = CustomStack.byItemStack(player.getItemInHand());
        if(customStack != null) {

            User user = User.getByUuid(player.getUniqueId());

            CropsUtils.handleCrops(player, user, customStack.getNamespacedID());

            //prevent player from breaking block above their levels
            if(!CropsUtils.canGrowSeeds(user, player, customStack.getNamespacedID())) {
                e.setCancelled(true);
            }
        }
   }
}
