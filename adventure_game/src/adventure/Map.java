package adventure;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;

public class Map extends JPanel {
	static int x, y, border;
	static boolean[] eventQueue = new boolean[] {
		false,
		false
	};
	Tile[][] tiles;
	GridBagConstraints c;
	Player p;
	Console console;
	Game container;
	int exposedCount = 0;
	static int dist = 4;
	static Random rand;
	
	static boolean animating = false;
	
	public Map(int x, int y, int border, Console console, Game container) {
		this.x = x;
		this.y = y;
		this.border = border;
		this.container = container;
		setPreferredSize(new Dimension(x, y));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, border));
		setBackground(Color.DARK_GRAY);
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		p = new Player(console, container);
		this.console = console;
		rand = new Random();
	}
	
	public void genTiles() {
		tiles = new Tile[15][15];
		int tileWidth = getWidth()/tiles.length;
		int tileHeight = getHeight()/tiles[0].length;
		
		int ponds = 0;
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				tiles[i][j] = new Tile(tileWidth, tileHeight, (i == 7 && j == 7));
				c.gridx = i;
				c.gridy = j;
				if(tiles[i][j].contains("pond")) ponds++;
				add(tiles[i][j], c);
			}
		}
		//Color background = new Color(167, 175, 60);
		//Color dest = new Color(22, 192, 16);
		ponds -= 20;
		if(ponds < 0) ponds = 0;
		if(ponds > 10) ponds = 10;
		int r = (int) (167 - 14.5 * ponds);
		int g = (int) (175 + 1.7 * ponds);
		int b = (int) (60 - 4.4 * ponds);
		
		for(Tile[] tileRow : tiles) {
			for(Tile tile : tileRow) {
				tile.background = new Color(r, g, b);
			}
		}
		
		p.x = (int) Math.ceil(tiles.length/2);
		p.y = (int) Math.ceil(tiles[p.x].length/2);
		//tiles[p.x][p.y].setPlayer(true);
		balance();
	}
	
	public void expose() {
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				if(i == 7 && j == 8) continue;
				handleMilestones(tiles[i][j]);
				tiles[i][j].expose();
			}
		}
	}
	
	public void balance() {
		//balance villages
		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 15; j++) {
				if(tiles[i][j].contains("village")) {
					for(int k = i - dist; k < i + dist; k++) {
						for(int l = j - dist; l < j + dist; l++) {
							if(k < 0 || k > 14 || l < 0 || l > 14) continue;
							if(tiles[k][l].contains("village")) {
								if(k == 7 && l == 7) continue;
								if(k == i && l == j) continue;
								tiles[k][l].entities.clear();
							}
						}
					}
				}
			}
		}
		
		for(int a = 0; a < 14; a += 8) {
			for(int b = 0; b < 14; b += 8) {
				boolean has = false;
				outloop:
				for(int i = a; i < a + 6; i++) {
					for(int j = b; j < b + 6; j++) {
						if(tiles[i][j].contains("village")) {
							has = true;
							break outloop;
						}
					}
				}
				
				if(!has) {
					int x = rand.nextInt(5) + a;
					int y = rand.nextInt(5) + b;
					tiles[x][y].entities.clear();
					tiles[x][y].addEntity("village");
				}
				has = false;
				
				repaint();
				revalidate();
			}
		}
	}
	
	//Player actions
	public void drink() {
		if(getCurrentTile().contains("pond")) {
			console.print("You kneel at the pond and drink water.");
			p.maxWater();
		} else if(getCurrentTile().contains("village")) {
			console.print("You drink some water at the village fountain.");
			p.maxWater();
		} else console.print("There is no water in sight.");
	}
	
	public void survey() {
		for(int i = p.x - 1; i <= p.x + 1; i++) {
			for(int j = p.y - 1; j <= p.y + 1; j++) {
				if(!(i < 0 || j < 0 || i > tiles.length - 1 || j > tiles[i].length - 1)) {
					handleMilestones(tiles[i][j]);
					tiles[i][j].expose();
				}
			}
		}
		repaint();
	}
	
	public void eat() {
		if(getCurrentTile().contains("village")) {
			console.print("You acquire some meat at the village, and gobble it down.");
			p.maxFood();
		} else console.print("There is no food in sight.");
	}
	
	public void rest() {
		if(getCurrentTile().contains("village")) {
			console.print("You find a bed, and lay down to rest.");
			p.maxHealth();
		} else console.print("There is no place to rest.");
	}
	
	public void inventory() {
		p.inv.print();
	}
	
	public void collect(String name) {
		switch(name) {
			case "stick":
				if(getCurrentTile().contains("tree")) {
					console.print("You break some sticks off of the tree.");
					p.inv.add(new Item("stick"), 2);
					getCurrentTile().removeEntity("tree");
					p.decFood(1);
					p.decWater(1);
				} else {
					console.print("There are no sticks in sight.");
				}
				break;
			case "rock":
				if(getCurrentTile().contains("rock")) {
					console.print("You collect a pile of rocks.");
					p.inv.add(new Item("rock"), 3);
					getCurrentTile().removeEntity("rock");
					p.decFood(2);
					p.decWater(1);
				} else {
					console.print("There are no rocks in sight.");
				}
				break;
			default:
					
		}
	}
	
	/*
	 * THIS METHOD IS FOR USE WITH THE INTERACTION CLASS.
	 * USE ITEM FROM THE PLAYER'S INVENTORY ON ENVIRONMENT.
	 */
	public void use(String itemName) {
		Item item = new Item(itemName);
		if(!p.inv.contains(item.name)) console.print("You don't have a " + itemName + ".");
		else {
			Interaction in = Interaction.getInteraction(item, getCurrentTile().entities);
			if(in == null) console.print("Nothing to use the " + item.name + " with.");
			else {
				Interaction.interact(in, getCurrentTile(), p.inv);
				console.print(in.msg);
				p.decFood(1);
				p.decWater(1);
			}
		}
	}
	
	public void pray() {
		if(getCurrentTile().contains("altar")) {
			if(p.razeCount <= 0)
				console.print("You kneel at the altar and pray for a while. You feel a bit better.");
			else if(p.razeCount <= 2)
				console.print("You kneel at the altar and pray for a while. There is only silence.");
			else
				console.print("The ones who erected this altar are weak and simple. Their dead god has nothing to offer you.");
		} else {
			int choice = rand.nextInt(3);
			switch (choice) {
				case 0:
					console.print("There's no church in the wild.");
					break;
				case 1:
					console.print("What's a god to a non-believer?");
					break;
				case 2:
					console.print("You cry out to a non-existent god.");
					break;
				default:
						
			}
		}
	}
	
	public void pillage() {
		getCurrentTile().removeEntity("village");
		p.maxFood += 3;
		p.maxWater += 3;
		p.maxHealth += 2;
		p.decFood(3);
		p.decWater(3);
		p.razeCount++;
	}
	
	public void toss(String name, int amount) {
		String sname = name.substring(0, name.length() - 1);
		if(!p.inv.contains(name)) {
			if(p.inv.contains(sname)) name = sname;
			else console.print("You don't have any " + name + "s.");
		}
		if(p.inv.remove(name, amount)) {
			if(amount > 1) name += "s";
			console.print("You toss " + amount + " " + name);
		}
		else console.print("You don't have that many " + name + "s.");
	}
	
	public void move(int dir) {
		int prevX = p.x;
		int prevY = p.y;
		boolean moved = false;
		switch(dir) {
		case 0:
			if(getCurrentTile().contains("village")) {
				console.print("You leave the safety of the village and head north into the wild," 
						+ " where you'll have to find your own food and water.");
			} else {
				console.print("You gather your things and head north.");
			}
			if(p.y > 0) {
				p.up();
				moved = true;
			}
			else console.print("You are met with a large mountain range, and snow begins to swirl down around you."
					+ " You can't go any further. You turn around and head back.");
			break;
		case 1:
			if(getCurrentTile().contains("village")) {
				console.print("You leave the safety of the village and head west into the wild," 
						+ " where you'll have to find your own food and water.");
			} else {
				console.print("You gather your things and head west.");
			}
			if(p.x > 0) {
				p.left();
				moved = true;
			}
			else console.print("You find a long coastline, and can't go any "
					+ "further. You turn around and head back.");
			break;
		case 2:
			if(getCurrentTile().contains("village")) {
				console.print("You leave the safety of the village and head south into the wild," 
						+ " where you'll have to find your own food and water.");
			} else {
				console.print("You gather your things and head south.");
			}
			if(p.y < 14) {
				p.down();
				moved = true;
			}
			else console.print("You hear loud howling, and your skin crawls. "
					+ "You can't go any further. You turn around and head back");
			break;
		case 3:
			if(getCurrentTile().contains("village")) {
				console.print("You leave the safety of the village and head east into the wild," 
						+ " where you'll have to find your own food and water.");
			} else {
				console.print("You gather your things and head east.");
			}
			if(p.x < 14) {
				p.right();
				moved = true;
			}
			else console.print("You find an interminable desert, devoid of life. "
					+ "You can't go any further. You turn around and head back");
		}
		tiles[prevX][prevY].setPlayer(false);
		if(tiles[p.x][p.y].setPlayer(true)) {
			for(Entity e : tiles[p.x][p.y].entities) {
				p.discovery(e.type);
			}
		}
		if(moved) sponGen();
		handleMilestones(getCurrentTile());
		
		//What you see when you get there
		if(p.health > 0)
			console.print(getCurrentTile().toString());
		else {
			console.print("You see nothing.");
			container.death();
		}
	}
	
	//spontaneous stuff
	public void sponGen() {
		growTrees();
	}
	
	private void growTrees() {
		for(Tile[] tileRow : tiles) {
			for(Tile tile : tileRow) {
				Random r = new Random();
				if(1 == r.nextInt(3 * tiles.length * tiles[0].length) 
						&& !(tile.contains("tree") || tile.containsFat())) {
					tile.addEntity("tree");
					tile.removeOverlap();
				}
			}
		}
	}
	
	public void handleMilestones(Tile t) {
		int lastTile = (15 * 15);
		System.out.println(Tile.exposedCount);
		if(Tile.exposedCount == 20 || eventQueue[0]) {
			if(!t.containsFat()) {
				t.addEntity("body");
				eventQueue[0] = false;
			}
			else eventQueue[0] = true;
		}
		if(Tile.exposedCount == lastTile) {
			t.entities.clear();
			t.addEntity("altar");
		}
	}
	
	public void animate() {
		//calculate gradient based on health percentage
		int gradient = 130 - (100*p.health/p.maxHealth);
		
		if(animating == false) {
			animating = true;
			for(Tile[] tileRow : tiles)
				for(Tile t : tileRow)
					t.setBackground(new Color(gradient, 0, 0));
			return;
		}
		
		Color cur = tiles[0][0].getBackground();
		Color dest = new Color(0, 0, 0);
		
		//Calculate dif to about 20 iterations
		int dif = gradient/20;
		
		if(cur.getRed() <= dest.getRed() || cur.getRed() < dif) {
			cur = dest;
			animating = false;
		}
		
		if(cur.getRGB() != dest.getRGB()) {
			cur = new Color(cur.getRed() - dif, cur.getGreen(), cur.getBlue());
			for(Tile[] tileRow : tiles)
				for(Tile t : tileRow) {
					t.setBackground(cur);
					Tile.setFade(new Color(cur.getRed(), cur.getGreen(), cur.getBlue(), Tile.getFade().getAlpha()));
				}
		}
	}
	
	//get stuff
	public Tile getCurrentTile() {return tiles[p.x][p.y];}
	
	public Player getPlayer() {return p;}
	
	public boolean isAnimating() {return animating;}
}


