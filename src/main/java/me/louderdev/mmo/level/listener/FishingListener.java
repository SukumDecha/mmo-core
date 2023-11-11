package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.FishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FishingListener implements Listener {


    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if(event.getCaught() != null) {
            try {
                Item caught = (Item) event.getCaught();
                caught.setMetadata("fishing", new FixedMetadataValue(MmoCore.getInstance(), event.getPlayer().getName()));

            } catch (Exception e) {}
        }
    }


    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if(!item.hasMetadata("fishing")) return;
        if(!item.getMetadata("fishing").get(0).asString().equals(event.getPlayer()
                .getName())) {
            event.setCancelled(true);
            return;
        }


        Player player = event.getPlayer();
        User user = User.getByUuid(player.getUniqueId());

        CustomStack stack = CustomStack.byItemStack(event.getItem().getItemStack());

        if(!FishUtils.canCaught(player, user, stack == null ? event.getItem().getItemStack().getType().name()
                : stack.getNamespacedID())) {
            event.getItem().getItemStack().setType(Material.AIR);
            event.setCancelled(true);
            return;
        }

        FishUtils.handleCaught(player, user, stack == null ? event.getItem().getItemStack().getType().name() : stack.getNamespacedID());
    }


}
