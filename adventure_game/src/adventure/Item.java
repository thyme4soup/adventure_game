package adventure;

public class Item {
	public String name;
	private static String[] itemList = new String[] {
		"stick",
		"rock",
		"hammer",
		"sharp"
	};
	
	public Item(String name) {
		for(int i = 0; i < itemList.length; i++) {
			if(name.contains(itemList[i])) {
				name = itemList[i];
				break;
			}
			if(i == itemList.length - 1) name = "unknown";
		}
		this.name = name;
	}
	
	public boolean is(Item i) {
		return this.name.equals(i.name);
	}
}
