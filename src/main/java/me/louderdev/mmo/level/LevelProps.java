package me.louderdev.mmo.level;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomCrop;
import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class LevelProps {

    private double requiredLevel;
    private String allowedAsString;

    private CustomBlock allowedBlock;
    private CustomStack allowedItem, allowedCropsAsSeed;
    private CustomCrop allowedCrops;

    public LevelProps(double requiredLevel, String allowedAsString, ActionType type) {
        this.requiredLevel = requiredLevel;
        this.allowedAsString = allowedAsString;

        switch (type) {
            case MINING: {
                this.allowedBlock = CustomBlock.getInstance(allowedAsString);
                break;
            }
            case PLATING: {
                this.allowedCropsAsSeed =CustomStack.getInstance(allowedAsString);
                break;
            }
            case CRAFTING: {
                //Parased from string
                this.allowedItem = CustomStack.getInstance(allowedAsString);
                break;
            }
            default: {
                break;
            }
        }
    }
}
