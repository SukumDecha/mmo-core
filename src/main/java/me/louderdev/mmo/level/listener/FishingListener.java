package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.FishUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class FishingListener implements Listener {

    private final MmoCore plugin = MmoCore.getInstance();

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if(event.getCaught() != null) {
            Item caught = (Item) event.getCaught();

            CustomStack customStack = CustomStack.byItemStack(caught.getItemStack());
            if(customStack != null) {
                Player player = event.getPlayer();
                User user = User.getByUuid(player.getUniqueId());
                FishUtils.handleCaught(player, user, customStack.getNamespacedID());

            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if(!item.hasMetadata("fishing")) return;
        if(!item.getMetadata("fishing").get(0).equals(event.getPlayer()
                .getUniqueId().toString())) {
            event.setCancelled(true);
        }

    }
}
