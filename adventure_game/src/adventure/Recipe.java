package adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private static ArrayList<Recipe> recipes; //input, output
	private Item[] input;
	private Item output; //for simplicity, each recipe only has one output
	
	//initialize items
	private static Item stick = new Item("stick");
	private static Item rock = new Item("rock");
	private static Item hammer = new Item("hammer");
	
	public Recipe(Item[] in, Item out) {
		input = in;
		output = out;
	}
	
	public static void createRecipes() {
		//initialize recipes
		recipes = new ArrayList<>();
		
		//add recipes
		recipes.add(new Recipe(new Item[] {stick, rock}, hammer));
	}
	
	/**
	 * Checks if the input is a valid recipe
	 * @param in the item stack to check
	 * @return the Item if the recipe is valid, else null
	 */
	public Item isRecipe(Item[] in) {
		Item toReturn = null;
		for(Recipe r : recipes) {
			if(in.length == r.input.length) {
				//TODO: check if same
			}
		}
		return toReturn;
	}
	
	/**
	 * Checks if the player has the items to craft an item, private because called in craft
	 * in order to reduce confusion and clutter elsewhere
	 * @param i the desired item to craft
	 * @param inv the players inventory
	 * @return if the player has items to craft an item
	 */
	private boolean canCraft(Item i, Inventory inv) {
		
		return false;
	}
	
	/**
	 * crafts the item into the players inventory if possible
	 * @param i the item to craft
	 * @param inv the players inventory
	 * @return if the craft was successful
	 */
	public boolean craft(Item i, Inventory inv) {
		if(!canCraft(i, inv)) return false;
		return true;
	}
}

/*helper class
class ItemStack {
	Item item;
	int quantity;
	
	public ItemStack(Item item, int n) {
		this.item = new Item(item.toString());
		quantity = n;
	}
}*/