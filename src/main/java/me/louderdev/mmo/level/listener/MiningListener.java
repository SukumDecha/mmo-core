package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.MiningUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class MiningListener implements Listener {

    @EventHandler
    public void onCustomBlockBreak(CustomBlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = User.getByUuid(player.getUniqueId());;

        //prevent player from breaking block above their levels
        if(!MiningUtils.canBreakBlock(user, player, event.getNamespacedID())) {
            event.setCancelled(true);
            return;
        }

        MiningUtils.handleMining(player, user, event.getNamespacedID());
    }


}
