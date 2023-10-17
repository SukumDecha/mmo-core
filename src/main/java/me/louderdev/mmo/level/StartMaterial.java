package me.louderdev.mmo.level;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Setter
@Getter
public class StartMaterial {

    private boolean custom;
    private ItemStack itemStack;
    private CustomStack customStack;
    private CustomBlock customBlock;

    public StartMaterial(boolean isCustom) {
        this.custom = isCustom;
    }

}
