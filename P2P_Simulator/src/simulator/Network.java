package simulator;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Network {
	protected int nodeCount;
	protected int neighborCount;
	public ArrayList<Node> nodeList;
	public ArrayList<File> fileList;

	public Network(int nodes, int numNeighbors) {
		Node currNode;
		Node currNeighbor;
		nodeList = new ArrayList<Node>();
		fileList = new ArrayList<File>();
		int size;

		initNodeList(nodes);

		for (int i = 0; i < nodes; i++) {
			size = 0;
			currNode = nodeList.get(i);
			System.out.println("Node id: "+ currNode.nodeId);
			fileList.addAll(currNode.createFileList(5));
			
			initNeighbors(numNeighbors, currNode, size);
		}
	}

	/**
	 * @param neighbors
	 * @param currNode
	 * @param count
	 */
	private void initNeighbors(int neighbors, Node currNode, int count) {
		Node currNeighbor;
		while (count < neighbors) {
			Random generator = new Random();
			currNeighbor = nodeList.get(generator.nextInt(nodeList.size()));
			if (!currNode.neighbors.contains(currNeighbor) && currNode != currNeighbor) {
				currNode.addNeighbor(currNeighbor);
				count++;
			}
		}
	}

	/**
	 * @param nodes
	 */
	private void initNodeList(int nodes) {
		for (int i =0; i < nodes; i++) {
			nodeList.add(new Node(UUID.randomUUID()));	
		}
	}
	
	

}
