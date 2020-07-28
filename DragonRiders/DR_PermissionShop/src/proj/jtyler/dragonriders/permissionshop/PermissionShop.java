package proj.jtyler.dragonriders.permissionshop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class PermissionShop extends JavaPlugin {
	
	private static PermissionShop plugin;
	
    private Economy econ;
    private Permission perms;
	
	public void onEnable() {
		
		plugin = this;
		
        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
		
		this.saveResource("config.yml", false);
		
		ConfigHandler.setConfig(this.getConfig());
		BoughtPermission.setSaveFile(new File(this.getDataFolder(), "save.yml"));
		BoughtPermission.clearLoad();
		BoughtPermission.loadSave();
		BoughtPermission.activateAll();
		
		BoughtPermission.runChecker();
		
		this.getCommand("permissionshop").setExecutor(new ShopGUI());
		this.getServer().getPluginManager().registerEvents(new ShopGUI(), this);
		this.getServer().getPluginManager().registerEvents(shopListener, this);
	}
	
	public void onDisable() {
		BoughtPermission.deactivateAll();
		BoughtPermission.saveLoad();
		BoughtPermission.saveSaveFile();
		BoughtPermission.clearLoad();
		BoughtPermission.cancelChecker();
	}
	
	public static PermissionShop getPlugin() {
		return plugin;
	}
	
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

	public void reload() {
		
		BoughtPermission.deactivateAll();
		BoughtPermission.saveLoad();
		BoughtPermission.saveSaveFile();
		BoughtPermission.clearLoad();
		BoughtPermission.cancelChecker();
		
		plugin = this;
		
		this.saveResource("config.yml", false);
		
		this.reloadConfig();
		
		ConfigHandler.setConfig(this.getConfig());
		BoughtPermission.setSaveFile(new File(this.getDataFolder(), "save.yml"));
		BoughtPermission.clearLoad();
		BoughtPermission.loadSave();
		BoughtPermission.activateAll();
		
		BoughtPermission.runChecker();
	}
	
	private Listener shopListener = new Listener() {
		
		@EventHandler
		public void onJoin(PlayerJoinEvent e) {
			for (BoughtPermission bp : BoughtPermission.getLoad(e.getPlayer())) {
				bp.updateOfflinePlayer(e.getPlayer());
				bp.activate();
			}
		}
		
		@EventHandler
		public void onLeave(PlayerQuitEvent e) {
			for (BoughtPermission bp : BoughtPermission.getLoad(e.getPlayer())) {
				bp.deactivate();
			}
		}
		
	};
	
}
