package adventure;

import java.util.ArrayList;

public class Item {
	public String name;
	private static ArrayList<Item> itemList = new ArrayList<>();
	
	//List of all items
	public static Item stick = new Item("stick");
	public static Item rock = new Item("rock");
	public static Item hammer = new Item("hammer");
	public static Item sharp = new Item ("sharp");
	public static Item axe = new Item("axe");
	public static Item wood = new Item("wood");
	
	public static void initializeItems() {
		itemList.add(stick);
		itemList.add(rock);
		itemList.add(hammer);
		itemList.add(sharp);
		itemList.add(axe);
		itemList.add(wood);
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
	
	public boolean is(Item i) {
		return this.name.equals(i.name);
	}
}
