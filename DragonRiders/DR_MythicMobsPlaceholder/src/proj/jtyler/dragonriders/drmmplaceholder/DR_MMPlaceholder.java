package proj.jtyler.dragonriders.drmmplaceholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DR_MMPlaceholder extends PlaceholderExpansion {

	private MythicMobs depends = null;
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}
	
	@Override
	public String getPlugin() {
		return "MythicMobs";
	}
	
	@Override
	public boolean register() {
		
		if (!canRegister())
			return false;
		
		depends = (MythicMobs) Bukkit.getPluginManager().getPlugin(getPlugin());
		
		if (depends == null)
			return false;
		
		return PlaceholderAPI.registerPlaceholderHook(getIdentifier(),this);
		
	}
	
	@Override
	public String getAuthor() {
		return "JTyler_";
	}

	@Override
	public String getIdentifier() {
		return "mmcooldown";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		
		try {
			
			MythicSpawner spawner = MythicMobs.inst().getSpawnerManager().getSpawnerByName(identifier);
			
			if (spawner == null)
				return "null";
			
			int seconds = spawner.getRemainingCooldownSeconds();
			
			int minutes = seconds/60;
			
			seconds = seconds%60;
			
			if (minutes == 0 && seconds == 0)
				return "Alive";
			
			String minutes_s = minutes > 0 ? minutes + "m " : "";
			
			return minutes_s + seconds + "s";
			
			
		} catch (Exception e) {
			return "-";
		}
	}
	
	
}
