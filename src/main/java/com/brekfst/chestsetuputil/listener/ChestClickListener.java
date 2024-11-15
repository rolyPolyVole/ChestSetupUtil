package com.brekfst.chestsetuputil.listener;

import com.brekfst.chestsetuputil.ChestSetupUtil;
import com.brekfst.chestsetuputil.model.ChestLocation;
import com.brekfst.chestsetuputil.model.GameConfig;
import com.brekfst.chestsetuputil.util.ChatColourUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ChestClickListener implements Listener {
    private final ChestSetupUtil plugin;

    public ChestClickListener(ChestSetupUtil plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            // getItemInMainHand() never nulls
            // uh at this point maybe isValidItem() is needed

            // Checking the persistent data here is enough and I doubt there's much of a performance difference. - Enderman
            if (item != null && item.getType() == Material.WOODEN_HOE &&
                    item.getItemMeta() != null &&
                    item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "chest_setup_tool"), PersistentDataType.BYTE)) {

                event.setCancelled(true); // Prevent chest from opening

                GameConfig config = plugin.getSessionManager().getGameConfig(player.getUniqueId());
                if (config == null) { // No active session
                    player.sendMessage(ChatColourUtil.formatPrefix("&cYou don't have an active session. Use /chestutils start to begin."));
                    return;
                }

                //for consistency, i'd use Block block = event.getClickedBlock(), and get the world name off the block
                ChestLocation chestLocation = new ChestLocation(
                        player.getWorld().getName(),
                        event.getClickedBlock().getX(),
                        event.getClickedBlock().getY(),
                        event.getClickedBlock().getZ()
                );
                config.addChestLocation(chestLocation);

                //somewhere here you can add the check to see if the exact same chest location already exists

                player.sendMessage(ChatColourUtil.formatPrefix("&aChest location added!"));
            }
        }
    }

}
