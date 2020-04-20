package com.jtylerboylan.marketplace.guis;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jtylerboylan.marketplace.Business;
import com.jtylerboylan.marketplace.Core;
import com.jtylerboylan.marketplace.Entity;

public class BusinessBalanceGUI extends UserGUI {

	private Business business;
	
	boolean custom_input;
	boolean deposit;
	
	DecimalFormat dc = new DecimalFormat("#.##");
	
	public BusinessBalanceGUI(Player user, Business business, boolean custom_input, boolean deposit) {
		super(user);
		this.business = business;
		this.custom_input = custom_input;
		this.deposit = deposit;
	}

	public void load() {

		if (custom_input) {
			user.closeInventory();
			String tag = deposit ? "&a&lDEPOSIT" : "&d&lWITHDRAW";
			user.sendMessage(cc(prefix + " &e&lTYPE THE AMOUNT YOU WANT TO " + tag + "&e&l! " + prefix));
			user.sendMessage(cc(prefix + " &7Business balance: " + business.getBalance() + " &8&o(Enter 0 to cancel)"));
			return;
		}
		
		Inventory business_balance = Bukkit.createInventory(user, 9, cc(business_prefix + business.getName()));
		
		ItemStack view_tile = UserGUI.tile(Material.EMERALD, 1, (short) 0, cc("&bBalance"), cc("&7Balance: &2$" + dc.format(business.getBalance())));
		ItemStack deposit_tile = UserGUI.tile(Material.INK_SACK, 1, (short) 10, cc("&aDeposit"), cc("&7Balance: &2$" + dc.format(business.getBalance())), cc("&2Click to deposit"));
		ItemStack withdraw_tile = UserGUI.tile(Material.INK_SACK, 1, (short) 13, cc("&dWithdraw"), cc("&7Balance: &2$" + dc.format(business.getBalance())), cc("&5Click to withdraw"));
		ItemStack withdraw_all = UserGUI.tile(Material.WOOL, 1, (short) 2, cc("&d&lWithdraw All"), cc("&7Balance: &2$" + dc.format(business.getBalance())), cc("&5Click to withdraw all"));
		
		business_balance.setItem(0, view_tile);
		business_balance.setItem(2, deposit_tile);
		business_balance.setItem(4, withdraw_tile);
		business_balance.setItem(6, withdraw_all);
		business_balance.setItem(8, UserGUI.back_tile);
		
		user.openInventory(business_balance);
		
	}

	public void clickSlot(int slot) {
		switch (slot) {
		case 2:
			UserGUI.openGUI(user, new BusinessBalanceGUI(user, business, true, true));
			break;
		case 4:
			UserGUI.openGUI(user, new BusinessBalanceGUI(user, business, true, false));
			break;
		case 6:
			Core.getEconomy().depositPlayer(user, business.getBalance());
			user.sendMessage(cc(prefix + " &aBusiness > $" + dc.format(business.getBalance()) + " was withdrawed from " + business.getName() + "&a!"));
			business.setBalance(0);
			UserGUI.openGUI(user, new BusinessBalanceGUI(user, business, false, false));
			break;
		case 8:
			UserGUI.openGUI(user, new BusinessPageGUI(user, business));
			break;
		}
		
	}

	public void stringInput(String string) {
		if (!isDouble(string)) {
			user.sendMessage(prefix + " &cError > Invalid entry!");
			return;
		}
		double d = Double.parseDouble(string);
		if (deposit) {
			if (Core.getEconomy().getBalance(user) < d) {
				user.sendMessage(cc(prefix + " &cDenied > Insufficient funds!"));
				return;
			}
			Core.getEconomy().withdrawPlayer(user, d);
			business.addBalance(d);
			user.sendMessage(cc(prefix + " &aBusiness > $" + d + " was deposited to " + business.getName() + "&a!"));
			UserGUI.openGUI(user, new BusinessBalanceGUI(user,business,false,false));
		} else {
			if (business.getBalance() < d) {
				user.sendMessage(cc(prefix + " &cDenied > Business has insufficient funds!"));
				return;
			}
			business.removeBalance(d);
			Core.getEconomy().depositPlayer(user, d);
			user.sendMessage(cc(prefix + " &aBusiness > $" + d + " was withdrawed from " + business.getName() + "&a!"));
			UserGUI.openGUI(user, new BusinessBalanceGUI(user,business,false,false));
		}
	}

	public boolean hasAccess(Player player) {
		Entity entity = new Entity(player);
		if (entity.hasAccess(business))
			return true;
		return false;
	}
	
	private boolean isDouble( String str ){
	    try{
	        Double.parseDouble( str );
	        return true;
	    } catch( Exception e ){
	        return false;
	    }
	}

}
