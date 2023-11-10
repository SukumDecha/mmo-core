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

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if(event.getCaught() != null) {
            try {
                Item caught = (Item) event.getCaught();

                CustomStack customStack = CustomStack.byItemStack(caught.getItemStack());
                User user = User.getByUuid(event.getPlayer().getUniqueId());

                String name = customStack == null ?
                        caught.getItemStack().getType().name() : customStack.getNamespacedID();
                if(!FishUtils.canFish(user, event.getPlayer(), name)) {
                    caught.getItemStack().setType(Material.AIR);
                    event.setCancelled(true);
                    return;
                }

                FishUtils.handleFishing(event.getPlayer(), user, name);
            } catch (Exception e) {}
        }
    }

}
