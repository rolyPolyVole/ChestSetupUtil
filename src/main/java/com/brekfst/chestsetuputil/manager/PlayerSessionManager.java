package com.brekfst.chestsetuputil.manager;

import com.brekfst.chestsetuputil.model.GameConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSessionManager { // all good
    private final Map<UUID, GameConfig> activeConfigs = new HashMap<>();

    public void startSession(UUID playerUUID) {
        activeConfigs.put(playerUUID, new GameConfig());
    }

    public GameConfig getGameConfig(UUID playerUUID) {
        return activeConfigs.get(playerUUID);
    }

    public void endSession(UUID playerUUID) {
        activeConfigs.remove(playerUUID);
    }
}


