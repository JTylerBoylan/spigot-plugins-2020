package proj.jtyler.dragonriders.drmines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Mines extends JavaPlugin implements Listener {
	
	public World world = null;
	public List<String> blocks = null;
	public Material filler = null;
	public long resetTime = 0L;
	
	public HashMap<String, Long> break_map = null;
	
	int scheduler;
	
	public void onEnable() {
		
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.saveConfig();
		
		this.getServer().getPluginManager().registerEvents(this, this);
		break_map = new HashMap<String,Long>();
		loadReseter();
		
		world = Bukkit.getWorld(this.getConfig().getString("world"));
		if (world == null)
			this.getServer().getLogger().log(Level.WARNING, "Could not find world: " + this.getConfig().getString("world"));
		blocks = this.getConfig().getStringList("mine-blocks");
		filler = Material.getMaterial(this.getConfig().getString("filler-block"));
		resetTime = this.getConfig().getInt("reset-time-s") * 1000;
	}
	
	public void onDisable() {
		resetAll();
		Bukkit.getScheduler().cancelTask(scheduler);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMine(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (e.getBlock().getLocation().getWorld() == world) {

			if (p.getGameMode() != GameMode.SURVIVAL)
				return;
			e.setCancelled(true);
			
			if (blocks.contains(e.getBlock().getType().toString())) {
				
				Block b = e.getBlock();
				break_map.put(b.getX() + " " + b.getY() + " " + b.getZ() + " " + b.getType().toString(), System.currentTimeMillis() + resetTime);
				
				for (ItemStack drop : e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())) {
					boolean d = false;
					for (ItemStack i : p.getInventory().addItem(drop).values()) {
						p.getLocation().getWorld().dropItem(p.getLocation(), i);
						d = true;
					}
					if (d)
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonMines] &fInventory full. Items dropped on ground."));
				}
				
				e.getBlock().setType(filler);
				
			}
		}
	}
	
	public void fixWorld() {
		if (world == null)
			world = Bukkit.getWorld(this.getConfig().getString("world"));
	}
	
	public void reset(String save) {
		String[] info = save.split(" ");
		Location loc = new Location(world, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
		Material mat = Material.getMaterial(info[3]);
		loc.getBlock().setType(mat);
	}
	
	public void loadReseter() {
		scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				fixWorld();
				List<String> remove = new ArrayList<String>();
				for (String keys : break_map.keySet()) {
					if (break_map.get(keys) <= System.currentTimeMillis()) {
						remove.add(keys);
						reset(keys);
					}
				}
				for (String r : remove) {
					break_map.remove(r);
				}
			}
		}, 0L, 20L);
	}
	
	public void resetAll() {
		for (String saves : break_map.keySet())
			reset(saves);
	}
	
}
