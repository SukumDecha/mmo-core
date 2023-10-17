package me.louderdev.mmo.level.listener;

import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.level.MiningUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class MiningListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = User.getByUuid(player.getUniqueId());


        MiningUtils.handleMining(event.getBlock(), player, user);

        if(!MiningUtils.handleFilterMining(event.getBlock(), user, player)) {
            event.setCancelled(true);
        }
    }
}
