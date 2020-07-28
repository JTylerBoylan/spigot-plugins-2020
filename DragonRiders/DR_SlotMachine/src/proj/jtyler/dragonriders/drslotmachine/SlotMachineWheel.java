package proj.jtyler.dragonriders.drslotmachine;

import java.util.List;
import java.util.Random;

public class SlotMachineWheel {
	
	private static final int wheel_sizes = 100;
	
	private List<SlotItem> items = null;
	private int[] chance_spread = null;
	
	private int wheel1_index = 1;
	private int wheel2_index = 1;
	private int wheel3_index = 1;
	
	public SlotMachineWheel(List<SlotItem> items, int[] chance_spread) {
		this.items = items;
		this.chance_spread = chance_spread;
		generateWheels();
	}
	
	private SlotItem[] wheel1 = null;
	private SlotItem[] wheel2 = null;
	private SlotItem[] wheel3 = null;
	
	private void generateWheels() {
		Random r = new Random();
		wheel1 = new SlotItem[wheel_sizes];
		wheel2 = new SlotItem[wheel_sizes];
		wheel3 = new SlotItem[wheel_sizes];
		for (int i = 0 ; i < wheel_sizes; i++) {
			wheel1[i] = items.get(chance_spread[r.nextInt(chance_spread.length)]);
			wheel2[i] = items.get(chance_spread[r.nextInt(chance_spread.length)]);
			wheel3[i] = items.get(chance_spread[r.nextInt(chance_spread.length)]);
		}
	}
	
	public void nextWheel1() {
		wheel1_index++;
	}
	public void nextWheel2() {
		wheel2_index++;
	}
	public void nextWheel3() {
		wheel3_index++;
	}
	
	public SlotItem peekWheel1Previous() {
		return wheel1[wheel1_index-1];
	}
	public SlotItem peekWheel2Previous() {
		return wheel2[wheel2_index-1];
	}
	public SlotItem peekWheel3Previous() {
		return wheel3[wheel3_index-1];
	}
	
	public SlotItem peekWheel1Current() {
		return wheel1[wheel1_index];
	}
	public SlotItem peekWheel2Current() {
		return wheel2[wheel2_index];
	}
	public SlotItem peekWheel3Current() {
		return wheel3[wheel3_index];
	}
	
	public SlotItem peekWheel1Next() {
		return wheel1[wheel1_index+1];
	}
	public SlotItem peekWheel2Next() {
		return wheel2[wheel2_index+1];
	}
	public SlotItem peekWheel3Next() {
		return wheel3[wheel3_index+1];
	}
	
	
}
