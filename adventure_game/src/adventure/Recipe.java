package adventure;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private static Map<ItemStack[], ItemStack> recipes; //input, output
	
	//stores the defined recipes in map
	public static void createRecipes() {
		//init recipes
		recipes = new HashMap<>();
		
		ItemStack stick = new ItemStack(new Item("stick"), 1);
		ItemStack rock = new ItemStack(new Item("rock"), 1);
		ItemStack hammer = new ItemStack(new Item("hammer"), 1);
		
		recipes.put(new ItemStack[] {stick, rock}, hammer);
	}
	
	/**
	 * Checks if the input is a valid recipe
	 * @param in the item stack to check
	 * @return the Item if the recipe is valid, else null
	 */
	public Item isRecipe(ItemStack[] in) {
		
		return null;
	}
	
	/**
	 * Checks if the player has the items to craft an item, private because called in craft
	 * in order to reduce confusion and clutter elsewhere
	 * @param i the desired item to craft
	 * @param inv the players inventory
	 * @return if the player has items to craft an item
	 */
	private boolean canCraft(ItemStack i, Inventory inv) {
		
		return false;
	}
	
	/**
	 * crafts the item into the players inventory if possible
	 * @param i the item to craft
	 * @param inv the players inventory
	 * @return if the craft was successful
	 */
	public boolean craft(ItemStack i, Inventory inv) {
		if(!canCraft(i, inv)) return false;
		return true;
	}
}

//helper class
class ItemStack {
	Item item;
	int quantity;
	
	public ItemStack(Item item, int n) {
		this.item = new Item(item.toString());
		quantity = n;
	}
}