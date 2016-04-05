package adventure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class Tile extends JPanel{
	int width, height;
	boolean hasPlayer = false;
	boolean exposed = false;
	boolean discovered = false;
	Color background;
	static Color fade;
	static int exposedCount;
	
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	String[] entityList = new String[]{
			"altar",
			"village",
			"pond",
			"rock",
			"tree",
			"body"
	};
	int[] entityProbability = new int[] {
			0,
			20,
			9,
			5,
			3,
			0
	};
	boolean[] entityFat = new boolean[] {
			true,
			true,
			false,
			false,
			false,
			false
	};
	
	public Tile(int width, int height, boolean isStart) {
		this.width = width;
		this.height = height;
		this.setBackground(Color.BLACK);
		background = Color.GREEN;
		fade = new Color(0,0,0,0);
		exposedCount = 0;
		setPreferredSize(new Dimension(width, height));
		Random r = new Random();
		
		if(isStart) addEntity("village");
		else {
			for(int i = 0; i < entityList.length; i++) {
				if(entityProbability[i] == 0) continue;
				if(1 == r.nextInt(entityProbability[i])) {
					entities.add(new Entity(entityList[i], width, height));
					if(entityFat[i]) break;
				}
			}
		}
		removeOverlap();
	}
	
	public void removeOverlap() {
		//spread cheecks
		boolean clear = false;
		while(!clear) {
			clear = true;
			for(int i = 0; i < entities.size() - 1; i++) {
				for(int j = i+1; j < entities.size(); j++) {
					if(entities.get(i).getRect().intersects(entities.get(j).getRect())) {
						entities.get(i).genLoc();
						clear = false;
					}
				}
			}
		}
	}
	
	public void addEntity(String entity) {
		for(int i = 0; i < entityList.length; i++) {
			if(entity.equals(entityList[i])) entities.add(new Entity(entityList[i], width, height));
		}
	}
	
	public void removeEntity(String entity) {
		for(int i = 0; i < entities.size(); i++) {
			if(entity.equals(entities.get(i).type)) {
				entities.remove(i);
			}
		}
	}
	
	public boolean contains(String entity) {
		for(Entity e : entities) {
			if(entity.equals(e.type)) return true;
		}
		return false;
	}
	
	public boolean containsFat() {
		if(entities.size() == 1) {
			for(int i = 0; i < entityList.length; i++) {
				if(entities.get(0).type.equals(entityList[i]) && entityFat[i]) return true;
			}
		}
		return false;
	}
	
	public boolean setPlayer(boolean has) {
		hasPlayer = has;
		if(!exposed) expose();
		repaint();
		if(!discovered) {
			discovered = true;
			return true;
		}
		return false;
	}
	
	public void expose() {
		if(!exposed) exposedCount++;
		exposed = true;
	}
	
	public void refresh() {
		this.repaint();
		this.revalidate();
	}
	
	@Override
	public String toString() {
		String result = "You see ";
		if(entities.size() == 0) result += "nothing but grass";
		else if(entities.size() == 1) result += entities.get(0);
		else if(entities.size() == 2) {
			result += entities.get(0) + " and " + entities.get(1);
		}
		else {
			for(int i = 0; i < entities.size() - 1; i++) {
				result += entities.get(i).toString() + ", ";
			}
			result += "and " + entities.get(entities.size()-1).toString();
		}
		return result + ".";
	}
	
	public static void setFade(Color c) {fade = c;}
	public static Color getFade() {return fade;}
	
	@Override
	public void paintComponent(Graphics g) {
		if(exposed) {
			g.setColor(background);
			g.fillRect(0, 0, width, height);
			for(Entity entity : entities) {
				entity.draw(g);
			}
			if(hasPlayer) {
				g.setColor(Color.YELLOW);
				g.fillRect(width/2 - 1, height/2 - 1, 3, 3);
			}
			g.setColor(fade);
			g.fillRect(0, 0, width, height);
		} else {
			g.setColor(this.getBackground());
			g.fillRect(0, 0, width, height);
		}
	}
}

class Entity {
	String type = "";
	int width, height;
	int tileWidth, tileHeight;
	int x, y;
	int regenCap;
	int regenReset = 500;
	Color c;
	int shrink = 0;
	
