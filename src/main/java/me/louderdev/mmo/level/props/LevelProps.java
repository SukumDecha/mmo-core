package me.louderdev.mmo.level.props;

import dev.lone.itemsadder.api.CustomCrop;

import lombok.Getter;

import java.util.List;


@Getter
public class LevelProps {

    private final int chance;
    private final double requiredLevel;
    private final String allowedAsString, rarity;
    private final List<String> cmds;

    public LevelProps(double requiredLevel, String allowedAsString, List<String> cmds, int chance
    , String rarity) {
        this.requiredLevel = requiredLevel;
        this.allowedAsString = allowedAsString;
        this.cmds = cmds;
        this.chance = chance;
        this.rarity = rarity;
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
