package adventure;

import java.util.ArrayList;

public class Item {
	public String name;
	public String definition;
	public static ArrayList<Item> itemList = new ArrayList<>();
	
	//List of all items
	public static Item stick = new Item("stick", "an item collected in areas that have trees");
	public static Item sharp_rock = new Item ("sharp rock", "an item commonly created by using a hammer on a rock");
	public static Item rock = new Item("rock", "a common rock");
	public static Item hammer = new Item("hammer", "a rock on a stick");
	public static Item axe = new Item("axe", "a sharp rock on a stick");
	public static Item wood = new Item("wood", "an item collected using an axe on a tree");
	public static Item null_item = new Item("null");
	
	public static void initializeItems() {
		//ITEMS WHOSE NAMES CONTAIN ANOTHER ITEM MUST GO BEFORE
		//EXAMPLE: SHARP_ROCK, ROCK
		
		itemList.add(stick);
		itemList.add(sharp_rock);
		itemList.add(rock);
		itemList.add(hammer);
		itemList.add(axe);
		itemList.add(wood);
		itemList.add(null_item);
	}
	
	public Item(String name) {
		for(int i = 0; i < itemList.size(); i++) {
			if(name.contains(itemList.get(i).name)) {
				name = itemList.get(i).name;
				break;
			}
			if(i == itemList.size() - 1) name = "unknown";
		}
		this.name = name;
	}
	public Item(String name, String definition) {
		for(int i = 0; i < itemList.size(); i++) {
			if(name.contains(itemList.get(i).name)) {
				name = itemList.get(i).name;
				break;
			}
			if(i == itemList.size() - 1) name = "unknown";
		}
		this.name = name;
		this.definition = definition;
	}
	
	public boolean is(Item i) {
		return this.name.equals(i.name);
	}
}
