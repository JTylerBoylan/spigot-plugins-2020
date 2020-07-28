package proj.jtyler.dragonriders.luckyblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Reward {

	private static Random random = new Random();
	
	private static ArrayList<Reward> rewards = new ArrayList<Reward>();
	
	private String id = null;
	
	private float chance = 0.0f;
	
	private List<String> commands = null;
	
	private String message = null;
	
	private String announcement = null;
	
	public Reward(String id) {
		this.id = id;
	}
	
	public float getChance() {
		if (chance == 0.0f)
			chance = (float) LuckyBlockDR.getRewardsConfig().getDouble(id + ".chance");
		return chance;
	}
	
	public List<String> getCommands() {
		if (commands == null)
			commands = LuckyBlockDR.getRewardsConfig().getStringList(id + ".commands");
		return commands;
	}
	
	public String getMessage() {
		if (message == null)
			message = LuckyBlockDR.getRewardsConfig().getString(id + ".message");
		return message;
	}
	
	public String getAnnouncement() {
		if (announcement == null)
			announcement = LuckyBlockDR.getRewardsConfig().getString(id + ".announcement");
		return announcement;
	}
	
	public void give(Player player) {
		if (player == null)
			return;
		List<String> cmds = getCommands();
		if (cmds != null) {
			for (int i = 0; i < cmds.size(); i++) {
				String cmd = cmds.get(i).replace("%player%", player.getName());
				LuckyBlockDR.getPlugin().getServer().dispatchCommand(LuckyBlockDR.getPlugin().getServer().getConsoleSender(), cmd);
			}
		}
		if (getMessage() != null)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage().replace("%prefix%", LuckyBlockDR.getPrefix()).replace("%player%", player.getName())));
		if (getAnnouncement() != null)
			for (Player all : Bukkit.getOnlinePlayers())
				all.sendMessage(ChatColor.translateAlternateColorCodes('&', getAnnouncement().replace("%prefix%", LuckyBlockDR.getPrefix()).replace("%player%", player.getName())));
	}
	
	public static void addReward(String id) {
		rewards.add(new Reward(id));
	}
	
	public static void addRewards(Set<String> ids) {
		for (String id : ids) {
			rewards.add(new Reward(id));
		}
	}
	
	public static Reward randomReward() {
		if (rewards.size() == 0)
			return null;
		Integer[] pool = new Integer [10000];
		int index = 0;
		for (int i = 0; i < rewards.size(); i++) {
			Reward r = rewards.get(i);
			for (int j = 0; j < r.getChance()*10000-1; j++) {
				pool[index] = i;
				index++;
			}
		}
		
		int in = random.nextInt(10000);
		if (pool[in] == null)
			return null;
		return rewards.get(pool[in]);
	}
	
	public static void reload() {
		random = new Random();
		rewards = new ArrayList<Reward>();
	}
	
}
