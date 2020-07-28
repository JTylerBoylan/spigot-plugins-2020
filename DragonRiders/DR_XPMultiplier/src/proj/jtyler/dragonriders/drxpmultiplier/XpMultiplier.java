package proj.jtyler.dragonriders.drxpmultiplier;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;

public class XpMultiplier extends JavaPlugin {

	public static XpMultiplier instance;
	private File data_file;
	private FileConfiguration data_config;
	
	public void onEnable() {
		instance = this;
		
		data_file = new File(this.getDataFolder(), "data.yml");
		data_config = YamlConfiguration.loadConfiguration(data_file);
		
		this.saveResource("config.yml", false);
		
		this.getServer().getPluginManager().registerEvents(XPListener(), this);
		this.getCommand("xpmultiplier").setExecutor(XPMCommand());
		
		PlaceholderAPI.registerExpansion(new XpMultiplierPlaceholder());
	}
	
	public void onDisable() {
	}
	
	public int getMultiplierLevel(Player player) {
		if (data_config.getInt(player.getUniqueId().toString()) == 0)
			setMultiplierLevel(player, 1);
		return data_config.getInt(player.getUniqueId().toString());
	}
	
	public void setMultiplierLevel(Player player, int level) {
		data_config.set(player.getUniqueId().toString(), level);
		try {
			data_config.save(data_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public float getXpMultiplier(int level) {
		return (float) this.getConfig().getDouble("xp-multiplier-levels." + level);
	}
	
	public int getMaxLevel() {
		return this.getConfig().getInt("max-level");
	}
	
	public Listener XPListener() {
		return new Listener() {
			@EventHandler
			public void xpEvent(PlayerExpChangeEvent e) {
				e.setAmount((int)(e.getAmount()*getXpMultiplier(getMultiplierLevel(e.getPlayer()))));
			}
		};
	}
	
	public CommandExecutor XPMCommand() {
		return new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				String usage = "/xpm <get/set/upgrade> <player> <level>";
				String prefix = ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f ");
				if (args.length < 2 || (args.length == 2 && args[0].equalsIgnoreCase("set"))) {
					sender.sendMessage(prefix + "Usage: " + usage);
					return true;
				}
				if (args[0].equalsIgnoreCase("get")) {
					if (!sender.hasPermission("drxpmultiplier.get")) {
						sender.sendMessage(prefix + "You don't have permission for this command.");
						return true;
					}
					String player_name = args[1];
					Player player = Bukkit.getPlayer(player_name);
					if (player == null) {
						sender.sendMessage(prefix + "That player isn't online");
					}
					int level = getMultiplierLevel(player);
					sender.sendMessage(prefix +  player.getName() + "'s xp multiplier level: " + level + ". Multiplier: x" + getXpMultiplier(level));
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (!sender.hasPermission("drxpmultiplier.set")) {
						sender.sendMessage(prefix + "You don't have permission for this command.");
						return true;
					}
					String player_name = args[1];
					Player player = Bukkit.getPlayer(player_name);
					if (player == null) {
						sender.sendMessage(prefix + "That player isn't online");
					}
					int level = 0;
					try {
						level = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage(prefix + "Invalid level: " + args[1]);
						return true;
					}
					
					if (level > getMaxLevel()) {
						sender.sendMessage(prefix + "Highest xp multiplier level is " + getMaxLevel() + ".");
						return true;
					}
					if (level < 1) {
						sender.sendMessage(prefix + "Lowest xp multiplier level is 1.");
						return true;
					}
					
					setMultiplierLevel(player, level);
					sender.sendMessage(prefix + "Set " + player.getName() + "'s multiplier level to " + level);
					
					return true;
				}
				
				if (args[0].equalsIgnoreCase("upgrade")) {
					if (!sender.hasPermission("drxpmultiplier.upgrade")) {
						sender.sendMessage(prefix + "You don't have permission for this command.");
						return true;
					}
					String player_name = args[1];
					Player player = Bukkit.getPlayer(player_name);
					if (player == null) {
						sender.sendMessage(prefix + "That player isn't online");
					}
					int level = getMultiplierLevel(player) + 1;
					
					if (level > getMaxLevel()) {
						sender.sendMessage(prefix + player.getName() + " is max level.");
						return true;
					}
					
					setMultiplierLevel(player, level);
					sender.sendMessage(prefix + "Set " + player.getName() + "'s multiplier level to " + level);
					
					return true;
				}
				
				return false;
			}
		};
	}
	
	
	
}
