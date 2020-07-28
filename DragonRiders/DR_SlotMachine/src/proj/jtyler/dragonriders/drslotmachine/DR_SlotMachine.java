package proj.jtyler.dragonriders.drslotmachine;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class DR_SlotMachine extends JavaPlugin {

	private static DR_SlotMachine instance;
	
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
	
	public void onEnable() {
		instance = this;
		
		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			this.getLogger().info("No config.yml found. Generating new file...");
			this.saveResource("config.yml", true);
		}
		if (!(new File(this.getDataFolder(), "slotmachine.yml").exists())) {
			this.getLogger().info("No slotmachine.yml found. Generating new file...");
			this.saveResource("slotmachine.yml", true);
		}
		
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		this.getCommand("dragonslots").setExecutor(SlotMachineUtil.commandExecutor());
		
		this.getServer().getPluginManager().registerEvents(SlotMachineUtil.listener(), this);
	}
	
	
	public static DR_SlotMachine get() {
		return instance;
	}
	
	public String getPrefix() {
		return this.getConfig().getString("prefix");
	}
	
	public long getCooldown() {
		return this.getConfig().getInt("cooldown-s")*20;
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
