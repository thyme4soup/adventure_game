package adventure;

import java.util.ArrayList;

public class Interaction {
	private static ArrayList<Interaction> interactions;
	private Item input, output;
	private boolean removeEntity;
	public String msg;
	
	//initialize items
	private static Item stick = new Item("stick");
	private static Item rock = new Item("rock");
	private static Item sharprock = new Item("sharprock");
	private static Item hammer = new Item("hammer");

	public Interaction(Item input, Item output, boolean remove, String msg) {
		this.input = input;
		this.output = output;
		removeEntity = remove;
		this.msg = msg;
	}
	
	public static void createInteractions() {
		interactions = new ArrayList<>();
		
		interactions.add(new Interaction(hammer, sharprock, true,
				"You hammer away at the rock, and get some sharprocks."));
	}
	
}
