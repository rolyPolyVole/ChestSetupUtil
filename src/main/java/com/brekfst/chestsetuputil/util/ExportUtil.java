package com.brekfst.chestsetuputil.util;

import com.brekfst.chestsetuputil.ChestSetupUtil;
import com.brekfst.chestsetuputil.model.ChestLocation;
import com.brekfst.chestsetuputil.model.GameConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ExportUtil {

    public static void performExport(ChestSetupUtil plugin, Player player) {
        UUID playerUUID = player.getUniqueId();
        GameConfig config = plugin.getSessionManager().getGameConfig(playerUUID);

        if (config == null || config.getChestLocations().isEmpty()) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cNo chest locations to export."));
            return;
        }

        JsonArray chestLocationsArray = new JsonArray();
        List<ChestLocation> chestLocations = config.getChestLocations();

        for (ChestLocation location : chestLocations) {
            JsonObject chestObject = new JsonObject();
            chestObject.addProperty("worldName", location.getWorldName());
            chestObject.addProperty("x", location.getX());
            chestObject.addProperty("y", location.getY());
            chestObject.addProperty("z", location.getZ());
            chestObject.addProperty("maxTotalValue", 69);
            chestObject.addProperty("maxValuePerItem", 69);
            chestLocationsArray.add(chestObject);
        }

        JsonObject outputJson = new JsonObject();
        outputJson.add("chestLocations", chestLocationsArray);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        File outputFile = new File(pluginFolder, "chest_locations_" + player.getName() + "_" + timestamp + ".json");

        try (FileWriter writer = new FileWriter(outputFile)) {
            plugin.getGson().toJson(outputJson, writer);
            player.sendMessage(ChatColourUtil.formatPrefix("&aChest locations exported successfully. You can copy and paste this into your config."));
            plugin.getSessionManager().endSession(playerUUID);
        } catch (IOException e) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cAn error occurred while exporting the configuration. Please check the server logs for more details."));
            plugin.getLogger().log(Level.SEVERE, "An error occurred while exporting chest locations for player " + player.getName(), e);
        }
    }
}
