package adventure;

import java.awt.Color;
import java.util.ArrayList;

public class Player {
	public int x, y;
	public int food, water, health;
	public int maxFood, maxWater, maxHealth;
	private Console console;
	private Game container;
	public boolean awake = false;
	Inventory inv;
	
	
	public Player(Console console, Game container) {
		this.x = 7;
		this.y = 7;
		this.console = console;
		this.container = container;
		maxFood = 20;
		maxWater = 15;
		maxHealth = 20;
		food = maxFood;
		water = maxWater;
		health = maxHealth;
		inv = new Inventory(console);
	}
	
	public void up() {
		y -= 1;
		update();
	}
	
	public void down() {
		y += 1;
		update();
	}

	public void left() {
		x -= 1;
		update();
	}

	public void right() {
		x += 1;
		update();
	}
	
	public void update() {
		decFood(1);
		decWater(1);
		String status = "";
		if(food < 4) {
			status = "You are dying of hunger";
			health--;
		}
		else if(food < 7) status = "You need food";
		else if(food < 10) status = "You feel a little hungry";
		
		if(water < 10) {
			if(!status.equals("")) status += " and y";
			else status += "Y";
		}
		
		if(water < 4) {
			status += "ou are dying of thirst";
			health--;
		}
		else if(water < 7) status += "ou need water";
		else if(water < 10) status += "ou feel a little thirsty";
		
		status += ".";
		if(status.length() > 3) console.print(status);
	}
	
	public Color getFade() {
		int alpha = 0;
		int x = Math.min(food, water);
		if(x < 4) {
			alpha = 100 + (120 - (int)(120 * ((float)health/(float)maxHealth)));
		} else if(x < 7) {
			alpha = 50;
		} else if(x < 10) {
			alpha = 25;
		}
		
		if(alpha > 220) alpha = 220;
		
		Color color = new Color(0,0,0,alpha);
		return color;
	}
	
	public void discovery(String type) {
		switch(type) {
		case "village":
			console.print("The world seems smaller.");
			maxFood += 2;
			maxWater += 2;
			container.map.survey();
			break;
		default:
			break;
		}
	}
	
	public void decFood(int n) {
		food-= n;
		if(food < 0) food = 0;
	}
	
	public void decWater(int n) {
		water-= n;
		if(water < 0) water = 0;
	}
	
	public void addFood(int n) {
		food += n;
		if(food > maxFood) food = maxFood;
	}
	
	public void addWater(int n) {
		water += n;
		if(water > maxWater) water = maxWater;
	}
	
	/*
	 * THIS METHOD IS FOR USE WITH THE RECIPE CLASS.
	 * COMBINE MULTIPLE ITEMS FROM THE PLAYER'S INVENTORY.
	 */
	public void combine(Item[] items) {
		Item toCraft = Recipe.isRecipe(items);
		if(toCraft == null) {
			console.print("Nothing happens.");
		} else {
			if(Recipe.craft(toCraft, inv)) {
				console.print(Recipe.getRecipe(toCraft).msg);
			} else {
				console.print("You do not have all of those items!");
			}
		}
	}
	
	public String toSingular(String name) {
		for(Item i : inv.inventory.keySet()) {
			if(name.contains(i.name)) name = i.name;
		}
		return name;
	}
	
	public void maxFood() {food = maxFood;}
	
	public void maxWater() {water = maxWater;}
	
	public void maxHealth() {health = maxHealth;}
}
