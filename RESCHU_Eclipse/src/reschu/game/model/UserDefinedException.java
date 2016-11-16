package reschu.game.model;

public class UserDefinedException extends Exception
{ 
	private static final long serialVersionUID = 4639071009913933407L;
	public UserDefinedException() {}
	public UserDefinedException(String msg) { super(msg); }
	
}
