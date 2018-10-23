/**
 * Node class to deal with the geographical points in the Main class.
 * 
 */

public class Node {
	private double latitude;
	private double longitude;
	private long nodeID;
	private double weight;
	private long endNodeID;
	private long startNodeID;
	
	/**
	 * Constructor for the Node class to instantiate a new Node with the desired
	 * ID.
	 * 
	 * @param nodeID
	 *            - Desired ID of the node.
	 */
	public Node(long nodeID) {
		this.nodeID = nodeID;
	}
	
	/**
	 * Constructor for Node class to create a new Node with the specified edges
	 * 
	 * @param startNodeID - Starting node ID 
	 * @param endNodeID - Ending node ID
	 * @param weight - Weight of the edge between the two nodes
	 */
	public Node(long startNodeID, long endNodeID, double weight){
		this.startNodeID = startNodeID;
		this.endNodeID = endNodeID;
		this.weight = weight;
	}
	
	/**
	 * Constructor for the Node class to instantiate a new Node with the desired
	 * Latitude and Longitude.
	 * 
	 * @param latitude
	 *            - Desired latitude.
	 * @param longitude
	 *            - Desired longitude.
	 */
	public Node(long nodeID, double latitude, double longitude) {
		this.nodeID = nodeID;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Sets the latitude for the current node.
	 * 
	 * @param lat
	 *            - Desired latitude
	 */
	public void setLat(double lat) {
		this.latitude = lat;
	}

	/**
	 * Sets the longitude for the current node.
	 * 
	 * @param lon
	 *            - Desired longitude.
	 */
	public void setLon(double lon) {
		this.longitude = lon;
	}

	/**
	 * Returns the ID of the current node.
	 * 
	 * @return - Returns the node ID.
	 */
	public long getNodeID() {
		return nodeID;
	}

	/**
	 * Returns the longitude of the current node.
	 * 
	 * @return - Longitude of the current node.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude of the current node.
	 * 
	 * @return - Latitude of the current node.
	 */
	public double getLatitude() {
		return latitude;
	}
	
	public void setEdgeWeight(double weight){
		this.weight = weight;
	}
	
	public double getEdgeWeight(){
		return weight;
	}
	
	public long getStartNode(){
		return startNodeID;
	}
	
	public long getEndNode(){
		return endNodeID;
	} 
}
