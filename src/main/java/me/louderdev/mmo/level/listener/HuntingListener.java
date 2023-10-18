package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomMob;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.type.CraftUtils;
import me.louderdev.mmo.utils.type.HuntingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class HuntingListener implements Listener {

    @EventHandler
    public void onHunt(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null || event.getEntity() instanceof Item) return;

        LivingEntity entity = event.getEntity();
        Player player = event.getEntity().getKiller();
        User user = User.getByUuid(player.getUniqueId());

        ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);

        HuntingUtils.handleKilled(player, user, mythicMob == null ? event.getEntity().getName() : mythicMob.getName(), event.getEntity().getLocation());
    }

//    @EventHandler
//    public void onDamageEntityByEntity(EntityDamageByEntityEvent event) {
//        if(!(event.getDamager() instanceof Player)) return;;
//
//        Player damager = (Player) event.getDamager();
//        Entity victim = event.getEntity();
//
//        CustomMob customMob = CustomMob.byAlreadySpawned(victim);
//
//        if(customMob != null) {
//            User user = User.getByUuid(damager.getUniqueId());
//
//            if(!HuntingUtils.canDamage(damager, user, customMob.getNamespacedID())) {
//                event.setCancelled(true);
//            }
//        }
//    }
}
