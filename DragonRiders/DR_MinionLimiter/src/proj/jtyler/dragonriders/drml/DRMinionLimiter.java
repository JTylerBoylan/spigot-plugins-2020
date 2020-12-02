package proj.jtyler.dragonriders.drml;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;

public class DRMinionLimiter extends JavaPlugin {

	private static Permission perms;
	private static DRMinionLimiter instance;
	
	private static File saveFile;
	private static FileConfiguration saveConfig;
	
	private static int default_max;
	
	private static String prefix;
	
	public void onEnable() {
		
		instance = this;
		
		this.saveResource("config.yml", false);
		
		default_max = this.getConfig().getInt("default-max");
		prefix = this.getConfig().getString("prefix");
		
		saveFile = new File(this.getDataFolder(), "save.yml");
		saveConfig = YamlConfiguration.loadConfiguration(saveFile);
		
		this.setupPermissions();
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		this.getCommand("drml-set").setExecutor(new SetCommand());
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			new DRMLPlaceholder().register();
		
	}
	
	public static Plugin getInstance() {
		return instance;
	}
	
	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}
	
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static File getSave() {
    	return saveFile;
    }
    
    public static FileConfiguration getSaveConfig() {
    	return saveConfig;
    }
    
    public static void saveData() {
    	try {
			getSaveConfig().save(getSave());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Permission getPermissions() {
    	return perms;
    }
    
    public static int getMaximumMinions(UUID uuid) {
    	if (saveConfig.getConfigurationSection(uuid.toString()) == null)
    		setMaximumMinions(uuid, default_max);
    	return saveConfig.getInt(uuid.toString() + ".max");
    }
    
    public static int getCurrentMinions(UUID uuid) {
    	if (saveConfig.getConfigurationSection(uuid.toString()) == null)
    		setCurrentMinions(uuid, 0);
    	return saveConfig.getInt(uuid.toString() + ".current");
    }
    
    public static void setMaximumMinions(UUID uuid, int max) {
    	saveConfig.set(uuid.toString() + ".max", max);
    }
    
	public static void setCurrentMinions(UUID uuid, int current) {
		saveConfig.set(uuid.toString() + ".current", current);
	}

	
}
