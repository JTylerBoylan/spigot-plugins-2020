package proj.jtyler.dragonriders.drssisleader;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DR_SSIsLeaderPlaceholder extends PlaceholderExpansion {

	private SuperiorSkyblock depends = null;
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}
	
	@Override
	public String getPlugin() {
		return "SuperiorSkyblock2";
	}
	
	@Override
	public boolean register() {
		
		if (!canRegister())
			return false;
		
		depends = (SuperiorSkyblock) Bukkit.getPluginManager().getPlugin(getPlugin());
		
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
		return "ssisleader";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public String onRequest(OfflinePlayer p, String identifier) {
		
		SuperiorPlayer player = SuperiorSkyblockAPI.getPlayer(p.getUniqueId());
		
		if (identifier.equalsIgnoreCase("level")) {
			
			if (player == null)
				return "";
			
			if (player.equals(player.getIslandLeader())) {
				return player.getIsland().getIslandLevel().toString();
			} else {
				return "";
			}
			
		}
		
		if (identifier.equalsIgnoreCase("worth")) {
			
			if (player == null)
				return "";
			
			if (player.getIsland() == null || player.getIsland().getWorth() == null)
				return "";
			
			if (player.equals(player.getIslandLeader())) {
				return player.getIsland().getWorth().toString();
			} else {
				return "";
			}
			
		}
		
		return "";
		
	}

	
	
}
