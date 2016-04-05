package adventure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Console extends JPanel implements ActionListener, KeyListener {
	static int x;
	static String[] content;
	static String[] hist;
	static int histNum;
	static JLabel[] labels;
	static JTextField field;
	static int offset;
	Game container;
	GridBagConstraints c;
	static boolean animating = false;
	String queuedPrint = "";
	String printer = "";
	
	static final String[] hints = new String[] {
		"You grow restless. Go north, if not south, west, or east.",
		"A flock of birds fly overhead, going south. Winter is coming.",
		"If only you had a stick and a rock, you could make a hammer.",
		"If only you had a stick and something sharp, you could make an axe.",
		"So many resources at hand, and you need only combine them.",
		"You catch the faint smell of cooking meat.",
		"You see heavy, rain-filled clouds on the horizon."
	};
	
	public Console(int x, int y, int border, Game container) {
		this.x = x;
		setPreferredSize(new Dimension(x, y));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, border));
		setBackground(Color.DARK_GRAY);
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		content = new String[10];
		hist = new String[10];
		histNum = 0;
		labels = new JLabel[content.length];
		field = new JTextField();
		this.container = container;
		addKeyListener(this);
	}
	
	public void initLabels() {
		for(int i = 0; i < content.length; i++) {
			content[i] = " ";
			hist[i] = "";
			labels[i] = new JLabel(content[i]);
			labels[i].setMinimumSize(new Dimension(x - 20, 17));
			labels[i].setAlignmentX(LEFT_ALIGNMENT);
			labels[i].setAlignmentY(TOP_ALIGNMENT);
			labels[i].setForeground(new Color(120 + 15*i, 120 + 15*i, 120 + 15*i));
			c.gridy = i;
			add(labels[i], c);
		}
		c.insets = new Insets(6,0,0,0);
		c.gridy++;
		add(field, c);
		
		field.setPreferredSize(new Dimension(x + 15, 20));
		field.setBorder(null);
		field.setForeground(Color.WHITE);
		field.setBackground(new Color(120, 120, 120));
		field.addActionListener(this);
		field.setFocusable(true);
		field.addKeyListener(this);
		update();
	}
	
	//handle print queue additions
	public void print(String s) {
		queuedPrint += "%"+s; //%included in all cases
		Game.timer.start();
		animating = true; //for typing animation
	}
	
	//for chopping off section to print and return resulting substring
	public String[] addLine(String s) {
		String[] result = new String[2];
		result[0] = s;
		result[1] = "";
		int maxChars = 74;
		boolean multiLine = false;
		int j = s.indexOf("%");
		if(j >= 0 && j < maxChars) { //check for new line code
			result[0] = s.substring(0, j);
			multiLine = true;
		}
		else if(s.length() > maxChars) { //check for line overflow
			int i = maxChars;
			while(i >= 0 && s.charAt(i) != ' ') i--;
			if(i <= 0) i = maxChars;
			result[0] = s.substring(0, i);
			multiLine = true;
		}
		//recursive handling of multi-line text (in case of overflow or %)
		if(multiLine) result[1] = (s.substring(result[0].length() + 1)); //+1 to cut out the un-needed space
		return result;
	}
	
	public void update() {

		field.setText("");
		for(int i = 0; i < labels.length; i++) {
			labels[i].setText(content[i]);
		}
	}
	
	//for convenience
	public void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch(Exception e) {e.printStackTrace();}
	}
	
	//startup
	public void begin() {
		field.requestFocus();
		field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
	}
	
	//clear console
	public void clear() {
		for(int i = 0; i < content.length; i++) content[i] = " ";
		update();
	}
	
	//red flash for unknown command
	public void unknown() {
		Game.unknownCounter++;
		Game.unknownCalled = true;
		
		//print random hint in the case of multiple unknowns
		if(Game.unknownCounter > 1 && Game.unknownCounter % 4 == 0) {
			if(container.map.p.awake) {
				Random r = new Random();
				print(hints[r.nextInt(hints.length)]);
			} else {
				print("Through the veil of sleep you feel a faint breeze on your face. It's time to wake up.");
			}
		}
		
		//red
		field.setBackground(new Color(225, 131, 131));
		animating = true;
	}
	
	//idk what this does
	public void updateHist(String s) {
		if(hist.length > 0)
			for(int i = hist.length - 1; i > 0; i--) hist[i] = hist[i - 1];
		hist[0] = s;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//TODO: disable enter while animating
		String s = e.getActionCommand().trim().toLowerCase();
		histNum = -1;
		if(!s.equals("")) {
			//print(e.getActionCommand());
			updateHist(s);
			field.setText("");
			container.handleCommand(s);
		}
		if(!animating) field.setBackground(new Color(120, 120, 120));
	}

	//handle any animations
	public void animate() {
		if(queuedPrint.length() > 0 || printer.length() > 0) { //only you can prevent forest fires
			
			if(printer.length() == 0 && queuedPrint.length() != 0) { //if printer is empty and there is still text queued
				//chop off chunk and set chunk to printer
				String[] input = addLine(queuedPrint);
				queuedPrint = input[1];
				printer = input[0];
			}
			if(printer.length() != 0) { //if printer either previously had text or has text now
				field.setText(field.getText() + printer.charAt(0));
				printer = printer.substring(1);
			}
			if(printer.length() == 0 && field.getText().length() > 0) { //if we just finished printing
				for(int i = 0; i < content.length - 1; i++) content[i] = content[i+1];
				content[content.length - 1] = field.getText();
				update();
			}
			
		} //end print animation code
		
		//begin color animation code
		Color cur = field.getBackground();
		Color dest = new Color(120, 120, 120);
		if(cur.getRGB() != dest.getRGB() && cur.getRed() > dest.getRed()) {
			field.setBackground(new Color(cur.getRed() - 10, cur.getGreen() - 1, cur.getBlue() - 1));
		} //end color animation code
		
		//animation finishing check
		if(cur.getRed() < dest.getRed() && queuedPrint.length() == 0 && printer.length() == 0) {
			field.setBackground(new Color(120, 120, 120));
			//field.setBorder(null);
			animating = false;
		}
	}
	
	public boolean isAnimating() {return animating;}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		//see action event todo
		if(e.getKeyCode() == 38) {
			if(histNum < hist.length - 1 && !hist[histNum + 1].equals("")) histNum++;
			if(!hist[histNum].equals("")) field.setText(hist[histNum]);
		} else if(e.getKeyCode() == 40) {
			if(histNum >= 0) histNum--;
			if(histNum >= 0) field.setText(hist[histNum]);
			else field.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
