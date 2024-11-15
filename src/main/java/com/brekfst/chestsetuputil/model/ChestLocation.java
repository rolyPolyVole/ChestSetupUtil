package com.brekfst.chestsetuputil.model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// could be a record but again that's just personal preference
public class ChestLocation implements ConfigurationSerializable {
    private final String worldName;
    private final int x, y, z;

    public ChestLocation(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("worldName", worldName);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }

    public static ChestLocation deserialize(Map<String, Object> map) {
        return new ChestLocation(
                (String) map.get("worldName"),
                (int) map.get("x"),
                (int) map.get("y"),
                (int) map.get("z")
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChestLocation)) return false;
        ChestLocation other = (ChestLocation) obj;
        return x == other.x && y == other.y && z == other.z && worldName.equals(other.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }
}
