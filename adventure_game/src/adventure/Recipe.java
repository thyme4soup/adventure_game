package adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
	
	public static Recipe getRecipe(Item i) {
		for(Recipe r : recipes) {
			if(r.output.is(i)) return r;
		}
		return null;
	}
	
	/**
	 * Checks if the input is a valid recipe
	 * @param in the item stack to check
	 * @return the Item if the recipe is valid, else null
	 */
	public static Item isRecipe(Item[] in) {
		Item toReturn = null;
		for(Recipe r : recipes) {
			if(in.length == r.input.length) {
				if(sameElements(in, r.input)) {
					toReturn = r.output;
					break;
				}
			}
		}
		return toReturn;
	}
	
	private static boolean sameElements(Item[] a, Item[] b) {
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < b.length; j++) {
				if(a[i].is(b[j])) break;
				if(j == b.length - 1) return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the player has the items to craft an item, private because called in craft
	 * in order to reduce confusion and clutter elsewhere
	 * @param i the desired item to craft
	 * @param inv the players inventory
	 * @return if the player has items to craft an item
	 */
	private static boolean canCraft(Item item, Inventory inv) {
		Recipe r = getRecipe(item);
		if(r == null) return false;
		ArrayList<Item> inventory = inv.getInventory();
		for(int i = 0; i < r.input.length; i ++) {
			if(inventory.isEmpty()) return false;
			for(int j = 0; j < inventory.size(); j++) {
				if(r.input[i].is(inventory.get(j))) {
					inventory.remove(j);
					break;
				}
				if(j == inventory.size() - 1) return false; //inventory does not have a required part
			}
		}
		return true;
	}
	
	/**
	 * crafts the item into the players inventory if possible
	 * @param i the item to craft
	 * @param inv the players inventory
	 * @return if the craft was successful
	 */
	public static boolean craft(Item item, Inventory inv) {
		if(!canCraft(item, inv)) return false;
		Recipe r = getRecipe(item);
		for(int i = 0; i < r.input.length; i++) {
			inv.remove(r.input[i].name, 1);
		}
		inv.add(r.output, 1);
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