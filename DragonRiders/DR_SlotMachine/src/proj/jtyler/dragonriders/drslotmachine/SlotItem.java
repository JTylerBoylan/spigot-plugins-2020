package proj.jtyler.dragonriders.drslotmachine;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SlotItem {

	private static File slotmachine_yml = new File(DR_SlotMachine.get().getDataFolder(), "slotmachine.yml");
	
	private ConfigurationSection item_config = null;
	private SlotMachine machine = null;
	private Material material = null;
	private String name = null;
	private ArrayList<String> lore = null;
	private Float multiplier = null;
	private Boolean wildcard = null;
	private Integer chance = null;
	private ItemStack item = null;
	public SlotItem(SlotMachine machine, Material material) {
		this.item_config = YamlConfiguration.loadConfiguration(slotmachine_yml).getConfigurationSection(machine.getID() + ".items." + material.name());
		this.machine = machine;
		this.material = material;
	}
	
	public SlotMachine getSlotMachine() {
		return machine;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public String getName() {
		if (name == null)
			name = item_config.getString("name");
		return name;
	}
	
	public ArrayList<String> getLore(){
		if (lore == null) {
			lore = new ArrayList<String>();
			for (String s : item_config.getStringList("lore"))
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return lore;
	}
	
	public Float getMultiplier() {
		if (multiplier == null)
			multiplier = (float) item_config.getDouble("multiplier");
		return multiplier;
	}
	
	public Boolean isWildCard() {
		if (wildcard == null)
			wildcard = item_config.getBoolean("wild-card");
		return wildcard;
	}
	
	public Integer getChance() {
		if (chance == null)
			chance = item_config.getInt("chance");
		return chance;
	}
	
	public ItemStack toItemStack() {
		if (item == null) {
			item = new ItemStack(getMaterial());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getName()));
			ArrayList<String> lore = new ArrayList<String>();
			for (String s : getLore())
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item.clone();
	}
	
	public boolean matches(SlotItem item) {
		return this.getMaterial() == item.getMaterial() || this.isWildCard() || item.isWildCard();
	}
	
	public boolean equals(SlotItem item) {
		return this.getMaterial() == item.getMaterial();
	}
	
}
