import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Drone {
	public PerceptronMatrix perceptronMatrix;
	private String name;
	private int x;
	private int y;
	private double direction;
	private TheWorld world;
	private Sensor[] sensors;
	private Action[] actions;
	
	public Drone(String name, Action[] actions, Sensor[] sensors, TheWorld world, int x, int y, int direction) {
		this.name = name;
		this.actions = actions;
		this.sensors = sensors;
		this.world = world;
		this.x=x;
		this.y=y;
		this.direction=direction;
		
		Autonomous drone = new Autonomous(this.name);
		
		drone.setDirection(direction);
		this.world.add(drone, this.x, this.y);
		this.perceptronMatrix = new PerceptronMatrix(this.sensors.length,this.actions);

	}
	
	//set and get methods
	public String getName() {
		return this.name;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public double getDirection() {
		return this.direction;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setDirection(float dir) {
		this.direction = dir;
	}

	//uses training data to train the drone's perceptron matrix
	public void trainDrone(double[][] training_data) {
		this.perceptronMatrix.learnAllWeights(training_data);
	}
		
	//gets the visible x and y coordinates based on the drone's current orientation
	private int[][] getVisible(){
		
		int[][] visible = new int[this.sensors.length][2];
		int i = 0;
		for(Sensor sensor : this.sensors) {
			if (this.direction < 0.25) {		//up
				visible[i][0] = this.x + sensor.getX();
				visible[i][1] = this.y - sensor.getY();
			}
			else if (this.direction < 0.5) {	//right
				visible[i][0] = this.x + sensor.getY();
				visible[i][1] = this.y + sensor.getX();
			}
			else if (this.direction < 0.75) {	//down
				visible[i][0] = this.x - sensor.getX();
				visible[i][1] = this.y + sensor.getY();
			}
			else if (this.direction < 1.0) {	//left
				visible[i][0] = this.x - sensor.getY();
				visible[i][1] = this.y - sensor.getX();
			}
			i++;
		}
		return visible;
			
	}
	
	//gets the objects in the world that the drone can currently see
	private WorldObject[] getVisibleObjects(int[][] visible_cells) {
		
		WorldObject[] visible_objects = new WorldObject[visible_cells.length];
		int i=0;
		for(Sensor sensor : this.sensors) {
			int x = visible_cells[i][0];
			int y = visible_cells[i][1];
			if(this.world.getWorldArray()[y][x] instanceof WorldObject) {
				visible_objects[i] = this.world.getWorldArray()[y][x];
			}
			else {
				visible_objects[i] = null;
			}
			i++;
		}
		return visible_objects;
	}
	
	//uses the currently visible objects as inputs for the drone's perceptron matrix
	//perceptron matrix then makes a decision and outputs an action for the drone, which the drone then takes
	//if the drone is indecisive it will throw an error that will be caught in the run() function
	private void makeDecision(WorldObject[] visible_objects) throws indecisiveException {
		double[] inputs = new double[visible_objects.length];
		int i=0;
		for(Sensor sensor : this.sensors) {
			if(visible_objects[i]==null) {
				inputs[i] = 1.0;
			}
			else if(visible_objects[i].getClass().equals(sensor.getSignal())) {
				inputs[i] = -1.0;
			}
			else {
				inputs[i] = 0;
			}
			i++;
		}
		this.perceptronMatrix.setInputs(inputs);
		try {
			Action decision = this.perceptronMatrix.makeDecision();
			takeAction(decision);
		}
		catch(indecisiveException e) {
			throw e;
		}
	}
	
	//takes an action and updates the drone's position and orientation accordingly
	private void takeAction(Action action) {
		
		if(this.direction<0.25) {
			this.x+= action.getXDelta();
			this.y-= action.getYDelta();
		}
		else if(this.direction<0.5) {
			this.y+= action.getXDelta();
			this.x+= action.getYDelta();
		}
		else if(this.direction<0.75) {
			this.x-= action.getXDelta();
			this.y+= action.getYDelta();
		}
		else if(this.direction<1) {
			this.y-= action.getXDelta();
			this.x-= action.getYDelta();
		}
		this.direction += action.getDirectionDelta();
		int temp = Math.floorMod((int) (100*this.direction), 100);
		this.direction = temp/100.0;
	}
	
	//places the drone in the world and lets the drone run for a given number of steps
	//if the drone meets an indecisive decision, it runs a simulation to try and learn what to do
	//if the drone crashes, the run ends (hopefully shouldn't happen)
	public void run(int itterations) {
		while(itterations>0) {
			//System.out.println(itterations);

			int[][] visible = getVisible();
			//System.out.println(Arrays.deepToString(visible));
			//System.out.println(this.direction);
			world.display(this.name, visible);
			//world.display();
			WorldObject[] visibleObjects = getVisibleObjects(visible);
			try {
				makeDecision(visibleObjects);
			}
			catch(indecisiveException e) {
				runSimulation();
				break;
			}
			try {
				world.step(this.name,this.x,this.y,this.direction);
				try{
					TimeUnit.SECONDS.sleep(1);
				}
				catch(InterruptedException e){
					System.out.println("Wait issue");
				}								//This try/catch delay is just so that the simulation doesn't go too fast
			}
			catch(crashException e) {
				System.out.println("CRASH at (" + this.x + "," + this.y + ")");
				break;
			}
			itterations--;
		}
	}
	
	//drone runs a simulated world where the drone learns new actions if it faces a new situation
	//unimplemented currently
	private void runSimulation() {
		//to fill later
	}
	
}
