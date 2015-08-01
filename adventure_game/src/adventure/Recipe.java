package adventure;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
	private static Map<ItemStack[], ItemStack> recipes; //input, output
	
	public static void createRecipes() {
		//init recipes
		recipes = new HashMap<ItemStack[], ItemStack>();
		
		ItemStack stick = new ItemStack(new Item("stick"), 1);
		ItemStack rock = new ItemStack(new Item("rock"), 1);
		ItemStack hammer = new ItemStack(new Item("hammer"), 1);
		recipes.put(
				new ItemStack[] {
						stick,
						rock
				}, hammer);
		
	}
	
	public static boolean canCraft(Item i, Inventory inv) {
		return true;
	}
	
	public void craft(Item i, Inventory inv) {
		
	}
}

class ItemStack {
	Item item;
	int quantity;
	public ItemStack(Item item, int n) {
		this.item = new Item(item.toString());
		quantity = n;
	}
}