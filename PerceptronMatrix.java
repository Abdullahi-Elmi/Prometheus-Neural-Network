import java.util.Arrays;
import java.util.stream.DoubleStream;

public class PerceptronMatrix {
	private double[] inputs;
	private double[] outputs;
	private Action[] actions;
	private double[][] parameters;
	private double[] bias;
	
	public PerceptronMatrix(int input_number, Action[] actions) {
		int	output_number = actions.length;
		
		this.inputs = new double[input_number];
		this.outputs = new double[output_number];
		this.actions = actions;
		this.parameters = new double[output_number][input_number];
		this.bias = new double[output_number];
	}
	
	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}
	public Action makeDecision() throws indecisiveException{
		calculateOutputs();
		double max_score = 0;
		int index = 0;
		int max_index = 0;
		for(double score : outputs) {
			if(score>max_score) {
				max_score = score;
				max_index = index;
			}
			if(score == max_score) {
				double rand = Math.random();
				if(rand < 0.5) {
					max_index = index;
				}		
			}
			index++;
		}
		System.out.println(Arrays.toString(this.inputs));
		System.out.println(Arrays.toString(this.outputs));

		if(max_score < 0.55) {
			indecisiveException ind = new indecisiveException("Max score was:" + max_score + " which is less than the required 0.3");
			throw ind;
		}
		else {
			return actions[max_index];
		}
		
	}
	public double[] getOutputs() {
		return this.outputs;
	}
		
	private static double sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}

	private double[] matrixMultiplication(double[] vector, double[][] matrix) {
	    int rows = matrix.length;
	    int columns = matrix[0].length;

	    double[] result = new double[rows];

		for(int row=0; row<rows;row++) {
			double sum=0;
			for(int column=0; column<columns;column++) {
				sum += matrix[row][column] * vector[column];
			}
			result[row] = sum;
		}
		return result;
	}
	private double[] vectorAddition(double[] vector1, double[] vector2) {
		double[] returnVector = new double[vector1.length];
		for(int i=0;i<vector1.length;i++) {
			returnVector[i] = vector1[i]+vector2[i];
		}
		return returnVector;
		
	}
	public void calculateOutputs() {
		this.outputs = matrixMultiplication(this.inputs, this.parameters);
		this.outputs = vectorAddition(this.outputs, this.bias);
		DoubleStream stream = Arrays.stream(this.outputs).map(x ->{return Math.round(sigmoid(x)*100000)/100000.0;});
		this.outputs = stream.toArray();	
	}
	
	private double predict(double pbias, double[] pweights, double[] pinputs) {
		
		double activation = pbias;
		for(int i = 0; i < pinputs.length; i++){
			activation += pweights[i] * pinputs[i];
		}
		return activation;
	}
	private double meanSquareError(double[][] data_inputs, double[] data_classifications, double[] weights, double bias) {
		double error = 0;
		for (int i=0;i<data_inputs.length;i++) {
			double polyval = bias;
			for(int j=0;j<data_inputs[i].length;j++) {
				polyval += data_inputs[i][j] * weights[j];
			}
			error += Math.pow(data_classifications[i] - polyval, 2);
		}
		return (error/data_inputs.length);		
	}
	
	private void learnRowWeights(int row_number, double[][] training_data) {
		double[] weights = this.parameters[row_number].clone();
		double bias = this.bias[row_number];
		double[][] inputs = new double[training_data.length][this.inputs.length];
		double[] outputs =  new double[training_data.length];
		
		for(int row=0;row<training_data.length;row++) {
			for(int column=0;column<training_data[row].length;column++) {
				if(column < this.inputs.length) {
					inputs[row][column] = training_data[row][column];
				}
				else {
					outputs[row] = training_data[row][column];
				}
			}
		}
		int epoch = 10000000; 
		double stepsize = 1e-4;
		double mseCutoff = 1e-20;
		for(int i=0;i<epoch; i++) {	
			double[] sum = new double[this.inputs.length+1];
			
			for(int j=0;j<training_data.length;j++) {
				double prediction = predict(bias,weights,inputs[j]);
				double error = prediction - outputs[j];
				sum[0] += error;
				for(int k=1;k<sum.length;k++) {
					sum[k] += error * inputs[j][k-1];
				}				
			}
			
			bias -= stepsize * (sum[0]/ training_data.length);
			for(int k=1;k<sum.length;k++) {
				weights[k-1] -= stepsize * (sum[k]/ training_data.length);
			}
			
			if( (meanSquareError(inputs, outputs, this.parameters[row_number],this.bias[row_number]) - meanSquareError(inputs, outputs, weights,bias)) < mseCutoff) {
				this.parameters[row_number] = weights.clone();
				this.bias[row_number] = bias;
				break;
			}
			this.parameters[row_number] = weights.clone();
			this.bias[row_number] = bias;
		}
	}
	public void learnAllWeights(double[][] training_data) {
		double[][] training_subset = new double[training_data.length][this.inputs.length+1];
		for(int i=0;i<this.parameters.length;i++) {
			
			for(int row=0;row<training_subset.length;row++) {
				for(int column=0;column<training_subset[row].length;column++) {
					if(column<this.inputs.length) {
						training_subset[row][column] = training_data[row][column];
					}
					else {
						training_subset[row][column] = training_data[row][this.inputs.length+i];
					}
				}
			}
			learnRowWeights(i,training_subset);
		}
	}
    private void printRow(double[] row, int rowNum) {
    	System.out.printf("% 6f |",this.bias[rowNum]);
        for (double i : row) {
            System.out.printf("% 6f  ",i);
        }
        System.out.printf("%-5s",this.actions[rowNum].getName());
        System.out.println();
    }
    
	public void print() {
		System.out.printf("%9s  ", "bias");
		for (int i =0;i<this.inputs.length;i++) {
			String out = "Input" + (i+1);
            System.out.printf("%9s  ",out);
        }
		System.out.println("");
		for(int i=0;i<this.parameters.length;i++) {
			printRow(this.parameters[i],i);
		}
	}
}
