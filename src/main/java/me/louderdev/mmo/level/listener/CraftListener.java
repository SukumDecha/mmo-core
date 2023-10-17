package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomStack;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.CraftUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {

        Recipe recipe = event.getRecipe();

        // Check if the recipe is not null and has a result
        if (recipe != null && recipe.getResult() != null) {
            ItemStack resultItem = recipe.getResult();

            // Do something with the result item
           try {

               CustomStack customStack = CustomStack.byItemStack(resultItem);
               if(customStack != null) {
                   Player player = (Player) event.getWhoClicked();
                   User user = User.getByUuid(player.getUniqueId());

                   CraftUtils.handleCraft(player, user, customStack.getNamespacedID());

                   if(!CraftUtils.canCraft(player, user, customStack.getNamespacedID())) {
                       event.setCancelled(true);
                   }
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
    }
}
