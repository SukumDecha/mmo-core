package me.louderdev.mmo.level;

import dev.lone.itemsadder.api.CustomCrop;

import lombok.Getter;

import java.util.List;


@Getter
public class LevelProps {

    private double requiredLevel;
    private String allowedAsString;
    private List<String> cmds;

    public LevelProps(double requiredLevel, String allowedAsString, List<String> cmds) {
        this.requiredLevel = requiredLevel;
        this.allowedAsString = allowedAsString;
        this.cmds = cmds;

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

    @Override
    public String toString() {
        return "LevelProps{" +
                "requiredLevel=" + requiredLevel +
                ", allowedAsString='" + allowedAsString + '\'' +
                ", cmds=" + cmds +
                '}';
    }
}
