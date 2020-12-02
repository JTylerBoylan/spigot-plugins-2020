package proj.jtyler.dragonriders.drml;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DRMLPlaceholder extends PlaceholderExpansion {
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}
	
	@Override
	public String getPlugin() {
		return "SuperiorSkyblock2";
	}
	
	@Override
	public String getAuthor() {
		return "JTyler";
	}

	@Override
	public String getIdentifier() {
		return "drml";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public String onRequest(OfflinePlayer p, String identifier) {
		
		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(p.getUniqueId());
		
		if (sp == null)
			return "";
		
		Island island = sp.getIsland();
		
		if (island == null)
			return "0";
		
		if (identifier.equalsIgnoreCase("max"))
			return "" + DRMinionLimiter.getMaximumMinions(island.getUniqueId());
		
		if (identifier.equalsIgnoreCase("current")) 
			return "" + DRMinionLimiter.getCurrentMinions(island.getUniqueId());
		
		return "";
	}

}
