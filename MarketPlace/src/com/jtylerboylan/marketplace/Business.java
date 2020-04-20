package com.jtylerboylan.marketplace;

import java.util.ArrayList;
import java.util.List;

public class Business {

	private String business_id;
	
	private String display_name;
	
	private Entity owner;
	
	private List<String> permit_list;
	
	private double balance;
	
	public Business (Entity owner, String business_id) {
		this.business_id = business_id;
		this.display_name = business_id;
		this.owner = owner;
		this.permit_list = new ArrayList<String>();
		this.balance = 0;
	}
	
	public Business (Entity owner, String business_id, String display_name, List<String> permit_list, double balance) {
		this.business_id = business_id;
		this.display_name = display_name;
		this.owner = owner;
		this.permit_list = permit_list;
		this.balance = balance;
	}
	
	public String getID() {
		return business_id;
	}
	
	public String getName() {
		return display_name;
	}
	
	public void setName(String name) {
		this.display_name = name;
	}
	
	public Entity getOwner() {
		return owner;
	}

	public List<String> getPermitted(){
		return permit_list;
	}
	
	public boolean isPermitted(Entity entity) {
		return permit_list.contains(entity.getID());
	}
	
	public void permit(String entity_id) {
		permit_list.add(entity_id);
	}
	public void unpermit(String entity_id) {
		permit_list.remove(entity_id);
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public void addBalance(double add) {
		this.balance+=add;
	}
	public void removeBalance(double remove) {
		this.balance-=remove;
	}
	
}
