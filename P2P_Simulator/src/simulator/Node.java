package simulator;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Node {

	private static final int MAX_LOAD = 100;
	protected UUID nodeId;
	protected int load;
	protected ArrayList<Node> neighbors;
	protected ArrayList<File> files;
	protected ArrayList<Transfer> sendTransfers;
	protected ArrayList<Transfer> receiveTransfers;
	public Query currRequest;

	public Node(UUID currId) {
		nodeId = currId;
		load = 0;
		neighbors = new ArrayList<Node>();
		files = new ArrayList<File>();
		sendTransfers = new ArrayList<Transfer>();
		receiveTransfers = new ArrayList<Transfer>();

	}

	public ArrayList<File> createFileList(int numFiles) {
		for (int i = 0; i < numFiles; i++) {
			UUID newID = UUID.randomUUID();
			int fileSize = (int) Math.floor(Math.random() * 100 + 1);	// Max file size for transfer = 1GB, min = 1MB
			File newFile = new File(newID, fileSize);
			files.add(newFile);
			//			System.out.println("\t"+newFile.id);
		}

		return files;
	}

	public File getRandomFile() {
		Random generator = new Random();
		return files.get(generator.nextInt(files.size()));
	}

	public boolean transferFile(Node targetNode, File targetFile) {
		//		File requestedFile = getRandomFile();
		//if (load + requestedFile.size < 100) {
		//load += requestedFile.size;
		//	neighbor.load += requestedFile.size;
		//		if (load < MAX_LOAD && query.nodesVisited.get(query.nodesVisited.size() - 1).load < MAX_LOAD) {
		//			query.sender = this;
		//			query.sender.files.add(query.requestedFile);
		//		}

		targetNode.files.add(targetFile);
		return true;
	}

	public ArrayList<File> getFileList() {
		return files;
	}

	public void setQuery(File currFile){
		currRequest = new Query(currFile,this);
	}


	public void requestFile(Query currQuery){

		Node preSender = currQuery.sender;
		currQuery.update(this);
		currQuery.hopCount++;

		for(int i =0; i < neighbors.size(); i++){
			if (neighbors.get(i) != preSender)
				neighbors.get(i).receiveRequest(currQuery);
		}
	}

	public void receiveRequest(Query currQuery){
		if (currQuery.hopCount <= Query.ttl) {
			currQuery.nodesVisited.add(this);
			if (!currQuery.inProgress){
				if (files.contains(currQuery.requestedFile) && transferCheck(currQuery)) {
					//transferFile(currQuery.requester, currQuery.requestedFile);
					Transfer newTransfer = new Transfer(currQuery, 30);
					sendTransfers.add(newTransfer);
					currQuery.requester.receiveTransfers.add(newTransfer);	
					currQuery.inProgress = true;
				}
				else {
					requestFile(currQuery);
				}
			}
		}
	}

	public boolean transferCheck (Query myQuery) {
		UUID fileID = myQuery.requestedFile.id;
		Node requestor = myQuery.requester;
		//Node sender = myQuery.sender;

		for (Transfer tran : sendTransfers) {
			if (tran.transferedFile.id.equals(fileID) && 
					tran.receiver.equals(requestor)) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<Transfer> processTransfers(){
		ArrayList<Transfer> completed = new ArrayList<Transfer>();
		for (int j = 0; j < sendTransfers.size(); j++){
			Transfer currTransfer = sendTransfers.get(j);
			if (currTransfer.cycleTransfer()) {
				System.out.println("removing: " + currTransfer.transferedFile.id);
				sendTransfers.remove(currTransfer);
				completed.add(currTransfer);
				currTransfer.receiver.receiveTransfers.remove(currTransfer);				
			}
		}
		return completed;
	}


	//	public Query getQuery(){
	//		return currRequest;
	//	}
	//	public void clearQuery(){
	//		currRequest = new Query();
	//	}

	public void addNeighbor(Node currNode){
		neighbors.add(currNode);
	}
}
