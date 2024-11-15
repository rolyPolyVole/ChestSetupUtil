package com.brekfst.chestsetuputil;

import com.brekfst.chestsetuputil.commands.ChestUtilsCommand;
import com.brekfst.chestsetuputil.listener.ChestClickListener;
import com.brekfst.chestsetuputil.listener.ExportGUIListener;
import com.brekfst.chestsetuputil.manager.PlayerSessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSetupUtil extends JavaPlugin {
    private PlayerSessionManager sessionManager;
    private Gson gson;

    @Override
    public void onEnable() { // nice
        sessionManager = new PlayerSessionManager();
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        ChestUtilsCommand chestUtilsCommand = new ChestUtilsCommand(this);
        getCommand("chestutils").setExecutor(chestUtilsCommand);
        getCommand("chestutils").setTabCompleter(chestUtilsCommand);

        getServer().getPluginManager().registerEvents(new ChestClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ExportGUIListener(this), this);
    }

    public PlayerSessionManager getSessionManager() {
        return sessionManager;
    }

    public Gson getGson() {
        return gson;
    }
}
