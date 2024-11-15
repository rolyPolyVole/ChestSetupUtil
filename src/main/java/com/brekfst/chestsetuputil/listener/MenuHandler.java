package com.brekfst.chestsetuputil.listener;

import com.brekfst.chestsetuputil.ChestSetupUtil;
import com.brekfst.chestsetuputil.model.ChestLocation;
import com.brekfst.chestsetuputil.util.ChatColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MenuHandler implements InventoryHolder { // smart, implementing InventoryHolder
    private final Inventory menu;
    private final ChestSetupUtil plugin;
    private final List<ChestLocation> chestLocations;
    public final int page;
    private final int totalPages;

    private static final int INVENTORY_SIZE = 54;
    private static final int EXPORT_BUTTON_SLOT = 49;
    private static final int PREV_BUTTON_SLOT = 45;
    private static final int NEXT_BUTTON_SLOT = 53;

    public MenuHandler(ChestSetupUtil plugin, List<ChestLocation> chestLocations, int page) {
        this.plugin = plugin;
        this.chestLocations = chestLocations;
        this.page = page;
        this.totalPages = (int) Math.ceil((double) chestLocations.size() / getMaxItemsPerPage());
        String inventoryTitle = ChatColourUtil.formatMessage("&8Export Chest Locations");
        this.menu = Bukkit.createInventory(this, INVENTORY_SIZE, inventoryTitle);
        initializeMenu();
    }

    private void initializeMenu() { // nice
        ItemStack glassPane = createGlassPane();
        setBorderSlots(glassPane);
        addExportButton();
        populateChestItems();
    }

    private int getMaxItemsPerPage() { // if you're using static constants might aswell make this one static too
        return 28; // 4 rows of 7 items
    }

    private void setBorderSlots(ItemStack glassPane) {
        // Top and bottom borders
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, glassPane); // Top row
            menu.setItem(45 + i, glassPane); // Bottom row
        }
        // Left and right borders
        for (int i = 9; i <= 44; i += 9) {
            menu.setItem(i, glassPane); // Left column
            menu.setItem(i + 8, glassPane); // Right column
        }

        // that's good, here's another way you could do it if your gui was smaller
        // for (int i : new int[]{0, 69, 420}) {...}
    }

    private ItemStack createGlassPane() {
        ItemStack glassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = glassPane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(" ");
            glassPane.setItemMeta(paneMeta);
        }
        return glassPane;
    }

    private void addExportButton() {
        ItemStack exportButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta exportMeta = exportButton.getItemMeta();
        if (exportMeta != null) {
            exportMeta.setDisplayName(ChatColourUtil.formatMessage("&aExport Locations"));
            exportButton.setItemMeta(exportMeta);
        }
        menu.setItem(EXPORT_BUTTON_SLOT, exportButton);
    }

    private void populateChestItems() {
        List<Integer> innerSlots = new ArrayList<>();
        // Inner slots excluding borders and buttons
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                int slot = row * 9 + col;
                if (slot != EXPORT_BUTTON_SLOT && slot != PREV_BUTTON_SLOT && slot != NEXT_BUTTON_SLOT) {
                    innerSlots.add(slot);
                }
            }
        }

        // what the heck
        // even defining all the slots as a static field is more readable AND more efficient
        // because you don't perform 28 iterations with an operation to a list every time
        // private static final int[] INNER_SLOTS = new int[]{
        //     10, 11, 12, 13, 14, 15, 16,
        //     19, 20, 21, 22, 23, 24, 25,
        //     28, 29, 30, 31, 32, 33, 34,
        //     37, 38, 39, 40, 41, 42, 43
        // }

        int itemsPerPage = innerSlots.size();
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, chestLocations.size());

        List<ChestLocation> pageItems = chestLocations.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            ChestLocation location = pageItems.get(i);
            ItemStack chestItem = createChestItem(location);
            menu.setItem(innerSlots.get(i), chestItem);
        }

        addNavigationButtons();
    }

    private void addNavigationButtons() {
        // Previous Page Button
        if (page > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColourUtil.formatMessage("&aPrevious Page"));
                prevMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "navigation"), PersistentDataType.STRING, "previous");
                prevButton.setItemMeta(prevMeta);
            }
            menu.setItem(PREV_BUTTON_SLOT, prevButton);
        }

        // Next Page Button
        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColourUtil.formatMessage("&aNext Page"));
                nextMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "navigation"), PersistentDataType.STRING, "next");
                nextButton.setItemMeta(nextMeta);
            }
            menu.setItem(NEXT_BUTTON_SLOT, nextButton);
        }
    }

    private ItemStack createChestItem(ChestLocation location) {
        ItemStack chestItem = new ItemStack(Material.CHEST);
        ItemMeta chestMeta = chestItem.getItemMeta();

        //i'd make x y and z variables
        // double x = location.getX();
        // double y = location.getY();
        // double z = location.getZ();

        // in my projects, I usually have a class that just has every single NamespacedKey I use as a static constant
        // public final class Keys {
        //     public static final NamespacedKey CHEST_LOCATION = new NamespacedKey("chest_setup_util", "chest_location");
        // }

        if (chestMeta != null) {
            chestMeta.setDisplayName(ChatColourUtil.formatMessage(
                    "&eLocation: &7" + location.getX() + ", " + location.getY() + ", " + location.getZ()));
            chestMeta.setLore(List.of(ChatColourUtil.formatMessage("&7Click to remove")));
            // Store the location coordinates in the item's metadata
            String locationKey = location.getWorldName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
            chestMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "chest_location"), PersistentDataType.STRING, locationKey);
            chestItem.setItemMeta(chestMeta);
        }

        return chestItem;
    }

    public void openInventory(Player player) {
        player.openInventory(menu);
    }

    @Override
    public Inventory getInventory() {
        return menu;
    }
}