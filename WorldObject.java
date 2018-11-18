
public interface WorldObject {	//Interface that all 3 objects in the world will implement
	public char getToken();
	public void setX(int newX);
	public void setY(int newY);
	public String getName();
	public void setDirection(double direction);
	public int getX();
	public int getY();			//initializing all of the necessary methods
}
