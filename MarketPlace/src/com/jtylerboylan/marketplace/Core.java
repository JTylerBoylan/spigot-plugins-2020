package com.jtylerboylan.marketplace;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.jtylerboylan.marketplace.commands.BusinessCommand;
import com.jtylerboylan.marketplace.commands.BusinessCreateCommand;
import com.jtylerboylan.marketplace.commands.BusinessDeleteCommand;
import com.jtylerboylan.marketplace.commands.EntityCommand;
import com.jtylerboylan.marketplace.commands.EntityCreateCommand;
import com.jtylerboylan.marketplace.commands.EntityDeleteCommand;
import com.jtylerboylan.marketplace.data.BusinessData;
import com.jtylerboylan.marketplace.data.EntityData;
import com.jtylerboylan.marketplace.data.ItemData;
import com.jtylerboylan.marketplace.data.MarketData;
import com.jtylerboylan.marketplace.events.ChatGUIEvent;
import com.jtylerboylan.marketplace.events.ClickGUIEvent;
import com.jtylerboylan.marketplace.events.GUICloseEvent;
import com.jtylerboylan.marketplace.events.MarketCreateEvent;
import com.jtylerboylan.marketplace.events.MarketOpenEvent;

import net.milkbowl.vault.economy.Economy;

public class Core extends JavaPlugin {

	private static Plugin plugin;
	
	private static FileConfiguration server_config;
	
    private static Economy econ = null;
    
    private static final Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		plugin = this;
		
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		server_config = this.getConfig();
		
		getCommand("createentity").setExecutor(new EntityCreateCommand());
		getCommand("entity").setExecutor(new EntityCommand());
		getCommand("deleteentity").setExecutor(new EntityDeleteCommand());
		getCommand("createbusiness").setExecutor(new BusinessCreateCommand());
		getCommand("business").setExecutor(new BusinessCommand());
		getCommand("deletebusiness").setExecutor(new BusinessDeleteCommand());
		
		registerEvents(new ClickGUIEvent(), new ChatGUIEvent(), new GUICloseEvent(), new MarketCreateEvent(), new MarketOpenEvent());
		
		EntityData.load();
		BusinessData.load();
		MarketData.load();
		ItemData.load();
	}
	
	public void onDisable() {
		BusinessData.save();
		MarketData.save();
		ItemData.save();
		EntityData.save();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileConfiguration getServerConfig() {
		return server_config;
	}
	
	private void registerEvents(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
	
	public static void DEBUG(String txt) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.sendMessage("DEBUG: " + txt);
		}
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static Economy getEconomy() {
    	return econ;
    }
	
}
