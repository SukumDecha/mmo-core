package me.louderdev.mmo.command;

import lombok.Setter;
import me.louderdev.mmo.MmoCore;
import me.louderdev.mmo.utils.CC;
import me.louderdev.mmo.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand extends BukkitCommand {
   private boolean forPlayersOnly;
   private String testPermission;

   @Setter
   private boolean async;

   public BaseCommand(String name) {
      this(name, (new ArrayList()));
   }

   public BaseCommand(String name, List aliases) {
      this(name, aliases, "", false);
   }

   public BaseCommand(String name, boolean forPlayersOnly) {
      this(name, new ArrayList(), "", forPlayersOnly);
   }

   public BaseCommand(String name, List aliases, String testPermission, boolean forPlayersOnly) {
      super(name);
      this.setName(name);
      this.setAliases(aliases);
      this.testPermission = testPermission;

      this.forPlayersOnly = forPlayersOnly;

      try {
         Field f;
         f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         f.setAccessible(true);
         CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
         commandMap.register(name, this);
      } catch (NoSuchFieldException | IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player) && this.forPlayersOnly) {
         sender.sendMessage(CC.RED + "Only player can use this command");
         return false;
      } else if(this.forPlayersOnly && sender.isOp()) {
         if (this.async) {
            TaskUtils.runAsync( () -> {
               try {
                  this.execute(sender, args);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               } catch (InvalidConfigurationException e) {
                  throw new RuntimeException(e);
               }
            });
         } else {
            try {
               this.execute(sender, args);
            } catch (IOException e) {
               throw new RuntimeException(e);
            } catch (InvalidConfigurationException e) {
               throw new RuntimeException(e);
            }
         }
         return true;
      } else if (!sender.hasPermission(testPermission)) {
         sender.sendMessage(CC.RED + "No permission");
         return false;
      } else {
         if (this.async) {
            TaskUtils.runAsync( () -> {
               try {
                  this.execute(sender, args);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               } catch (InvalidConfigurationException e) {
                  throw new RuntimeException(e);
               }
            });
         } else {
            try {
               this.execute(sender, args);
            } catch (IOException e) {
               throw new RuntimeException(e);
            } catch (InvalidConfigurationException e) {
               throw new RuntimeException(e);
            }
         }

         return true;
      }
   }

   public abstract void execute(CommandSender var1, String[] var2) throws IOException, InvalidConfigurationException;
}
