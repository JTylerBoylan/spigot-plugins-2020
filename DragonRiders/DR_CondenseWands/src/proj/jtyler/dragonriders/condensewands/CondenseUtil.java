package proj.jtyler.dragonriders.condensewands;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class CondenseUtil {
	
	private static File condense_file = new File(CondenseWands.getInstance().getDataFolder(), "condense.yml");
	private static FileConfiguration condense_config = YamlConfiguration.loadConfiguration(condense_file);
	
	public static int condense(Container container) {
		HashMap<Material, Integer> condensable = new HashMap<Material,Integer>();
		
		int condensed = 0;
		
		for (ItemStack item : container.getInventory().getContents()) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			if (getOutput(item.getType()) != null) {
				if (condensable.containsKey(item.getType()))
					condensable.put(item.getType(), condensable.get(item.getType()) + item.getAmount());
				else
					condensable.put(item.getType(), item.getAmount());
			}
		}
		
		HashMap<Material,Integer> output = new HashMap<Material,Integer>();
		HashMap<Material,Integer> input = new HashMap<Material, Integer>();
		
		for (Material m : condensable.keySet()) {
			Material out = getOutput(m);
			output.put(out, condensable.get(m)/getCost(m));
			input.put(m, output.get(out)*getCost(m));
		}
		
		for (Material m : input.keySet()) {
			int left = input.get(m);
			condensed += left;
			int slot = 0;
			while (left > 0 && slot < container.getInventory().getSize()) {
				ItemStack item = container.getInventory().getItem(slot);
				if (item == null || item.getType() == Material.AIR) {
					slot++;
					continue;
				}
				if (item.getType() == m) {
					if (item.getAmount() <= left) {
						left -= item.getAmount();
						container.getInventory().setItem(slot, null);
					} else {
						item.setAmount(item.getAmount() - left);
						left = 0;
					}
				}
				slot++;
			}
		}
		
		for (Material m : output.keySet()) {
			int left = output.get(m);
			while (left > 0) {
				ItemStack item = new ItemStack(m);
				if (left > item.getMaxStackSize()) {
					item.setAmount(item.getMaxStackSize());
				} else {
					item.setAmount(left);
				}
				left -= item.getAmount();
				for (ItemStack it : container.getInventory().addItem(item).values()) {
					container.getLocation().getWorld().dropItem(container.getLocation(), it);
				}
			}
		}
		
		return condensed;
		
	}
	
	public static Material getOutput(Material input) {
		String material = condense_config.getString(input.toString() + ".output");
		if (material == null)
			return null;
		return Material.getMaterial(material);
	}
	
	public static int getCost(Material input) {
		return Integer.parseInt(condense_config.getString(input.toString() + ".cost"));
	}
	
}
