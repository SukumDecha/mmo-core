package me.louderdev.mmo.level;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {

    PLANTING("plant"),
    MINING("mine"),
    FISHING("fish"),
    HUNTING("kill"),
    CRAFTING("craft");

    @Getter private String name;
}
