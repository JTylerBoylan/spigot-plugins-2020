package proj.jtyler.dragonriders.drml;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import me.jet315.minions.events.MinionBreakEvent;
import me.jet315.minions.events.PreMinionPlaceEvent;

public class EventListener implements Listener {

	@EventHandler
	public void prePlace(PreMinionPlaceEvent e) {
		
		Player p = e.getPlayer();
		
		Island island = SuperiorSkyblockAPI.getIslandAt(e.getMinionLocation());
		
		if (island == null)
			return;
		
		int max = DRMinionLimiter.getMaximumMinions(island.getUniqueId());
		int current = DRMinionLimiter.getCurrentMinions(island.getUniqueId());
		
		if (++current > max) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(DRMinionLimiter.getPrefix() + "This island has the maximum amount of minions.");
			return;
		}
		
		if (!p.hasPermission("drml.minions." + e.getMinionType().toLowerCase())) {
			p.sendMessage(DRMinionLimiter.getPrefix() + "You do not have access to this minion.");
			e.setCancelled(true);
			return;
		}
		
		SuperiorPlayer islandOwner = island.getOwner();
		
		if (islandOwner == null)
			return;
		
		if (!DRMinionLimiter.getPermissions().playerHas(p.getWorld().getName(), Bukkit.getOfflinePlayer(islandOwner.getUniqueId()), "drml.minions." + e.getMinionType().toLowerCase())) {
			p.sendMessage(DRMinionLimiter.getPrefix() + "You cannot place this minion here.");
			e.setCancelled(true);
			return;
		}
		
		p.sendMessage(DRMinionLimiter.getPrefix() + "You placed minion " + current + "/" + DRMinionLimiter.getMaximumMinions(island.getUniqueId()) + ".");
		
		DRMinionLimiter.setCurrentMinions(island.getUniqueId(), current);
		
	}
	
	@EventHandler
	public void onBreak(MinionBreakEvent e) {
		
		Island island = SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation());
		
		if (island == null)
			return;
		
		DRMinionLimiter.setCurrentMinions(island.getUniqueId(), DRMinionLimiter.getCurrentMinions(island.getUniqueId()) - 1);
		
	}
	
	
}
