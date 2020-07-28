package proj.jtyler.dragonriders.drsmelt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public class Smelt extends JavaPlugin {
	
	static List<Material> blacklist;
	
	static HashMap<Material, Material> smelt_map  = new HashMap<Material,Material>();
	
	public void onEnable() {
		this.saveResource("config.yml", false);
		blacklist = new ArrayList<Material>();
		for (String s : this.getConfig().getStringList("blacklist")) {
			blacklist.add(Material.getMaterial(s));
		}
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		
		if (label.equalsIgnoreCase("smelt")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You can't run this command from here.");
				return true;
			}
			Player p = (Player) sender;
			if (!p.hasPermission("drsmelt.smelt")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders] &fYou can't use this command."));
				return true;
			}
			if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
				if (!p.hasPermission("drsmelt.smelt.all")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders] &fYou can't use this command."));
					return true;
				}
				
				for (int i = 0; i < p.getInventory().getSize(); i++) {
					ItemStack item = p.getInventory().getItem(i);
					if (item != null && getSmelt(item.getType()) != null && !blacklist.contains(item.getType())) {
						p.getInventory().setItem(i, new ItemStack(getSmelt(item.getType()), item.getAmount()));
					}
				}
				
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[DragonRiders] &fItems smelted."));
				return true;
			}
			
			ItemStack i = p.getInventory().getItemInMainHand();
			
			if (getSmelt(i.getType()) != null && !blacklist.contains(i.getType())) {
				p.getInventory().setItemInMainHand(new ItemStack(getSmelt(i.getType()), i.getAmount()));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[DragonRiders] &fItems smelted."));
				return true;
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders] &fThis item can't be smelted."));
				return true;
			}
			
		}
		
		return true;
	}
	
	public Material getSmelt(Material type) {
		if (smelt_map.containsKey(type))
			return smelt_map.get(type);
		ItemStack result = null;
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
		   Recipe recipe = iter.next();
		   if (!(recipe instanceof FurnaceRecipe)) continue;
		   if (((FurnaceRecipe) recipe).getInput().getType() != type) continue;
		   result = recipe.getResult();
		   break;
		}
		
		smelt_map.put(type, result == null ? null : result.getType());
		
		return smelt_map.get(type);
	}
	
}
