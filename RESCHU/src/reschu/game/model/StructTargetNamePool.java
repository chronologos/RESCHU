package reschu.game.model;

public class StructTargetNamePool {
	private String name;
	private boolean used;
	
	public StructTargetNamePool(String s) {name = s; used = false; }
	
	public String getName() {return name;}
	public synchronized boolean isUsed() {return used;}
	public synchronized void setUsed(boolean u) {used = u;}
}
