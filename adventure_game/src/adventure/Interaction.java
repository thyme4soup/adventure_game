package adventure;

import java.util.ArrayList;

public class Interaction {
	private static ArrayList<Interaction> interactions;
	private Item input, output;
	private String entity;
	private boolean removeEntity;
	public String msg;
	
	//initialize items
	private static Item stick = new Item("stick");
	private static Item rock = new Item("rock");
	private static Item sharp = new Item("sharp");
	private static Item hammer = new Item("hammer");

	public Interaction(Item input, Item output, String entity, boolean remove, String msg) {
		this.input = input;
		this.output = output;
		this.entity = entity;
		removeEntity = remove;
		this.msg = msg;
	}
	
	public static void createInteractions() {
		interactions = new ArrayList<>();
		
		//add new interactions here
		interactions.add(new Interaction(hammer, sharp, "rock", true,
				"You hammer away at the rock, and get some sharprocks."));
	}
	
	/**
	 * Gets the interaction, only if an interaction with the specified item and entities exist.
	 * @param item
	 * @param entities
	 * @return the first interaction that matches given input
	 */
	public static Interaction getInteraction(Item item, ArrayList<Entity> entities) {
		for(Interaction i : interactions) {
			for(Entity e : entities) {
				if(i.input.is(item) && i.entity.equals(e.type)) return i;
			}
		}
		return null;
	}
	
	/**
	 * Executes the interaction. Assumes that if an interaction is passed, it is valid.
	 * @param in the interaction
	 * @param t the current tile
	 * @param inv the player's inventory
	 */
	public static void interact(Interaction in, Tile t, Inventory inv) {
		inv.remove(in.input.name, 1);
		if(in.removeEntity) t.removeEntity(in.entity);
		inv.add(in.output, 1);
	}
	
	public String getEntityName() {return entity;}
	
}
