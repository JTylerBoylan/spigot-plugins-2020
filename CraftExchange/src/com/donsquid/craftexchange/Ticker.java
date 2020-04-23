package com.donsquid.craftexchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.donsquid.craftexchange.Order.OrderType;

public class Ticker {

	private String TICKER;
	
	private ItemStack ITEM;
	
	private int VOLUME;
	
	private ArrayList<Order> BUY_ORDERS;
	private ArrayList<Order> SELL_ORDERS;
	
	private File FILE;
	
	public Ticker(String TICKER) {
		this.TICKER = TICKER.toUpperCase();
		this.ITEM = new ItemStack(Material.AIR);
		this.BUY_ORDERS = new ArrayList<Order>();
		this.SELL_ORDERS = new ArrayList<Order>();
		this.VOLUME = 0;
		this.FILE = A.getFileManager().getTickerFile(TICKER);
		save();
	}
	
	public Ticker(String TICKER, ItemStack ITEM, ArrayList<Order> BUY_ORDERS, ArrayList<Order> SELL_ORDERS, int VOLUME) {
		this.TICKER = TICKER.toUpperCase();
		this.ITEM = ITEM;
		this.BUY_ORDERS = BUY_ORDERS;
		this.SELL_ORDERS = SELL_ORDERS;
		this.VOLUME = VOLUME;
		this.FILE = A.getFileManager().getTickerFile(TICKER);
	}
	
	public String getTicker() {
		return TICKER;
	}
	
	public ItemStack getItemStack() {
		return ITEM.clone();
	}
	
	public ArrayList<Order> getBuyers(){
		return BUY_ORDERS;
	}
	
	public ArrayList<Order> getSellers(){
		return SELL_ORDERS;
	}
	
	public int getVolume() {
		return VOLUME;
	}
	
	public float getBid(int size) {
		
		ArrayList<Order> bids = getHighestBidders();
		float sum = 0;
		for (int i = 0; i < size; i++) {
			if (i >= bids.size()) {
				size = i;
				break;
			}
			sum += bids.get(i).getPrice();
		}
		
		if (size == 0)
			return 0;
		
		return sum / size;
		
	}
	
	public float getAsk(int size) {
		
		ArrayList<Order> asks = getLowestAskers();
		float sum = 0;
		for (int i = 0; i < size; i++) {
			if (i >= asks.size()) {
				size = i;
				break;
			}
			sum += asks.get(i).getPrice();
		}
		
		if (size == 0)
			return 0;
		
		return sum / size;
		
	}
	
	public final ArrayList<Order> getHighestBidders() {
		
		boolean sorted = false;
		Order temp;
		
		while(!sorted) {
			
			sorted = true;
			for (int i = 0; i < BUY_ORDERS.size()-1; i++)
				if (BUY_ORDERS.get(i).getPrice() < BUY_ORDERS.get(i+1).getPrice()) {
					
					temp = BUY_ORDERS.get(i);
					BUY_ORDERS.set(i, BUY_ORDERS.get(i+1));
					BUY_ORDERS.set(i+1, temp);
					sorted = false;
					
				}
			
		}

		return BUY_ORDERS;
	}
	
	public final ArrayList<Order> getLowestAskers() {
		
		boolean sorted = false;
		Order temp;
		
		while(!sorted) {
			
			sorted = true;
			for (int i = 0; i < SELL_ORDERS.size()-1; i++)
				if (SELL_ORDERS.get(i).getPrice() > SELL_ORDERS.get(i+1).getPrice()) {
					
					temp = SELL_ORDERS.get(i);
					SELL_ORDERS.set(i, SELL_ORDERS.get(i+1));
					SELL_ORDERS.set(i+1, temp);
					sorted = false;
					
				}
			
		}

		return SELL_ORDERS;
	}
	
	public float getValue(int v) {
		v = v%3;
		switch(v) {
		case 0:
			return getVolume();
		case 1:
			if (getHighestBidders().size() == 0)
				return 0;
			return getHighestBidders().get(0).getPrice();
		case 2:
			if (getLowestAskers().size() == 0)
				return Float.MAX_VALUE;
			return getLowestAskers().get(0).getPrice();
		default:
			return getVolume();
		}
	}
	
	public void setTicker(String TICKER) {
		this.TICKER = TICKER;
		save();
	}
	
	public void setItemStack(ItemStack ITEM) {
		this.ITEM = ITEM;
		save();
	}
	
