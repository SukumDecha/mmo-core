package me.louderdev.mmo.level.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {

    PLANTING("ปลูก/เก็บเกี่ยว"),
    MINING("ขุด"),
    FISHING("ตกปลา"),
    HUNTING("ล่าสัตว์"),
    CRAFTING("คราฟ");

    @Getter private String name;
}
