package adventure;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Inventory {
	Map<Item, Integer> inventory;
	Console console;
	
	public Inventory(Console console) {
		inventory = new HashMap<>();
		this.console = console;
	}
	
	public void add(Item i, int amount) {
		if(inventory.isEmpty()) {
			inventory.put(i, amount);
			return;
		}
		Item toReplace = null;
		Item toPut = null;
		for(Item j : inventory.keySet()) {
			if(i.name.equals(j.name)) {
				toReplace = j;
				break;
			} 
		}
		if(toReplace != null) inventory.replace(toReplace, inventory.get(toReplace) + amount);
		else inventory.put(i, amount);
	}
	
	public boolean remove(String s, int amount) {
		Item i = get(s);
		if(inventory.get(i) < amount) return false;
		else if(inventory.get(i) == amount) inventory.remove(i);
		else inventory.replace(i,  inventory.get(i) - amount);
		return true;
	}
	
	public void print() {
		if(inventory.isEmpty()) console.print("You have nothing in your inventory.");
		else {
			console.print("Inventory:");
			for(Item i : inventory.keySet()) {
				String s = inventory.get(i) + " " + i.name;
				if(inventory.get(i) > 1) s += "s";
				console.print(s);
			}
		}
	}
	
	public boolean contains(String s) {
		for(Item i : inventory.keySet()) {
			if(i.name.equals(s)) return true;
		}
		return false;
	}
	
	//return 0 if yes to both, 1 if no type, 2 if not enough
	public int contains(String s, int n) {
		for(Item i : inventory.keySet()) {
			if(i.name.equals(s)) {
				if(inventory.get(i) >= n) return 0;
				else return 2;
			}
		}
		return 1;
	}
	
	public Item get(String s) {
		String sname = s.substring(0, s.length() - 1);
		for(Item i : inventory.keySet()) {
			if(i.name.equals(s) || i.name.equals(sname)) return i;
		}
		return null;
	}
	
	public ArrayList<Item> getInventory() {
		ArrayList<Item> a = new ArrayList<>();
		for(Item i : inventory.keySet()) {
			a.add(i);
		}
		return a;
	}
	
	public void clear() {
		inventory.clear();
	}
}
