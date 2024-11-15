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

// I would use the GSON library for working with JSON objects. - Enderman

public class ExportUtil {

    // not sure why this is a static util, can probably fit in PlayerSessionManager or another manager

    // Agreed. You should use dependency injection for any class and method that requires the plugin class, or more
    // generally any method that requires the main class of your program. - Enderman
    public static void performExport(ChestSetupUtil plugin, Player player) {
        UUID playerUUID = player.getUniqueId();
        GameConfig config = plugin.getSessionManager().getGameConfig(playerUUID);

        if (config == null || config.getChestLocations().isEmpty()) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cNo chest locations to export."));
            return;
        }

        // start
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
        // end - can be moved to a different method
        // JsonArray chestLocationsArray = getJsonChestLocationsArray(config.getChestLocations());

        JsonObject outputJson = new JsonObject();
        outputJson.add("chestLocations", chestLocationsArray);
        // ^^ maybe this can also be included in the method, so its
        // JsonObject outputJson = createOutputJsonObject(config.getChestLocations());

        //start
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        File outputFile = new File(pluginFolder, "chest_locations_" + player.getName() + "_" + timestamp + ".json");
        //end - this can also be its own method
        // File outputFile = createOutputFile(plugin, player.getName());


        // this here below could also be its own method, for example writeToOutputFile(plugin, outputJson, outputFile) throws IOException
        try (FileWriter writer = new FileWriter(outputFile)) {
            plugin.getGson().toJson(outputJson, writer);

            // though these 2 lines below would be outside of the method
            player.sendMessage(ChatColourUtil.formatPrefix("&aChest locations exported successfully. You can copy and paste this into your config."));
            plugin.getSessionManager().endSession(playerUUID);
        } catch (IOException e) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cAn error occurred while exporting the configuration. Please check the server logs for more details."));
            plugin.getLogger().log(Level.SEVERE, "An error occurred while exporting chest locations for player " + player.getName(), e);
        }

        // when separating into methods, the whole point is that each method has a single responsibility
        // createOutputJsonObject - takes in the config and returns a JsonObject
        // createOutputFile - takes in the player name and creates and returns a File with a good name
        // writeToOutputFile - takes in the JsonObject and the File and writes the JsonObject to the File
    }
}
