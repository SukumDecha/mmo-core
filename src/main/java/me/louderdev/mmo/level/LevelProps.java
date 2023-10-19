package me.louderdev.mmo.level;

import dev.lone.itemsadder.api.CustomCrop;

import lombok.Getter;

import java.util.List;


@Getter
public class LevelProps {

    private int chance;
    private double requiredLevel;
    private String allowedAsString, rarity;
    private List<String> cmds;

    public LevelProps(double requiredLevel, String allowedAsString, List<String> cmds, int chance
    , String rarity) {
        this.requiredLevel = requiredLevel;
        this.allowedAsString = allowedAsString;
        this.cmds = cmds;
        this.chance = chance;
        this.rarity = rarity;
        /*
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

         */
    }

    public boolean isCustom() {
        return allowedAsString.contains(":");
    }

    @Override
    public String toString() {
        return "LevelProps{" +
                "chance=" + chance +
                ", requiredLevel=" + requiredLevel +
                ", allowedAsString='" + allowedAsString + '\'' +
                ", cmds=" + cmds +
                '}' + "\n";
    }
}
