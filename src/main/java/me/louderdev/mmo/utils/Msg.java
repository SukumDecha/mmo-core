package me.louderdev.mmo.utils;

import lombok.AllArgsConstructor;
import me.louderdev.mmo.MmoCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

@AllArgsConstructor
public enum Msg {
    REQUIRED_MORE_LEVEL("REQUIRED_MORE_LEVEL"),
    LEVEL_UP("LEVEL_UP"),
    ITEM_DROP("ITEM_DROP"),
    EARN_XP("EARN_XP");

    private String path;


    public String format(Object ...objects) {
        return new MessageFormat(MmoCore.getInstance().getMessageFile().getString(path)).format(objects);
    }

    public void sendMessage(Player player, Object ...objects) {
        player.sendMessage(CC.translate(format(objects)));
    }
}
