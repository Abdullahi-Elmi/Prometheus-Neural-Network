
public class Sensor {

	private Class<? extends WorldObject> signal;
	private int x;
	private int y;
	private boolean positive = true;
	
	public Sensor(WorldObject signal, int x, int y, boolean positive) {
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
		this.positive = positive;	
	}
	public Sensor(WorldObject signal, int x, int y) {
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
	}

	public Class getSignal() {
		return this.signal;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int weight() {
		if(this.positive) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
}
