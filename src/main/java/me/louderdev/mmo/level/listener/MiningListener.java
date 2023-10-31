package me.louderdev.mmo.level.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.file.ConfigFile;
import me.louderdev.mmo.utils.type.MiningUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MiningListener implements Listener {

    private ConfigFile cachedConfig;
    @Getter public static List<String> placedBlock = new ArrayList<>();

    public MiningListener(ConfigFile cached) {
        this.cachedConfig = cached;
        if(cachedConfig.contains("placed_blocks")) {
            placedBlock = cachedConfig.getStringList("placed_blocks");
        }
    }

    @EventHandler
    public void onCustomBlockBreak(CustomBlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = User.getByUuid(player.getUniqueId());

        //prevent player from breaking block above their levels
        if (!MiningUtils.canBreakBlock(user, player, event.getNamespacedID())) {
            event.setCancelled(true);
            return;
        }

        if(isBlockAlreadyPlaced(event.getBlock().getLocation())) return;

        MiningUtils.handleMining(player, user, event.getNamespacedID());
    }

    @EventHandler
    public void onNormalBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(CustomBlock.byAlreadyPlaced(event.getBlock()) != null) return;;

        User user = User.getByUuid(player.getUniqueId());

        //prevent player from breaking block above their levels
        if (!MiningUtils.canBreakBlock(user, player, event.getBlock().getType().name())) {
            event.setCancelled(true);
            return;
        }

        if(isBlockAlreadyPlaced(event.getBlock().getLocation())) return;

        MiningUtils.handleMining(player, user, event.getBlock().getType().name());
    }

    @EventHandler
    public void onCustomPlace(CustomBlockPlaceEvent event) {
        if(isBlockAlreadyPlaced(event.getBlock().getLocation())
        || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        saveBlockLocation(event.getBlock().getLocation());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(isBlockAlreadyPlaced(event.getBlock().getLocation())
                || event.getPlayer().getGameMode() == GameMode.CREATIVE
        || CustomBlock.byAlreadyPlaced(event.getBlock()) != null) return;

        if(MiningUtils.isInConfigBlock(User.getByUuid(event.getPlayer().getUniqueId())
        , event.getBlock().getType().name()))
        saveBlockLocation(event.getBlock().getLocation());
    }

    private boolean isBlockAlreadyPlaced(Location location) {
        // Check if the location is in the list of placed blocks
        return placedBlock.contains(locationToString(location));
    }

    private void saveBlockLocation(Location location) {
        // Get the list of stored locations from the data file
        // Convert the location to a string and add it to the list
        placedBlock.add(locationToString(location));
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    private void saveDataConfig() {
        cachedConfig.save();
    }
}
