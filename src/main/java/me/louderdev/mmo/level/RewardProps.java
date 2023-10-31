package me.louderdev.mmo.level;

import lombok.Getter;

import java.util.List;


@Getter
public class RewardProps {

    private double requiredLevel;
    private List<String> rewardCmds;

    public RewardProps(double requiredLevel,  List<String> rewardCmds) {
        this.requiredLevel = requiredLevel;
        this.rewardCmds = rewardCmds;
    }

    @Override
    public String toString() {
        return "RewardProps{" +
                "requiredLevel=" + requiredLevel +
                ", rewardCmds=" + rewardCmds +
                '}';
    }
}