	public Entity(String type, int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		regenCap = regenReset;
		this.type = type;
		switch(type.toLowerCase()) {
		case "pond":
			width = 3*tileWidth/7;
			height = width;
			c = Color.BLUE;
			break;
		case "rock":
			width = tileWidth/3;
			height = width;
			c = new Color(125, 125, 125);
			break;
		case "tree":
			width = tileWidth/4;
			height = width;
			c = new Color(16, 138, 11);
			break;
		case "body":
			width = tileWidth/5;
			height = width;
			c = new Color(192, 192, 192);
			break;
		case "village":
			width = tileWidth/2;
			height = tileWidth/2;
			c = new Color(135, 82, 2);
			break;
		case "altar":
			width = tileWidth/2;
			height = tileWidth/2;
			c = Color.RED;
			break;
		default:
			System.out.println("Un-handled entity, using defaults");
			width = 3;
			height = 3;
			c = Color.BLACK;
			break;
		}
		genLoc();
	}
	public void genLoc() {
		regenCap--;
		if(regenCap <= 0) {
			regenCap = regenReset;
			shrink++;
			//System.out.println(shrink);
		}
		Random r = new Random();
		x = r.nextInt(tileWidth - width);
		y = r.nextInt(tileHeight - height);
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, width - shrink, height - shrink);
	}
	
	public void draw(Graphics g) {
		switch(type) {
		//for unforeseen entities of various shapes, add a case
		case "altar":
			g.setColor(new Color(233, 175, 38));
			g.fillRect(tileWidth/4, tileHeight/4, tileWidth/2, tileHeight/2);
			g.setColor(new Color(245, 68, 2));
			g.fillRect(tileWidth/3 + 1, tileHeight/3 + 1, tileWidth/3 - 2, tileHeight/3 - 2);
			g.setColor(Color.BLACK);
			g.drawRect(tileWidth/3 + 1, tileHeight/3 + 1, tileWidth/3 - 3, tileHeight/3 - 3);
			g.drawRect(tileWidth/4, tileHeight/4, tileWidth/2, tileHeight/2);
			break;
		case "village": //example of a custom case
			g.setColor(new Color(135, 82, 2));
			g.fillRect(tileWidth/4 - 2, tileWidth/4 - 2, tileWidth/3 - 3, tileHeight/3 - 3);
			g.fillRect(7*tileWidth/12, tileHeight/4 - 2, tileWidth/3 - 3, tileHeight/3 - 3);
			g.fillRect(tileWidth/4 - 2, 7*tileWidth/12, tileWidth/3 - 3, tileHeight/3 - 3);
			g.fillRect(7*tileWidth/12, 7*tileHeight/12, tileWidth/3 - 3, tileHeight/3 - 3);
			g.setColor(Color.black);
			g.drawRect(tileWidth/4 - 2, tileWidth/4 - 2, tileWidth/3 - 3, tileHeight/3 - 3);
			g.drawRect(7*tileWidth/12, tileHeight/4 - 2, tileWidth/3 - 3, tileHeight/3 - 3);
			g.drawRect(tileWidth/4 - 2, 7*tileWidth/12, tileWidth/3 - 3, tileHeight/3 - 3);
			g.drawRect(7*tileWidth/12, 7*tileHeight/12, tileWidth/3 - 3, tileHeight/3 - 3);
			break;
		default: //oval case
			g.setColor(c);
			g.fillOval(x, y, width, height);
			switch(type) {
			case "tree":
				g.setColor(new Color(4, 80, 1));
				break;
			case "pond":
				g.setColor(new Color(1, 25, 80));
				break;
			case "rock":
				g.setColor(new Color(50, 50, 50));
				break;
			case "body":
				g.setColor(new Color(192, 192, 192));
				break;

			default:
				g.setColor(Color.BLACK);
				break;
			}
			g.drawOval(x, y, width, height);
			break;
		}
	}
	
	@Override
	public String toString() {
		String modifier = "a ";
		String vowels = "aeiou";
		if(vowels.contains(""+type.charAt(0))) modifier = "an ";
		return modifier + type;
		}
}