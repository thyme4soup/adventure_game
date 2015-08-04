package adventure;

public class Item {
	public String name;
	
	public Item(String name) {
		this.name = name;
	}
	
	public boolean is(Item i) {
		return this.name.equals(i.name);
	}
}
