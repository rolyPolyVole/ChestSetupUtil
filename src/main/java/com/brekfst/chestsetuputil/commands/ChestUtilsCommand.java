package com.brekfst.chestsetuputil.commands;

import com.brekfst.chestsetuputil.ChestSetupUtil;
import com.brekfst.chestsetuputil.listener.MenuHandler;
import com.brekfst.chestsetuputil.model.GameConfig;
import com.brekfst.chestsetuputil.util.ChatColourUtil;
import com.brekfst.chestsetuputil.util.ExportUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChestUtilsCommand implements CommandExecutor, TabCompleter {
    private final ChestSetupUtil plugin;

    public ChestUtilsCommand(ChestSetupUtil plugin) {
        this.plugin = plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    // personally I just hate intelliJ warnings, so I use stuff like @SuppressWarnings and other annotations
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { // pattern variable
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        // you can use a pattern variable here
        Player player = (Player) sender;

        // Check for permission
        if (!player.hasPermission("chestutils.admin")) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColourUtil.formatPrefix("&aUsage: /chestutils <tool|start|export>"));
            return true;
        }

        //could be replaced with enhanced switch but that's more personal preference
        switch (args[0].toLowerCase()) {
            case "tool":
                giveChestTool(player);
                break;
            case "start":
                startSession(player);
                break;
            case "export":
                if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                    // here's a trick
                    // variable.equals("yourString") will throw NPE if variable is null
                    // "yourString".equalsIgnoreCase(variable) will not throw NPE
                    // not really applicable here since args is a string array but it's good to know

                    performExport(player);
                } else {
                    openExportGui(player);
                }
                break;
            default:
                player.sendMessage(ChatColourUtil.formatPrefix("&cUnknown subcommand. Use /chestutils <tool|start|export>"));
        }

        return true;
    }

    private void giveChestTool(Player player) {
        ItemStack chestTool = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = chestTool.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Chest Setup Tool");

        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Right-click a chest to add it to",
                ChatColor.LIGHT_PURPLE + "configuration."
        ));

        //meta.setEnchantmentGlintOverride(true); alternative
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "chest_setup_tool"),
                PersistentDataType.BYTE, (byte) 1
        );

        chestTool.setItemMeta(meta);

        player.getInventory().addItem(chestTool);
        player.sendMessage(ChatColourUtil.formatPrefix("You received the Chest Setup Tool!"));
    }

    private void startSession(Player player) {
        plugin.getSessionManager().startSession(player.getUniqueId());
        player.sendMessage(ChatColourUtil.formatPrefix("Chest configuration session started."));
    }

    private void openExportGui(Player player) {
        GameConfig config = plugin.getSessionManager().getGameConfig(player.getUniqueId());

        if (config == null || config.getChestLocations().isEmpty()) {
            player.sendMessage(ChatColourUtil.formatPrefix("&cNo chest locations to export."));
            return;
        }

        // Open the export GUI starting from page 0
        new MenuHandler(plugin, config.getChestLocations(), 0).openInventory(player);
    }

    private void performExport(Player player) {
        ExportUtil.performExport(plugin, player);
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("tool", "start", "export");
        }
        return Collections.emptyList();
    }
}
