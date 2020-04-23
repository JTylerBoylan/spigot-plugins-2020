package com.donsquid.craftexchange;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Order {

	public static enum OrderType { BUY, SELL }
	
	private OrderType TYPE;
	private String TICKER;
	private float PRICE;
	private int AMOUNT;
	private UUID PLAYER;
	
	
	public Order(OrderType TYPE, String TICKER, float PRICE, int AMOUNT, UUID PLAYER) {
		this.TYPE = TYPE;
		this.TICKER = TICKER;
		this.PRICE = PRICE;
		this.AMOUNT = AMOUNT;
		this.PLAYER = PLAYER;
	}
	
	public OrderType getType() {
		return TYPE;
	}
	
	public String getTicker() {
		return TICKER;
	}
	
	public float getPrice() {
		return PRICE;
	}
	
	public int getAmount() {
		return AMOUNT;
	}
	
	public void fill(int size) {
		this.AMOUNT -= size;
		
		// ORDER QUEUE AND RETURN
		if (this.TYPE == OrderType.SELL) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(PLAYER);
			A.getEconomy().depositPlayer(player, size*PRICE);
			if (player.getPlayer() != null) {
				A.sendMessage(player.getPlayer(), "order-filled");
				player.getPlayer().sendMessage("SOLD " + size + " " + TICKER + " AT $" + PRICE + "/" + TICKER);
			}
		}
		
		if (this.TYPE == OrderType.BUY) {
			Player player = Bukkit.getPlayer(PLAYER);
			if (player == null) {
				A.addPendingOrder(new Order(TYPE, TICKER, PRICE, size, PLAYER));
			} else {
				ItemStack item = A.getTicker(TICKER).getItemStack();
				item.setAmount(size);
				player.getInventory().addItem(item);
				A.sendMessage(player.getPlayer(), "order-filled");
				player.getPlayer().sendMessage("BOUGHT " + size + " " + TICKER + " AT $" + PRICE + "/" + TICKER);
			}
		}
		
		A.getTicker(TICKER).addToVolume(size);
		
	}
	
	public void cancel() {
		
		if (this.TYPE == OrderType.SELL) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(PLAYER);
			if (player.getPlayer() == null) {
				A.addPendingOrder(new Order(OrderType.BUY, TICKER, PRICE, AMOUNT, PLAYER));
			} else {
				ItemStack item = A.getTicker(TICKER).getItemStack();
				item.setAmount(AMOUNT);
				player.getPlayer().getInventory().addItem(item);
				return;
			}
		}
		
		if (this.TYPE == OrderType.BUY) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(PLAYER);
			A.getEconomy().depositPlayer(player, PRICE*AMOUNT);
		}
		
	}
	
	public UUID getPlayerUUID() {
		return PLAYER;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(PLAYER);
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(PLAYER);
	}
	
	public String toString() {
		return TYPE.toString() + " " + TICKER + " " + PRICE + " " + AMOUNT + " " + PLAYER.toString();
	}
	
	public static Order fromString(String ORDER) {
		String[] L = ORDER.split(" ");
		
		if (L.length != 5)
			return null;
		
		OrderType type = OrderType.valueOf(L[0]);
		String ticker = L[1];
		float price = Float.parseFloat(L[2]);
		int amount = Integer.parseInt(L[3]);
		UUID player = UUID.fromString(L[4]);
		
		return new Order(type,ticker,price, amount, player);
	}
	
	public static CommandExecutor getCommandExecutor() {
		
		CommandExecutor executor = new CommandExecutor() {

			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
				if (label.equalsIgnoreCase("myorders")) {
					
					if (!(sender instanceof Player)) {
						A.sendMessage(sender, "sender-not-player");
						return true;
					}
					
					Player player = (Player) sender;
					
					if (!player.hasPermission("craftexchange.myorders") && !player.hasPermission("craftexchange.*order") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					ArrayList<Order> myorders = new ArrayList<Order>();
					
					for (Ticker t : A.getTickers()) {
						for (Order o : t.getBuyers()) {
							if (o.getPlayer() == player) {
								myorders.add(o);
							}
						}
						for (Order o : t.getSellers()) {
							if (o.getPlayer() == player) {
								myorders.add(o);
							}
						}
					}
					
					A.sendMessage(player, "myorders-text");
					for (Order o : myorders) {
						player.sendMessage(" - " + o.toString());
					}
					return true;
				}
				
				if (label.equalsIgnoreCase("cancelorder")) {
					
					if (!(sender instanceof Player)) {
						A.sendMessage(sender, "sender-not-player");
						return true;
					}
					
					Player player = (Player) sender;
					
					if (!player.hasPermission("craftexchange.cancelorder") && !player.hasPermission("craftexchange.*order") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length != 4) {
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
					
					Ticker ticker = A.getTicker(args[1]);
					
					if (ticker == null) {
						A.sendMessage(player, "ticker-unknown");
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
					
					OrderType type = OrderType.valueOf(args[0].toUpperCase());
					
					if (type == null) {
						A.sendMessage(player, "type-unknown");
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
					
					float price = 0;
					
					try {
						price = Float.parseFloat(args[2]);
					} catch (NumberFormatException e) {
						A.sendMessage(player, "invalid-number");
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
					
					int amount = 0;
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						A.sendMessage(player, "invalid-number");
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
					
					if (ticker.cancelOrder(player, type, price, amount)) {
						A.sendMessage(player, "cancelorder-success");
						return true;
					} else {
						A.sendMessage(player, "order-unknown");
						A.sendMessage(player, "cancelorder-usage");
						return true;
					}
				}
				
				return false;
			}
			
		};
		
		return executor;
	}
	
}
