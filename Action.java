
public class Action {
	private String name;
	private int x_delta;
	private int y_delta;
	private double direction_delta;
	
	public Action(String name, int x, int y, double dir) {
		this.name = name;
		this.x_delta = x;
		this.y_delta = y;
		this.direction_delta = dir;
	}
	
	public int getXDelta(){
		return x_delta;
	}
	public int getYDelta(){
		return y_delta;
	}
	public double getDirectionDelta(){
		return direction_delta;
	}

	public String getName() {
		return this.name;
	}
}
