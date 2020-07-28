package proj.jtyler.dragonriders.drcommandblocker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandBlocker extends JavaPlugin implements Listener {
	
	private List<World> hasBlockedCommand = null;
	
	public void onEnable() {
		this.saveResource("config.yml", false);
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (hasBlockedCommand == null)
			hasBlockedCommand = getBlockedCommandWorlds();
		if (hasBlockedCommand.contains(e.getPlayer().getLocation().getWorld())) {
			for (String blckd : getBlockedCommands(e.getPlayer().getLocation().getWorld())) {
				if (e.getMessage().contains(blckd)) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders] &fYou can't run this command here."));
				}
			}
		}
	}
	
	public List<World> getBlockedCommandWorlds(){
		ArrayList<World> worlds = new ArrayList<World>();
		for (String key : this.getConfig().getConfigurationSection("blocked-commands").getKeys(false)) {
			worlds.add(Bukkit.getWorld(key));
		}
		return worlds;
	}
	
	public List<String> getBlockedCommands(World w){
		return this.getConfig().getStringList("blocked-commands." + w.getName());
	}
	
}
