package me.louderdev.mmo.level.props;

import lombok.Getter;

import java.util.List;


@Getter
public class RewardProps {

    private final double requiredLevel;
    private final List<String> rewardCmds;

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
