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
	public int razeCount;
	
	
	public Player(Console console, Game container) {
		this.x = 7;
		this.y = 7;
		this.console = console;
		this.container = container;
		maxFood = 10;
		maxWater = 8;
		maxHealth = 15;
		razeCount = 0;
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
		//System.out.println("food %: " + (100*food/maxFood));
		//System.out.println("water %: " + (100*water/maxWater));
		String status = "";
		if(100*food/maxFood <= 20) {
			status = "You are dying of hunger";
			decHealth(1);
		}
		else if(100*food/maxFood <= 40) status = "You need food";
		else if(100*food/maxFood <= 60) status = "You feel a little hungry";
		
		if(100*water/maxWater <= 60) {
			if(!status.equals("")) status += " and y";
			else status += "Y";
		}
		
		if(100*water/maxWater <= 20) {
			status += "ou are dying of thirst";
			decHealth(1);
		}
		else if(100*water/maxWater <= 40) status += "ou need water";
		else if(100*water/maxWater <= 60) status += "ou feel a little thirsty";
		
		status += ".";
		if(status.length() > 3) console.print(status);
	}
	
	public Color getFade() {
		int alpha = 0;
		int x = Math.min((100*food/maxFood), (100*water/maxWater));
		if(x <= 20) {
			alpha = 100 + (120 - (int)(120 * ((float)health/(float)maxHealth)));
		} else if(x <= 40) {
			alpha = 50;
		} else if(x <= 60) {
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
			maxFood += 3;
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
	
	public void decHealth(int n) {
		health -= n;
		if(health > 0) container.flashRed();
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
				decFood(1);
				decWater(1);
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

	public boolean hasWeapon() {
		String[] weapons = new String[]{"axe", "sword"};
		for(String s : weapons) {
			if(inv.contains(s)) return true;
		}
		return false;
	}
}
