package proj.jtyler.dragonriders.condensewands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CondenseWands extends JavaPlugin {
	
	private static CondenseWands instance = null;
	
	public void onEnable() {
		
		instance = this;
		
		this.saveResource("config.yml", false);
		this.saveResource("condense.yml", false);
		
		this.getServer().getPluginManager().registerEvents(wandListener(), this);
		
		this.getCommand("condensewand").setExecutor(condenseWandCommand());
		
	}
	
	public static CondenseWands getInstance() {
		return instance;
	}
	
	public Listener wandListener() {
		return new Listener() {
			@EventHandler
			public void interact(PlayerInteractEvent e) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (e.getClickedBlock().getState() instanceof Container) {
						Container container = (Container) e.getClickedBlock().getState();
						ItemStack wand = e.getPlayer().getInventory().getItemInMainHand();
						if (CondenseWand.isCondenseWand(wand)) {
							e.setCancelled(true);
							int condensed = CondenseUtil.condense(container);
							if (condensed > 0)
								e.getPlayer().getInventory().setItemInMainHand(CondenseWand.use(wand, condensed));
							e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f Condensed " + condensed + " blocks."));
							if (CondenseWand.getUses(e.getPlayer().getInventory().getItemInMainHand()) <= 0) {
								e.getPlayer().getInventory().setItemInMainHand(null);
							}
						}
					}
				}
			}
		};
	}
	
	public CommandExecutor condenseWandCommand() {
		return new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label,
					String[] arguments) {
				
				String[] args = new String[4];
				for (int i = 0; i < 4; i++)
					try {
						args[i] = arguments[i];
					} catch (ArrayIndexOutOfBoundsException e) {
						args[i] = null;
					}
				
				if (args[0] != null && args[0].equalsIgnoreCase("give")) {
					if (sender.hasPermission("condensewands.give")) {
						if (args[1] == null || args[2] == null || args[3] == null) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f /cw give [player] [amount] [uses]"));
							return true;
						}
						
						Player player = Bukkit.getPlayer(args[1]);
						
						if (player == null) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f Unknown player."));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f /cw give [player] [amount] [uses]"));
							return true;
						}
						
						int amount;
						int uses;
						try {
							amount = Integer.parseInt(args[2]);
							uses = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f Invalid number."));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f /cw give [player] [amount] [uses]"));
							return true;
						}
						
						for (int i = 0; i < amount; i++) {
							ItemStack wand = CondenseWand.getCondenseWand(uses);
							for (ItemStack item : player.getInventory().addItem(wand).values())
								player.getLocation().getWorld().dropItem(player.getLocation(), item);
						}
						
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f Condense wand added to inventory."));
						return true;
						
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f You don't have permission for this command."));
						return true;
					}
				} else {
					
					if (!sender.hasPermission("condensewands.give")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&6&l CondenseWands &7by JTyler"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f Version 1.0"));
						return true;
					}
					
					if (sender.hasPermission("condensewands.give")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[DragonRiders]&f /cw give [player] [amount] [uses]"));
						return true;
					}
					
				}
				
				return false;
			}
			
		};
	}
	
}
