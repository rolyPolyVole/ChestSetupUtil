package com.brekfst.chestsetuputil.model;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
    private final List<ChestLocation> chestLocations = new ArrayList<>();

    public void addChestLocation(ChestLocation chestLocation) {
        chestLocations.add(chestLocation);
    }

    public List<ChestLocation> getChestLocations() {
        return chestLocations;
    }
}
