package adventure;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class Game extends JFrame implements ActionListener {
	static Game game;
	static int gameX = 500;
	static int mapY = 500;
	static int consoleY = 200;
	static int borderW = 3;
	static Map map;
	static Console console;
	static Timer timer;
	static int unknownCounter = 0;
	static boolean unknownCalled = false;
	static String[] dreams = new String[] {
		"You're in a tunnel. The tunnel is long. You pick a direction and begin walking, but as you walk you find a dark and menacing shape before you. " +
				"You turn and run back down the tunnel, but you find another dark shape slowly approaching. You turn and run again, only to become confronted with the first shape. " +
				"Your legs grow tired as you run back and forth, and the shapes come closer and closer until you curl up on the ground, accepting your fate. You close your eyes tight.",
		"You see before you a beautiful flower. You bend to pick it, but a sharp pain in your finger gives you pause. You pull back your hand to see a small bead of blood growing in size. Your eyes water, but not for the pain. You close your eyes.",
		"You are aware of a colony of ants in a meadow. The colony is small. Yet, time passes and the ants build on their colony, expanding outwards and downwards. One colony becomes many colonies, and those colonies also expand outwards and downwards. " +
				"As the expansion peaks, however, rain comes. The tunnels are filled and the ants die by the thousands. The subsequent sun finds only a few ants left, shattered and scattered. You close your eyes in sorrow."
	};
	
	public Game() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setSize(gameX + 2*borderW, mapY + consoleY + 2*borderW);
		setTitle("Adventure Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setBackground(Color.DARK_GRAY);
		setResizable(false);
		setFocusable(true);
		//console.print(map.getCurrentTile().toString());
	}

	public static void main(String[] args) {
		game = new Game();
		Item.initializeItems();
		Recipe.createRecipes();
		Interaction.createInteractions();
		game.start();
		Game.console.print("You are asleep.");
	}
	
	public void start() {
		timer = new Timer(15, this);
		
		console = new Console(gameX + 2*borderW, consoleY + 2*borderW, borderW, this);
		map = new Map(gameX + 2*borderW, mapY + 2*borderW, borderW, console, this);
		
		add(map);
		add(console);
		pack();
		validate();
		map.genTiles();
		console.initLabels();
		console.begin();
		setVisible(true);
	}
	
	public void death() {
		console.print("");
		console.print("The world around you starts to spin as you collapse. You are engulfed in darkness. "
				+ "You lie down and close your eyes.");
		remove(map);
		map = new Map(gameX + 2*borderW, mapY + 2*borderW, borderW, console, this);
		add(map);
		remove(console);
		add(console);
		pack();
		map.genTiles();
		repaint();
		revalidate();
		console.field.requestFocus();
		flashRed();
	}
	
	public void handleCommand(String command) {
		//console.print(command);
		if(command.equals("clear")) {
			console.clear();
			return;
		} else if(command.equals("exit")) {
			System.exit(0);
			return;
		} else if(command.contains("help") || command.equals("what") || command.contains("wtf")) {
			if(!map.p.awake)
				console.print("You are still asleep.");
			else if(map.p.awake) {
				console.print("To the north you see distant mountains. "
						+ "To the south you see dark woods. "
						+ "To the west you see a glimmering ocean. "
						+ "To the east you see a shimmering desert.");
				console.print("What do you want to do?");
			}
		} else if(map.p.awake) {
			if(command.contains("look")) {
				console.print("You look around you.");
				console.print(map.getCurrentTile().toString());
			} else if(command.contains("north")) {
				map.move(0);
			} else if(command.contains("west")) {
				map.move(1);
			} else if(command.contains("south")) {
				map.move(2);
			} else if(command.contains("east")) {
				map.move(3);
			} else if(command.contains("expose")) {
				map.expose();
			} else if(command.contains("inventory") || command.contains("items")) {
				map.inventory();
			} else if(command.contains("collect")) {
				if(command.contains("stick")) {
					map.collect("stick");
				} else if(command.contains("rock")) {
					map.collect("rock");
				} else {
					console.print("You don't see any of those around.");
				}
			} else if(command.contains("toss")) {
				String[] div = command.split("\\s+");
				//for(String s : div) System.out.println(s);
				int num = 0;
				if(div.length > 3 || div.length == 1) {
					console.unknown();
					timer.start();
				} else if(div.length == 2) {
					if(div[0].equals("toss")) map.toss(div[1], 1);
					else map.toss(div[0], 1);
				} else {
					try {
						num = Integer.parseInt(div[1]);
					} catch (Exception e) {
						//e.printStackTrace();
						console.unknown();
						timer.start();
					}
					if(map.p.inv.get(div[2]) != null)
						map.toss(div[2], num);
					else console.print(div[2] + " is not an item.");
				}
			} else if(command.contains("use")) { 
				//TODO: edit so command is of format: use [item] on [entity]
				String[] div = command.split("\\s+");
				if(div.length <= 1 || div.length > 2) {
					console.unknown();
					timer.start();
				} else {
					map.use(div[1]);
				}
			} else if(command.contains("combine")) {
				ArrayList<Item> items = new ArrayList<>();
				if(command.trim().length() == 7) {
					console.unknown();
					timer.start();
				} else {
					command = command.replace("combine", "").trim();
					int sizeInv = map.p.inv.size();
					for(Item i : Item.itemList) {
						if(sizeInv == 0 && !items.isEmpty()) break;
						if(command.contains(i.name)) {
							if(!map.p.inv.contains(i.name)) {
								items.clear();
								break;
							} else {
								while(command.contains(i.name)) {
									items.add(new Item(i.name));
									command = command.replaceFirst(i.name, "");
									sizeInv--;
								}
							}
						}
					}
					if(!items.isEmpty()) {
						Item[] list = new Item[items.size()];
						for(int i = 0; i < list.length; i++) {
							list[i] = items.get(i);
						}
						map.p.combine(list);
					} else console.print("You don't have all of those items!");
				}
			} else if (command.contains("make")) {
				console.print("That won't work. Try 'combine.'");
			} else if(command.contains("drink") || command.contains("water")) {
				map.drink();
			} else if(command.contains("climb") && command.contains("tree")) {
				if(map.getCurrentTile().contains("tree")) {
					console.print("You climb the tree, and survey the land around you.");
					map.survey();
					map.p.decFood(1);
				} else {
					console.print("There are no trees to climb.");
				}
			} else if(command.contains("pillage")) {
				if(map.getCurrentTile().contains("village")) {
					if(map.p.hasWeapon()) {
						console.print("You pillage the town... the simple townspeople fall meekly beneath your weapon and the huts take flame quickly. You feel yourself grow stronger.");
						map.pillage();
					}
					else console.print("The townspeople are small and know not of violence, yet still you lack the weapons to conquer them.");
				}
				else console.print("There are no villages in sight.");
			} else if(command.contains("eat") || command.contains("food")) {
				map.eat();
			} else if(command.contains("sleep") || command.contains("rest")) {
				map.rest();
			} else if(command.contains("kill myself") || command.contains("wreck myself") || command.contains("suicide")) {
				console.print(" ");
				console.print("You weave a rope out of dry grass, and find a nearby tree.");
				console.print("You attempt to hang yourself, but the rope breaks and you fall in a heap.");
				death();
			} else if(command.contains("pray")) {
				map.pray();
			} else if(map.getCurrentTile().contains("body")) {
				if(command.contains("loot") || command.contains("search body"))
					console.print("The body has only a few rags and a pair of well worn boots. They fit you perfectly.");
				else if(command.contains("eat") && command.contains("body")) {
					map.p.maxFood();
					map.p.health -= 2;
					console.print("The body is skeleton thin, but you tear in as only one facing starvation can. The skin gives way and the congealed blood explodes to the back of your throat, making you gag, but you continue." +
							"As you ravage the body you grow more and more frenzied. The thighs are stripped in minutes. The cheeks fill your own. The eyes squish almost satisfyingly as you eat them, and you find that the hard lenses are particularly nice to suck on.");
					console.print("You shove the mangled corpse under a bush to decompose completely");
					map.getCurrentTile().removeEntity("body");
				}
				else if(command.contains("bury") && command.contains("body")) {
					console.print("You set about burying the body. Shadows lengthen as you dig a shallow grave. "
							+ "Once you finish, you drag the body over and lay it in. The body sags to the bottom of the grave, "
							+ "and sighs as the first scoop of dirt lands on its stomach. After the grave is full you place a flower on the grave. "
							+ "As you do so, your stomach rumbles.");
					map.getCurrentTile().removeEntity("body");
				}
			} else if(command.contains("stats") 
					|| command.contains("check myself")
					|| command.contains("thirst")
					|| command.contains("hunger")
					|| command.contains("health")) {
				console.print(String.format("You rate your health %d/%d", map.p.health, map.p.maxHealth));
				console.print(String.format("You rate your repletion  %d/%d", map.p.food, map.p.maxFood));
				console.print(String.format("You rate your hydration %d/%d", map.p.water, map.p.maxWater));
			} else if(command.contains("dick") || command.contains("penis") || command.contains("cock")) {
				console.print("You don't have one of those.");
			} else {
				console.unknown();
				timer.start();
			}
		} //when asleep
		else if(command.contains("get up") || command.contains("wake up")) {
			map.p.awake = true;
			map.tiles[map.p.x][map.p.y].setPlayer(true);
			console.print("You open your eyes.");
			console.print(map.getCurrentTile().toString());
		} else if(command.contains("dream")) {
			console.print("");
			Random r = new Random();
			console.print(dreams[r.nextInt(dreams.length)]);
			console.print("You are asleep.");
		} else {
			console.unknown();
			timer.start();
		}
		
		if(!unknownCalled) unknownCounter = 0;
		else unknownCalled = false;
		
		Tile.fade = map.getPlayer().getFade();
		map.repaint();
	}
	
	public void flashRed() {
		map.animate();
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!console.isAnimating() && !map.isAnimating()) {
			timer.stop();
			return;
		}
		if(console.isAnimating()) {
			console.animate();
			console.repaint();
		}
		if(map.isAnimating()) {
			map.animate();
			map.repaint();
		}
	}
}
