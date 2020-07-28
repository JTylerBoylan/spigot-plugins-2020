package proj.jtyler.dragonriders.luckyblock;

import java.io.File;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LuckyBlockDR extends JavaPlugin implements Listener {

	private static Plugin plugin = null;
	
	private static String prefix = null;
	
	private static File rewards_file = null;
	private static FileConfiguration rewards_config = null;
	
	private static Material block_type = null;
	
	public void onEnable() {
		plugin = this;
		
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		prefix = getConfig().getString("prefix");
		
		this.saveResource("rewards.yml", false);
		
		rewards_file = new File(this.getDataFolder(), "rewards.yml");
		rewards_config = YamlConfiguration.loadConfiguration(rewards_file);
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		Set<String> reward_ids = rewards_config.getKeys(false);
		Reward.addRewards(reward_ids);
	}
	
	public void reload() {
		
		plugin.reloadConfig();
		
		plugin = null;
		prefix = null;
		rewards_file = null;
		rewards_config = null;
		block_type = null;
		
		Reward.reload();
		
		plugin = this;
		
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		prefix = getConfig().getString("prefix");
		
		this.saveResource("rewards.yml", false);
		
		rewards_file = new File(this.getDataFolder(), "rewards.yml");
		rewards_config = YamlConfiguration.loadConfiguration(rewards_file);
		
		Set<String> reward_ids = rewards_config.getKeys(false);
		Reward.addRewards(reward_ids);
		
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileConfiguration getRewardsConfig() {
		return rewards_config;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static Material getLuckyBlockType() {
		if (block_type == null)
			block_type = Material.valueOf(getPlugin().getConfig().getString("lucky-block-type"));
		return block_type;
	}
	
	@EventHandler
	public void onLuckyBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == getLuckyBlockType()) {
			e.setDropItems(false);
			Reward reward = Reward.randomReward();
			if (reward == null)
				return;
			reward.give(e.getPlayer());
		}
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (label.equalsIgnoreCase("luckyblockdr")) {
    		if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("luckyblockdr.reload")) {
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " Reloading..."));
    			reload();
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + " Reloaded."));
    			return true;
    		} else {
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + " Version " + this.getDescription().getVersion()));
    			return true;
    		}
    	}
        return false;
    }
	
	
}
