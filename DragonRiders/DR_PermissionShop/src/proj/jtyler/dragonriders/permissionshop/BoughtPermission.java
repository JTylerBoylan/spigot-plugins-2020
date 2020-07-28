package proj.jtyler.dragonriders.permissionshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BoughtPermission {
	
	private static ArrayList<BoughtPermission> load;
	
	private static File saveFile = null;
	private static FileConfiguration saveConfig = null;
	
	private static int checker;
	private static String prefix;
	
	private OfflinePlayer player;
	private long expires;
	private String node;
	
	public BoughtPermission(OfflinePlayer player, long expires, String node) {
		this.player = player;
		this.expires = expires;
		this.node = node;
	}
	
	public static void setSaveFile(File f) {
		saveFile = f;
		saveConfig = YamlConfiguration.loadConfiguration(saveFile);
	}
	
	public static void saveSaveFile() {
		if (saveFile != null && saveConfig != null)
			try {
				saveConfig.save(saveFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void clearLoad() {
		load = new ArrayList<BoughtPermission>();
	}
	
	public static void loadSave() {
		if (saveConfig.getStringList("saves") == null)
			return;
		ArrayList<String> saves = (ArrayList<String>) saveConfig.getStringList("saves");
		for (String s : saves) {
			addToLoad(fromString(s));
		}
	}
	
	public static void saveLoad() {
		ArrayList<String> saves = new ArrayList<String>();
		for (BoughtPermission b : load) {
			saves.add(b.toString());
		}
		saveConfig.set("saves", saves);
	}
	
	public static BoughtPermission fromString(String s) {
		String[] split = s.split(" ");
		OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(split[0]));
		long exp = Long.parseLong(split[1]);
		String node = split[2];
		return new BoughtPermission(p,exp,node);
	}
	
	public static void addToLoad(BoughtPermission bp) {
		load.add(bp);
	}
	
	public static void removeFromLoad(BoughtPermission bp) {
		load.remove(bp);
	}
	
	public static List<BoughtPermission> getLoad(Player p){
		ArrayList<BoughtPermission> act = new ArrayList<BoughtPermission>();
		for (BoughtPermission bp : load) {
			if (bp.getOfflinePlayer().getUniqueId().toString().equals(p.getUniqueId().toString()))
				act.add(bp);
		}
		return act;
	}
	
	public String toString() {
		return player.getUniqueId().toString() + " " + expires + " " + node;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return player;
	}
	
	public void updateOfflinePlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public long getTimeExpired() {
		return expires;
	}
	
	public String getNode() {
		return node;
	}
	
	public long getTimeLeft() {
		return expires - System.currentTimeMillis();
	}
	
	public boolean playerOnline() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (player.getUniqueId().toString().equals(all.getUniqueId().toString()))
				return true;
		}
		return false;
	}
	
	public static void activateAll() {
		for (BoughtPermission bp : load) {
			if (bp.getOfflinePlayer().isOnline())
				bp.activate();
		}
	}
	
	public static void deactivateAll() {
		for (BoughtPermission bp : load) {
			if (bp.getOfflinePlayer().isOnline())
				bp.deactivate();
		}
	}
	
	public void activate() {
		String prefix = ConfigHandler.getPrefix();
		for (World world: PermissionShop.getPlugin().getServer().getWorlds()) {
			if (!player.getPlayer().hasPermission(node.replace('@', '.')))
				PermissionShop.getPlugin().getPermissions().playerAdd(world.getName(), player, node.replace('@', '.'));
		}
		player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + node.replace('@', '.') + " actived. Expires in " + millisToTime(getTimeLeft())));
	}
	
	public void deactivate() {
		prefix = ConfigHandler.getPrefix();
		for (String cmd : ConfigHandler.getExitCommand(node)) {
			cmd = cmd.replace("%player%", player.getName());
			PermissionShop.getPlugin().getServer().dispatchCommand(PermissionShop.getPlugin().getServer().getConsoleSender(), cmd);
		}
		for (World world: PermissionShop.getPlugin().getServer().getWorlds()) {
			PermissionShop.getPlugin().getPermissions().playerRemove(world.getName(), player, node.replace('@', '.'));
		}
		player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + node.replace('@', '.') + " deactivated."));
	}
	
	public static void runChecker() {
		prefix = ConfigHandler.getPrefix();
		checker = Bukkit.getScheduler().scheduleSyncRepeatingTask(PermissionShop.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (BoughtPermission bp : load) {
					if (!bp.getOfflinePlayer().isOnline())
						return;
					if (bp.getTimeLeft() <= 0) {
						bp.deactivate();
						BoughtPermission.removeFromLoad(bp);
						return;
					}
					if (bp.getTimeLeft() <= 1000) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 1 second."));
						return;
					}
					if (bp.getTimeLeft() <= 2000) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 2 seconds."));
						return;
					}
					if (bp.getTimeLeft() <= 3000) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 3 seconds."));
						return;
					}
					if ((1000*9) <= bp.getTimeLeft() && bp.getTimeLeft() <= (1000*10)) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 10 seconds."));
						return;
					}
					if ((1000*29) <= bp.getTimeLeft() && bp.getTimeLeft() <= (1000*30)) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 30 seconds."));
						return;
					}
					if ((1000*59) <= bp.getTimeLeft() && bp.getTimeLeft() <= (1000*60)) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 1 minute."));
						return;
					}
					if ((60000*5-1000) <= bp.getTimeLeft() && bp.getTimeLeft() <= (60000*5)) {
						bp.getOfflinePlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Permission " + bp.getNode().replace('@', '.') + " expires in 5 minutes."));
						return;
					}
				}
			}
		}, 20L, 20L);
	}
	
	public static void cancelChecker() {
		Bukkit.getScheduler().cancelTask(checker);
	}
	
	public static String millisToTime(long millis) {
		long days,hours,minutes,seconds;
		seconds = millis/1000L;
		
		minutes = seconds/60L;
		if (minutes > 0)
			seconds %= 60L;
		
		hours = minutes/60L;
		if (hours > 0)
			minutes %= 60L;
		
		days = hours/24L;
		if (days > 0)
			hours %= 24L;
		
		String time = "";
		if (days > 0)
			time += days + "d ";
		if (hours > 0)
			time += hours + "h ";
		if (minutes > 0)
			time += minutes + "m ";
		time += seconds + "s";
		
		return time;
	}
	
}
