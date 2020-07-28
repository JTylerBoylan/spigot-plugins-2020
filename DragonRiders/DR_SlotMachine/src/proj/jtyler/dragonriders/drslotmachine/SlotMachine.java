package proj.jtyler.dragonriders.drslotmachine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SlotMachine {

	private static File slotmachine_yml = new File(DR_SlotMachine.get().getDataFolder(), "slotmachine.yml");
	private static FileConfiguration slot_config = YamlConfiguration.loadConfiguration(slotmachine_yml);
	
	private static HashMap<String, SlotMachine> slot_map = new HashMap<String, SlotMachine>();
	
	public static SlotMachine get(String slot_id) {
		if (!slot_map.containsKey(slot_id))
			if (slot_config.getConfigurationSection(slot_id) != null)
				slot_map.put(slot_id, new SlotMachine(slot_id));
		return slot_map.get(slot_id);
	}
	
	public static SlotMachine get(Integer map_id) {
		String key = MapIDtoSlotID(map_id);
		if (key != null)
			return get(key);
		return null;
	}
	
	private static String MapIDtoSlotID(Integer map_id) {
		for (String key : slot_config.getKeys(false)) {
			SlotMachine machine = new SlotMachine(key);
			if (machine.getMapID().intValue() == map_id.intValue()) {
				return key;
			}
		}
		return null;
	}
	
	private Integer map_id = null;
	private String slot_id = null;
	private Integer cost = null;
	private Integer reward = null;
	private List<SlotItem> items = null;
	private int[] spread = null;
	private SlotMachine(String slot_id) {
		this.slot_id = slot_id;
	}
	
	public String getID() {
		return slot_id;
	}
	
	public Integer getMapID() {
		if (map_id == null)
			map_id = slot_config.getInt(slot_id + ".map");
		return map_id;
	}
	
	public Integer getCost() {
		if (cost == null)
			cost = slot_config.getInt(slot_id + ".cost");
		return cost;
	}
	
	public Integer getReward() {
		if (reward == null)
			reward = slot_config.getInt(slot_id + ".reward");
		return reward;
	}
	
	public List<SlotItem> getItems(){
		if (items == null) {
			ArrayList<SlotItem> array = new ArrayList<SlotItem>();
			for (String key : slot_config.getConfigurationSection(slot_id + ".items").getKeys(false)) {
				array.add(new SlotItem(this, Material.getMaterial(key)));
			}
			items = array;
		}
		return items;
	}
	
	private int totalChance() {
		int total_chance = 0;
		for (SlotItem item : getItems()) {
			total_chance += item.getChance();
		}
		return total_chance;
	}
	
	public int[] getChanceSpread() {
		if (spread == null) {
			spread = new int[totalChance()];
			int index = 0;
			for (int i = 0; i < getItems().size(); i++) {
				SlotItem item = getItems().get(i);
				for (int j = 0; j < item.getChance(); j++) {
					spread[index+j] = i;
				}
				index += item.getChance();
			}
		}
		return spread;
	}
	
	public void run(Player player) {
		final SlotMachineWheel wheel = new SlotMachineWheel(getItems(),getChanceSpread());
		final SlotMachineGUI gui = new SlotMachineGUI(this, wheel, player);

		gui.open();
		gui.spin();
		
	}
	
}
