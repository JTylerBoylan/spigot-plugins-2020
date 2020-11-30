package proj.jtyler.dragonriders.drwsp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.wildstacker.api.events.SpawnerPlaceEvent;

import net.milkbowl.vault.permission.Permission;


public class WildStackerPerm extends JavaPlugin {

	private static String prefix;
	
    private Permission perms;
	
	private static HashMap<EntityType, String> permissionLevels;
	
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

	
	public void onEnable() {
		
		this.setupPermissions();
		
		this.saveResource("config.yml", false);
		
		prefix = this.getConfig().getString("prefix");
		
		this.getServer().getPluginManager().registerEvents(spawnerListener(), this);
		
		permissionLevels = new HashMap<EntityType, String>();
		
		for (String entityName : this.getConfig().getConfigurationSection("permission-levels").getKeys(false))
			if (EntityType.valueOf(entityName) != null)
				permissionLevels.put(EntityType.valueOf(entityName), this.getConfig().getString("permission-levels." + entityName));
			
		this.getLogger().info("Successfully loaded " + permissionLevels.size() + " entities and their levels.");
		
	}
	
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}
	
	
	public Listener spawnerListener() {
		
		return new Listener() {
			
			@EventHandler
			public void onPlace(SpawnerPlaceEvent e) {
				
				Player p = e.getPlayer();
				
				EntityType entityType = e.getSpawner().getSpawnedType();
				
				if (!permissionLevels.containsKey(entityType))
					return;
				
				String level = permissionLevels.get(entityType);
				
				if (!p.hasPermission("wildstackerperm." + level)) {
					p.sendMessage(getPrefix() + "You do not have access to this spawner.");
					e.setCancelled(true);
					return;
				}
				
				Island island = SuperiorSkyblockAPI.getIslandAt(p.getLocation());
				
				if (island == null)
					return;
				
				SuperiorPlayer islandOwner = island.getOwner();
				
				if (islandOwner == null)
					return;
				
				if (!perms.playerHas(p.getWorld().getName(), Bukkit.getOfflinePlayer(islandOwner.getUniqueId()), "wildstackerperm." + level)) {
					p.sendMessage(getPrefix() + "You cannot place this spawner here.");
					e.setCancelled(true);
					return;
				}
				
				
			}
			
		};
		
	}
	
}
