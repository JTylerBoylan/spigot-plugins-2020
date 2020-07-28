package proj.jtyler.dragonriders.drslotmachine;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import net.milkbowl.vault.economy.Economy;

public class SlotMachineUtil {
	
	private static ArrayList<Player> cooling_down = new ArrayList<Player>();
	
	public static Listener listener() {
		return new Listener() {
			
			@EventHandler
			public void onClickEvent(PlayerInteractEntityEvent e) {
				
				final Entity clicked = e.getRightClicked();
				
				if (clicked != null 
						&& clicked instanceof ItemFrame) {
					
					
					final Player player = e.getPlayer();
					final ItemFrame frame = (ItemFrame) clicked;
					final ItemStack item = frame.getItem();
					
					if (item != null 
							&& item.getType() == Material.FILLED_MAP 
							&& item.hasItemMeta()) {
						
						final MapMeta meta = (MapMeta) item.getItemMeta();

						SlotMachine machine = SlotMachine.get(meta.getMapView().getId());
						
						if (machine != null) {
							e.setCancelled(true);
							
							float cost = machine.getCost();
							
							final Economy econ = DR_SlotMachine.getEconomy();
							
							if (isCoolingDown(player)) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', DR_SlotMachine.get().getPrefix() + "You cannot play this right now."));
								return;
							}
							
							if (econ.getBalance(player) < cost) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', DR_SlotMachine.get().getPrefix() + "You don't have enough money to play at this slot machine."));
								return;
							} else {
								econ.withdrawPlayer(player, cost);
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', DR_SlotMachine.get().getPrefix() + "Playing slot machine " + machine.getID() + ". Cost: $" + cost));
								cooldown(player, DR_SlotMachine.get().getCooldown());
								machine.run(player);
							}
							return;
						}
						
					}
					
				}
				
			}
			
		};
	}
	
	public static CommandExecutor commandExecutor() {
		return new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arguments) {
				
				String[] args = new String[100];
				for (int i = 0; i < arguments.length; i++) {
					args[i] = arguments[i];
				}
				
				
				
				return true;
			}
			
			
		};
	}
	
	private static void cooldown(Player p, long time) {
		addToCooldown(p);
		DR_SlotMachine.get().getServer().getScheduler().runTaskLater(DR_SlotMachine.get(), new Runnable() {
			@Override
			public void run() {
				removeFromCooldown(p);
			}
		}, time);
	}
	
	private static void addToCooldown(Player p) {
		cooling_down.add(p);
	}
	
	private static void removeFromCooldown(Player p) {
		cooling_down.remove(p);
	}
	
	private static boolean isCoolingDown(Player p) {
		return cooling_down.contains(p);
	}
	
}
