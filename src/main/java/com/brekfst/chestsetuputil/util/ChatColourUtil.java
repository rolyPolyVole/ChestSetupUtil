package com.brekfst.chestsetuputil.util;

import org.bukkit.ChatColor;

public class ChatColourUtil {
    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.AQUA + "ChestUtil" + ChatColor.GRAY + "] " + ChatColor.RESET;

    public static String formatPrefix(String message) {
        return PREFIX + ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
