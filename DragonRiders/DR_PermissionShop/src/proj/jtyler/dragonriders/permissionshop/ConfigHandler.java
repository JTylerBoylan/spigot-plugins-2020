package proj.jtyler.dragonriders.permissionshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigHandler {

	public static FileConfiguration config = null;
	
	public static void setConfig(FileConfiguration c) {
		config = c;
	}
	
	public static String getPrefix() {
		return config.getString("prefix");
	}
	
	public static String getGUIName() {
		return config.getString("gui-name");
	}
	
	public static int getGUISize() {
		return config.getInt("gui-size");
	}
	
	public static Material getFillerMaterial() {
		return Material.getMaterial(config.getString("filler.material"));
	}
	
	public static String getFillerName() {
		return config.getString("filler.name");
	}
	
	public static Set<String> getNodes() {
		return config.getConfigurationSection("permissions").getKeys(false);
	}
	
	public static int getPrice(String node) {
		return config.getInt("permissions." + node + ".price");
	}
	
	public static long getTimeInMinutes(String node) {
		return config.getLong("permissions." + node + ".time-m");
	}
	
	public static Material getIcon(String node) {
		return Material.getMaterial(config.getString("permissions." + node + ".icon"));
	}
	
	public static int getSlot(String node) {
		return config.getInt("permissions." + node + ".slot");
	}
	
	public static String getName(String node) {
		return config.getString("permissions." + node + ".name");
	}
	
	public static List<String> getLore(String node){
		return config.getStringList("permissions." + node + ".lore");
	}
	
	public static List<String> getExitCommand(String node) {
		return config.getStringList("permissions." + node + ".exit-commands");
	}
	
	public static String getNode(int slot) {
		for (String node : getNodes())
			if (getSlot(node) == slot)
				return node;
		return null;
	}
	
	public static ItemStack getFillerTile() {
		ItemStack filler = new ItemStack(ConfigHandler.getFillerMaterial());
		ItemMeta meta = filler.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigHandler.getFillerName()));
		filler.setItemMeta(meta);
		return filler;
	}
	
	public static ItemStack getTile(String node) {
		ArrayList <String> lore = new ArrayList<String>();
		for (String s : getLore(node))
			lore.add(ChatColor.translateAlternateColorCodes('&', s));
		
		ItemStack item = new ItemStack(getIcon(node));
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', getName(node)));
		im.setLore(lore);
		item.setItemMeta(im);
		
		return item;
	}
	
}
