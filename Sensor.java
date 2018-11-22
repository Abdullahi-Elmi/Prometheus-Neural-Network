/*
This class is used to represent sensors the drone is equiped with.
Each sensor has a name assiciated with it, the position of the sensor relative to the drone,
 what type of WorldObject it is sensitive to, and whether it is a positive or negative sensor.

All x and y values assume that the robot currently has direction=0 (i.e the drone is facing due north)

An example sensor would be:
name = "Forward Left Immovable"
signal = new Immovable()
x = "-1"
y = "1"
positive = true

If the object being sensed is different from the signal type, then a 0 is given to the Drone
If the object being sensed matches the signal type, then 1 is returned if (positive==True) and -1 else

**** TO DO ******
Currently the Drone object does all the work figuring out the value the sensor returns.
This code should be moved here so the sensors themselves work it out and the Drone just asks for it.
*/

public class Sensor {

	private Class<? extends WorldObject> signal;
	private int x;
	private int y;
	private boolean positive = true;

	public Sensor(WorldObject signal, int x, int y, boolean positive) { //constructor for when you want to specify positive/negative
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
		this.positive = positive;
	}
	public Sensor(WorldObject signal, int x, int y) { //constructor when you assume a sensor is positive (the normal workflow)
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
	}

	//setter/getter methods
	public Class getSignal() {
		return this.signal;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	//**TO DO **
	//This function takes a WorldObject and returns the value appropriate with this sensor
	// idk if use int or double makes more sense
	public int score(WorldObject object){
		return 0;
	}

	//depreciated, delete when score() is made
	public int weight() {
		if(this.positive) {
			return 1;
		}
		else {
			return 0;
		}
	}

}
