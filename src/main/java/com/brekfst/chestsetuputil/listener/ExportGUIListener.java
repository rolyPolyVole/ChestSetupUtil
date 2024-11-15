package com.brekfst.chestsetuputil.listener;

import com.brekfst.chestsetuputil.ChestSetupUtil;
import com.brekfst.chestsetuputil.model.ChestLocation;
import com.brekfst.chestsetuputil.util.ChatColourUtil;
import com.brekfst.chestsetuputil.util.ExportUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ExportGUIListener implements Listener {
    private final ChestSetupUtil plugin;

    public ExportGUIListener(ChestSetupUtil plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHandler menuHandler)) {
            return;
        }

        event.setCancelled(true); // Prevent item movement

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        //switch statement!!
        //i would make an individual method for each case
        //removeChest(), export(), flipPage()

        if (clickedItem.getType() == Material.CHEST) {
            String locationKey = meta.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "chest_location"), PersistentDataType.STRING);
            if (locationKey != null) {
                String[] parts = locationKey.split(";");
                if (parts.length == 4) {
                    String worldName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);

                    ChestLocation locationToRemove = new ChestLocation(worldName, x, y, z);
                    //maybe you can add a ChestLocation.of(), cuz creating a new instance just for comparision can be kinda confusing
                    //also just looks cleaner imo

                    List<ChestLocation> chestLocations = plugin.getSessionManager().getGameConfig(player.getUniqueId()).getChestLocations();

                    if (chestLocations.remove(locationToRemove)) {
                        player.sendMessage(ChatColourUtil.formatPrefix("&aRemoved chest location."));
                    } else {
                        player.sendMessage(ChatColourUtil.formatPrefix("&cCould not find the chest location to remove."));
                    }

                    // Refresh the GUI
                    int currentPage = menuHandler.page; // getter is more OOP principled
                    new MenuHandler(plugin, chestLocations, currentPage).openInventory(player);
                }

                //maybe error here, parts not being 4 means something went HORRIBLY wrong
            }
        } else if (clickedItem.getType() == Material.LIME_STAINED_GLASS_PANE) {
            // Export and close the inventory
            player.closeInventory();
            performExport(player);
        } else if (clickedItem.getType() == Material.ARROW) {
            String navigation = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "navigation"), PersistentDataType.STRING);
            if (navigation != null) {
                int currentPage = menuHandler.page;
                List<ChestLocation> chestLocations = plugin.getSessionManager().getGameConfig(player.getUniqueId()).getChestLocations();

                if (navigation.equals("next")) {
                    new MenuHandler(plugin, chestLocations, currentPage + 1).openInventory(player);
                } else if (navigation.equals("previous")) {
                    new MenuHandler(plugin, chestLocations, currentPage - 1).openInventory(player);
                }
            }
        }
    }

    private void performExport(Player player) {
        ExportUtil.performExport(plugin, player);
    }
}