	public void setBuyers(ArrayList<Order> BUY_ORDERS) {
		this.BUY_ORDERS = BUY_ORDERS;
		save();
	}
	
	public void setSellers(ArrayList<Order> SELL_ORDERS) {
		this.SELL_ORDERS = SELL_ORDERS;
		save();
	}
	
	public void addToVolume(int SIZE) {
		this.VOLUME += SIZE;
		save();
	}
	
	public int Sell(Player player, float price, int amount) {
		
		if (amount == 0)
			return 0;
		
		int count = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null)
				continue;
			if (item.isSimilar(ITEM) && count < amount) {
				if (item.getAmount() > (amount-count)) {
					item.setAmount(item.getAmount()-(amount-count));
					count = amount;
				} else {
					count += item.getAmount();
					player.getInventory().remove(item);
				}
			} else
				continue;
			
		}
		
		price = (price/amount)*count;
		amount = count;
		
		if (amount == 0) {
			A.sendMessage(player, "item-not-found");
			return 0;
		}

		ArrayList<Order> buyers = new ArrayList<Order>(getHighestBidders());
		
		int n_amount = amount;
		float n_price = price;
		
		while (buyers.size() > 0 && n_amount > 0 && (n_price/n_amount) <= buyers.get(0).getPrice()) {
			
			int c_ = 0;
			for (int i = 0; i < buyers.get(0).getAmount() && i < n_amount; i++){
				if (c_*buyers.get(0).getAmount() < n_price)
					c_++;
			}
			
			n_price -= c_*buyers.get(0).getPrice();
			n_amount -= c_;
			
			buyers.get(0).fill(c_);
			
			if (buyers.get(0).getAmount() < 1) {
				BUY_ORDERS.remove(buyers.get(0));
				buyers.remove(0);
			}
			
		}
		
		A.getEconomy().depositPlayer(player, price - n_price); // give money from filled
		
		if (n_amount > 0) {
			Order order = new Order(OrderType.SELL, TICKER, n_price/n_amount, n_amount, player.getUniqueId()); // create new order for leftover amount
			SELL_ORDERS.add(order);
		}
		
		if (amount - n_amount > 0)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', A.getMessages().getString("prefix") + " Sold " + (amount - n_amount) + "/" + amount + " of " + TICKER + " for " + (price - n_price)/(amount - n_amount) + " each."));
		if (n_amount > 0)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', A.getMessages().getString("prefix") + "Created " + n_amount + " sell orders of " + TICKER + " at " + (n_price/n_amount) + " each."));
		
		save();
		
		return amount - n_amount;
	}
	
	public int Buy(Player player, float price, int amount) {
		
		if (amount == 0)
			return 0;
		
		float balance = (float) A.getEconomy().getBalance(player);
		
		if (balance < price) {
			amount = (int)((amount/price)*balance);
			price = balance;
		}
		
		if (amount == 0) {
			A.sendMessage(player, "no-money");
			return 0;
		}
		
		A.getEconomy().withdrawPlayer(player, price);
		
		ArrayList<Order> sellers = new ArrayList<Order>(getLowestAskers());
		
		float n_price = price;
		int n_amount = amount;
		
		while (sellers.size() > 0 && n_amount > 0 && (n_price/n_amount) >= sellers.get(0).getPrice()) {
			
			int c_ = 0;
			for (int i = 0; i < sellers.get(0).getAmount() && i < n_amount; i++){
				if (c_*sellers.get(0).getAmount() < n_price)
					c_++;
			}
			
			n_price -= c_*sellers.get(0).getPrice();
			n_amount -= c_;
			
			sellers.get(0).fill(c_);
			
			if (sellers.get(0).getAmount() < 1) {
				SELL_ORDERS.remove(sellers.get(0));
				sellers.remove(0);
			}
			
		}
		
		ItemStack item = getItemStack();
		item.setAmount(amount - n_amount);
		
		player.getInventory().addItem(item);
		
		if (n_amount > 0) {
			Order order = new Order(OrderType.BUY, TICKER, n_price/n_amount, n_amount, player.getUniqueId());
			BUY_ORDERS.add(order);
		}
		
		if (amount - n_amount > 0)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', A.getMessages().getString("prefix") + " Bought " + (amount - n_amount) + "/" + amount + " of " + TICKER + " for " + (price - n_price)/(amount - n_amount) + " each."));
		if (n_amount > 0)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', A.getMessages().getString("prefix") + "Created " + n_amount + " buy orders of " + TICKER + " at " + (n_price/n_amount) + " each."));
		
		save();
		
		return amount - n_amount;
	}
	
	public boolean cancelOrder(Player USER, OrderType type, float price, int amount) {
		
		if (type == OrderType.BUY)
			for (Order o : BUY_ORDERS) {
				if (o.getPlayer() == USER && o.getType() == type && o.getPrice() == price && o.getAmount() == amount) {
					o.cancel();
					BUY_ORDERS.remove(o);
					save();
					return true;
				}
			}
		if (type == OrderType.SELL)
			for (Order o : SELL_ORDERS) {
				if (o.getPlayer() == USER && o.getType() == type && o.getPrice() == price && o.getAmount() == amount) {
					o.cancel();
					SELL_ORDERS.remove(o);
					save();
					return true;
				}
			}
		return false;
	}
	
	public String toString() {
		return TICKER;
	}
	
	public FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(FILE);
	}
	
	public void save() {
		FileConfiguration ticker_config = getConfig();
		
		ticker_config.set("item-stack", ITEM);
		
		ticker_config.set("volume", VOLUME);
		
		ArrayList<String> order_list = new ArrayList<String>();
		
		for (Order order : BUY_ORDERS) 
			order_list.add(order.toString());
		for (Order order : SELL_ORDERS) 
			order_list.add(order.toString());
		
		ticker_config.set("order-list", order_list);
		
		try {
			ticker_config.save(FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CommandExecutor getCommandExecutor() {
		CommandExecutor executor = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
				if (!(sender instanceof Player)) {
					A.sendMessage(sender, "sender-not-player");
					return true;
				}
				
				Player player = (Player) sender;
				
				if (label.equalsIgnoreCase("newticker")) {
					
					if (!player.hasPermission("craftexchange.newticker") && !player.hasPermission("craftexchange.*ticker") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length != 1) {
						A.sendMessage(player, "newticker-usage");
						return true;
					}
					
					String tckr = args[0];
					
					if (A.getTicker(tckr) != null) {
						A.sendMessage(player, "ticker-already-exists");
						return true;
					}
					
					ItemStack item = player.getInventory().getItemInMainHand().clone();
					item.setAmount(1);
					
					
					if (item.getType() == Material.AIR) {
						A.sendMessage(player, "newticker-usage");
						return true;
					}
					
					Ticker ticker = new Ticker(tckr);
					ticker.setItemStack(item);
					
					A.addTicker(ticker);
					
					A.sendMessage(player, "newticker-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("setticker")) {
					
					if (!player.hasPermission("craftexchange.setticker") && !player.hasPermission("craftexchange.*ticker") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length != 1) {
						A.sendMessage(player, "setticker-usage");
						return true;
					}
					
					String tckr = args[0];
					
					Ticker ticker = A.getTicker(tckr);
					
					if (ticker == null) {
						A.sendMessage(player, "ticker-unknown");
						return true;
					}
					
					ItemStack item = player.getInventory().getItemInMainHand();
					
					if (item.getType() == Material.AIR) {
						A.sendMessage(player, "setticker-usage");
						return true;
					}
					
					ticker.setItemStack(item);
					
					A.sendMessage(player, "setticker-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("deleteticker")) {
					
					if (!player.hasPermission("craftexchange.deleteticker") && !player.hasPermission("craftexchange.*ticker") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					if (args.length != 1) {
						A.sendMessage(player, "deleteticker-usage");
						return true;
					}
					
					String tckr = args[0];
					
					Ticker ticker = A.getTicker(tckr);
					
					if (ticker == null) {
						A.sendMessage(player, "ticker-unknown");
						return true;
					}
					
					A.removeTicker(tckr);
					
					A.sendMessage(player, "deleteticker-success");
					
					return true;
				}
				
				if (label.equalsIgnoreCase("listtickers")) {
					
					if (!player.hasPermission("craftexchange.listtickers") && !player.hasPermission("craftexchange.*ticker") && !player.hasPermission("craftexchange.*")) {
						A.sendMessage(player, "no-permission");
						return true;
					}
					
					int size = A.getTickers().size();
					
					if (args.length > 0) {
						try {
							size = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							// NOTHING
						}
					}
					
					A.sendMessage(player, "listtickers-text");
					
					for (int i = 0; i < size; i++) {
						Ticker t = A.getTickers().get(i);
						player.sendMessage( "- " + t.getTicker() + " : " + t.getItemStack().getType().toString());
					}
					
					return true;
				}
				
				return false;
			}
			
		};
		
		return executor;
	}
	
}
