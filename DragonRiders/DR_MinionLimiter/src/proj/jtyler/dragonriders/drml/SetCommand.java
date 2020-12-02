package proj.jtyler.dragonriders.drml;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class SetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
		 String[] args) {
		
		if (!sender.hasPermission("drml.set")) {
			sender.sendMessage(DRMinionLimiter.getPrefix() + "You don't have permission for this command.");
			return true;
		}
		
		if (args.length != 2) {
			sender.sendMessage(DRMinionLimiter.getPrefix() + "Usage: /drml-set <player> <max-minions>");
			return true;
		}
		
		Player player = Bukkit.getPlayer(args[0]);
		
		if (player == null) {
			sender.sendMessage(DRMinionLimiter.getPrefix() + "Unknown Player. Usage: /drml-set <player> <max-minions>");
			return true;
		}
		
		int max = 0;
		
		try {
			max = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(DRMinionLimiter.getPrefix() + "Invalid integer. Usage: /drml-set <player> <max-minions>");
			return true;
		}
		
		SuperiorPlayer splayer = SuperiorSkyblockAPI.getPlayer(player);
		
		Island island = splayer.getIsland();
		
		if (island == null) {
			sender.sendMessage(DRMinionLimiter.getPrefix() + "This player does not have an island.");
			return true;
		}
		
		DRMinionLimiter.setMaximumMinions(island.getUniqueId(), max);
		
		return true;
	}

}
