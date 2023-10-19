package me.louderdev.mmo.command.impl;


import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.command.BaseCommand;
import me.louderdev.mmo.level.Level;
import me.louderdev.mmo.user.User;
import me.louderdev.mmo.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AdminCommand extends BaseCommand {

    private MmoCore plugin = MmoCore.getInstance();

    public AdminCommand() {
        super("madmin", Arrays.asList(new String("mmoadmn")), "*");
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws IOException, InvalidConfigurationException {
        if (args.length == 0) {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&e/madmin reload &7- &6to reload the configs files"));
            sender.sendMessage(CC.translate("&e/madmin list &7- &6show all levels"));
            sender.sendMessage(CC.CHAT_BAR);
        } else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {

                    plugin.reload();
                    sender.sendMessage(CC.GREEN + "The Mmo-level's config has been reloaded.");
                    return;
                } else if(args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(CC.CHAT_BAR);
                    for (Level level : Level.getAllLevels()) {
                        sender.sendMessage("");
                        sender.sendMessage(CC.translate(level.coloredToString()));
                    }
                    sender.sendMessage(CC.CHAT_BAR);
                } else if(args[0].equalsIgnoreCase("self")) {
                    Player player = (Player) sender;
                    User user = User.getByUuid(player.getUniqueId());

                    sender.sendMessage(CC.CHAT_BAR);
                    for (Level level : user.getAllLevels()) {
                        sender.sendMessage(CC.translate(level.coloredToString()));
                    }
                    sender.sendMessage(CC.CHAT_BAR);

                } else {
                    sender.sendMessage(CC.CHAT_BAR);
                    sender.sendMessage("&e/madmin reload &7- &6to reload the configs files");
                    sender.sendMessage(CC.CHAT_BAR);
                }
            }
        }
    }
}
