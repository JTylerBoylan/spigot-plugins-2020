package proj.jtyler.dragonriders.permissionshop;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopGUI implements CommandExecutor, Listener {

	private static Inventory inv = null;
	
	private static HashMap<Player, Inventory> open = new HashMap<Player, Inventory>();
	
	public static HashMap<Player,Inventory> getWhoseOpen(){
		if (open != null)
			return open;
		open = new HashMap<Player,Inventory>();
		return open;
	}
	
	public static void closeAll() {
		for (Player p : open.keySet())
			p.closeInventory();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You can't run this command from here.");
			return true;
		}
		
		String prefix = ConfigHandler.getPrefix();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "    PermissionShop Commands    "));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "/pshop  &7: Opens PermissionShop GUI."));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "/pshop help  &7: Opens this page."));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "/pshop active  &7: View your active permissions."));
				if (sender.hasPermission("permissionshop.reload"))
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "/pshop reload  &7: Reloads the plugin. &6[permissionshop.reload]"));
				if (sender.hasPermission("permissionshop.activeother"))
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "/pshop active <player>  &7: Views player's active permissions. &6[permissionshop.activeothers]"));
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("permissionshop.reload")) {
					PermissionShop.getPlugin().reload();
					sender.sendMessage(ChatColor.GREEN + "Plugin reloaded.");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("active")) {
				
				Player target = (Player) sender;
				String name = "Your";
				
				if (args.length > 1 && sender.hasPermission("permissionshop.activeothers"))
					target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "Unknown player. Usage: /pshop active <Player>"));
					return true;
				}
				if (target != (Player) sender)
					name = target.getName() + "'s";
				
				List<BoughtPermission> act = BoughtPermission.getLoad(target);
				if (act.size() == 0) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "No active permissions."));
					return true;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + name + " active permissions: "));
				for (BoughtPermission bp : act) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "- [" + bp.getNode().replace('@', '.') + "] , Time Left: " + BoughtPermission.millisToTime(bp.getTimeLeft())));
				}
				return true;
			}
		}
		
		Inventory inventory = getInventory();
		((Player) sender).openInventory(inventory);
		
		open.put((Player) sender, inventory);
		return true;
	}
	
	public static Inventory getInventory() {
		if (inv != null)
			return inv;
		inv = Bukkit.createInventory(null, ConfigHandler.getGUISize(), ChatColor.translateAlternateColorCodes('&', ConfigHandler.getGUIName()));
		
		ItemStack filler = ConfigHandler.getFillerTile();
		for (int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, filler);
		
		for (String node : ConfigHandler.getNodes()) {
			ItemStack item = ConfigHandler.getTile(node);
			int slot = ConfigHandler.getSlot(node);
			inv.setItem(slot, item);
		}
		
		return inv;
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (open.containsKey(player)) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().isSimilar(ConfigHandler.getFillerTile()))
				return;
			
			String node = ConfigHandler.getNode(e.getSlot());
			if (node == null)
				return;
			
			String prefix = ConfigHandler.getPrefix();
			
			for (BoughtPermission active : BoughtPermission.getLoad(player)) {
				if (active.getNode() == node) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "You already have this permission."));
					player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
					player.closeInventory();
					return;
				}
			}
			
			int price = ConfigHandler.getPrice(node);
			if (PermissionShop.getPlugin().getEconomy().getBalance(player) < price) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "You don't have enough money to buy this permission."));
				player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
				player.closeInventory();
				return;
			}
			
			long time = ConfigHandler.getTimeInMinutes(node);
			long time_milli = time*60000L;
			
			PermissionShop.getPlugin().getEconomy().withdrawPlayer(player, price);
			BoughtPermission perm = new BoughtPermission(player, System.currentTimeMillis() + time_milli, node);
			BoughtPermission.addToLoad(perm);
			perm.activate();
			player.closeInventory();
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (open.containsKey((Player) e.getPlayer()))
			open.remove((Player) e.getPlayer());
	}

	
	
}
