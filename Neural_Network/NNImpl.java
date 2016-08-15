/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> outputNodes=null;// list of the output layer nodes
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[][] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=trainingSet.get(0).classValues.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
			
		//Output node layer
		outputNodes=new ArrayList<Node> ();
		for(int i=0;i<outputNodeCount;i++)
		{
			Node node=new Node(4);
			//Connecting output layer nodes with hidden layer nodes
			for(int j=0;j<hiddenNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}	
			outputNodes.add(node);
		}
	}
	
	/**
	 * Get the output from the neural network for a single instance
	 * Return the idx with highest output values. For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.5], it should return 2. 
	 * The parameter is a single instance. 
	 */
	
	public int calculateOutputForInstance(Instance inst)
	{
		// Set input nodes.
    for (int inputIndex = 0; inputIndex < inputNodes.size() - 1; inputIndex++)
    {
      inputNodes.get(inputIndex).setInput(inst.attributes.get(inputIndex));
    }

    // Calculate output for hidden nodes.
    for (Node hiddenNode : hiddenNodes)
    {
      hiddenNode.calculateOutput();
    }

    // Calculate output for output nodes and find the highest.
    int output = 0;
    double maxOutput = 0;
    
    for (int outputIndex = 0; outputIndex < outputNodes.size(); outputIndex++)
    {
      Node outputNode = outputNodes.get(outputIndex);
      outputNode.calculateOutput();
      double currentOutput = Math.round(outputNode.getOutput() * 10.0) / 10.0;
      if (currentOutput >= maxOutput)
      {
        maxOutput = currentOutput;
        output = outputIndex;
      }
    }

    return output;
	}
	
	
	
	
	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */
	
	public void train()
	{
		for (int epoch = 0; epoch < maxEpoch; epoch++)
    {
      // Update weights after each instance.
      for (Instance trainingInstance : trainingSet)
      {
        // Get outputs and targets.
        calculateOutputForInstance(trainingInstance);
        ArrayList<Double> outputs = new ArrayList<Double>();
        for (int classIndex = 0; classIndex < trainingInstance.classValues.size(); classIndex++)
        {
          outputs.add(outputNodes.get(classIndex).getOutput());
        }

        ArrayList<Integer> targets = trainingInstance.classValues;

        // Calculate error.
        ArrayList<Double> errors = new ArrayList<Double>();
        for (int classIndex = 0; classIndex < outputs.size(); classIndex++)
        {
          errors.add(targets.get(classIndex) - outputs.get(classIndex));
        }

        // Calculate weight changes from hidden nodes to output nodes.
        double[][] changeHiddenToOutput = new double[hiddenNodes.size()][outputNodes.size()];
        double[] hiddenDeltaSums = new double[hiddenNodes.size()];
        for (int outputIndex = 0; outputIndex < outputNodes.size(); outputIndex++)
        {
          double deltaOutput = outputs.get(outputIndex) > 0 ? errors.get(outputIndex) : 0.0;
          ArrayList<NodeWeightPair> outputParents = outputNodes.get(outputIndex).parents;
          
          for (int hiddenIndex = 0; hiddenIndex < hiddenNodes.size(); hiddenIndex++)
          {
            changeHiddenToOutput[hiddenIndex][outputIndex]
                = learningRate * hiddenNodes.get(hiddenIndex).getOutput() * deltaOutput;
            hiddenDeltaSums[hiddenIndex] += outputParents.get(hiddenIndex).weight * deltaOutput;
          }
        }

        // Calculate weight changes from input nodes to hidden nodes.
        // Note that the bias node at hidden layer is not involved.
        double[][] changeInputToHidden = new double[inputNodes.size()][hiddenNodes.size() - 1];
        for (int hiddenIndex = 0; hiddenIndex < hiddenNodes.size() - 1; hiddenIndex++)
        {
          double deltaHidden = hiddenNodes.get(hiddenIndex).getSum() > 0 ? hiddenDeltaSums[hiddenIndex] : 0.0;
          for (int inputIndex = 0; inputIndex < inputNodes.size(); inputIndex++)
          {
            changeInputToHidden[inputIndex][hiddenIndex] = learningRate * inputNodes.get(inputIndex).getOutput() * deltaHidden;
          }
        }

        // Update weights.
        for (int outputIndex = 0; outputIndex < outputNodes.size(); outputIndex++)
        {
          ArrayList<NodeWeightPair> outputParents = outputNodes.get(outputIndex).parents;
          for (int hiddenIndex = 0; hiddenIndex < hiddenNodes.size(); hiddenIndex++)
          {
            outputParents.get(hiddenIndex).weight += changeHiddenToOutput[hiddenIndex][outputIndex];
          }
        }

        for (int hiddenIndex = 0; hiddenIndex < hiddenNodes.size() - 1; hiddenIndex++)
        {
          ArrayList<NodeWeightPair> hiddenParents = hiddenNodes.get(hiddenIndex).parents;
          for (int inputIndex = 0; inputIndex< inputNodes.size(); inputIndex++)
          {
            hiddenParents.get(inputIndex).weight += changeInputToHidden[inputIndex][hiddenIndex];
          }
        }
      }
    }
	}
}
