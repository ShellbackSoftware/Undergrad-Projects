import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static int totalNodes;
	private static int numberEdges;
	private static int numberVertices;
	private static double[] bounds = new double[4];
	private static LinkedHashMap<Long, Node> nodes;
	private static List<Node> nodeList = new LinkedList<Node>();
	private static List<Long> nodeIDs = new LinkedList<Long>();
	private static EdgeWeightedDigraph graph;
	private static LinkedHashMap<Long, Node> edgeNodes;
	private static LinkedList<LinkedList<Long>> group;

	/**
	 * Entry point method for the application.
	 * 
	 * @param args
	 *            Command line arguments. args[0] should be the path to the map
	 *            file to read
	 */
	public static void main(String[] args) {
		// Filename of the map/graph data should be the first argument
		if (args == null || args.length < 1) {
			System.out
					.println("Please pass the name of the file to read like this:\n\tjava Main fileToRead.txt");
		}
		String filename = args[0];
		try {
			Scanner scan = new Scanner(new FileInputStream(filename));
			scan.nextLine();
			for (int i = 0; i < 4; i++) {
				bounds[i] = scan.nextDouble();
			}
			scan.nextLine();
			scan.nextLine();
			totalNodes = scan.nextInt();
			numberVertices = scan.nextInt();
			numberEdges = scan.nextInt();
			nodes = new LinkedHashMap<Long, Node>();
			graph = new EdgeWeightedDigraph(totalNodes);
			for (int i = 0; i < totalNodes; i++) {
				long nodeID = scan.nextLong();
				double lat = scan.nextDouble();
				double lon = scan.nextDouble();
				Node currNode = new Node(nodeID, lat, lon);
				nodeList.add(currNode);
				nodeIDs.add(nodeID);
				nodes.put(nodeID, currNode);
				scan.nextLine();
			}
			edgeNodes = new LinkedHashMap<Long, Node>();
			group = new LinkedList<LinkedList<Long>>();
			for (int i = 0; i < numberEdges; i++) {
				if (scan.hasNextLine()) {
					long startID = scan.nextLong();
					long endID = scan.nextLong();
					double weight = scan.nextDouble();
					int start = nodeIDs.indexOf(startID);
					int end = nodeIDs.indexOf(endID);
					DirectedEdge edge = new DirectedEdge(start, end, weight);
					LinkedList<Long> subs = new LinkedList<Long>();
					graph.addEdge(edge);
					while (scan.hasNextLong()) {
						if (scan.hasNextLong()) {
							long currID = scan.nextLong();
							subs.add(currID);
							if (!scan.hasNextLong()) {
								group.add(i, subs);
							}
						}
					}
					scan.nextLine();
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the bounding box for the map in latitude/longitude format
	 * 
	 * @return The bounding box in this format: {min_x, max_x, min_y, max_y}
	 *         where x is longitude and y is latitude
	 */
	public static double[] getMapBounds() {
		return bounds;
	}

	/**
	 * Get the latitude for the node with the given node ID
	 * 
	 * @param nodeID
	 *            The id of the node to search for
	 * @return The latitude in decimal degrees, or if the nodeID is not a valid
	 *         node, returns NaN
	 */
	public static double getLatitude(long nodeID) {
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getNodeID() == nodeID) {
				return nodeList.get(i).getLatitude();
			}
		}
		return Double.NaN;
	}

	/**
	 * Get the longitude for the node with the given node ID
	 * 
	 * @param nodeID
	 *            The id of the node to search for
	 * @return The longitude in decimal degrees, or if the nodeID is not a valid
	 *         node, returns NaN
	 */
	public static double getLongitude(long nodeID) {
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getNodeID() == nodeID) {
				return nodeList.get(i).getLongitude();
			}
		}
		return Double.NaN;
	}

	/**
	 * Given a location, search for the node in the graph that is nearest in
	 * absolute distance. Uses the distance on a curved sphere of radius given
	 * by the WGS84 ellipsoid, see:
	 * http://en.wikipedia.org/wiki/Great-circle_distance
	 * 
	 * @param latitude
	 *            The latitude of the starting point
	 * @param longitude
	 *            The longitude of the starting point
	 * @return The nodeID of the node that is nearest to the starting point. In
	 *         the unlikely event of a tie returns any of the tying nodes
	 *         arbitrarily
	 */
	public static long getNearestNode(double latitude, double longitude) {
		double min = totalNodes;
		long targetNode = 0;
		for(int i = 0; i < nodeList.size(); i++){
			double lat1 = latitude;
			double lon1 = longitude;
			double lat2 = nodeList.get(i).getLatitude();
			double lon2 = nodeList.get(i).getLongitude();
			double radius = 6371;
			double deltaLat = Math.toRadians(lat2 - lat1);
			double deltaLon = Math.toRadians(lon2 - lon1);
			lat1 = Math.toRadians(lat1);
			lat2 = Math.toRadians(lat2);
			double insSqrt = Math.pow(Math.sin(deltaLat / 2), 2)
					+ Math.cos(lat1) * Math.cos(lat2)
					* Math.pow(Math.sin(deltaLon / 2), 2);
			double result = 2 * Math.asin(Math.sqrt(insSqrt));
			double total = radius * result;
			if (total < min) {
				targetNode = nodeList.get(i).getNodeID();
				min = total;
			}
		}
		return targetNode;
	}

	/**
	 * Get the length of the indicated edge. This is the total length of the
	 * edge, which includes all sub nodes.
	 * 
	 * @param startNodeID
	 *            The starting node ID
	 * @param endNodeID
	 *            The ending node ID
	 * @return The length of the edge in meters
	 * @throws EdgeNotFoundException
	 *             Thrown when the startNodeID and endNodeID do not represent a
	 *             legal edge in the graph. This is a custom exception class
	 *             written for this lab
	 */
	public static double getEdgeLength(long startNodeID, long endNodeID)
			throws EdgeNotFoundException {
		if (!edgeNodes.containsKey(startNodeID)
				|| !edgeNodes.containsKey(endNodeID)) {
			throw new EdgeNotFoundException();
		}
		if (edgeNodes.get(startNodeID).getEndNode() != endNodeID) {
			throw new EdgeNotFoundException();
		}

		return edgeNodes.get(startNodeID).getEdgeWeight();
	}

	/**
	 * Get the subnodes found within the indicated edge.
	 * 
	 * @param startNodeID
	 *            The starting node ID
	 * @param endNodeID
	 *            The ending node ID
	 * @return A list of the nodeID's for the nodes contained within this edge.
	 *         The list includes the starting and ending node ID's. The order of
	 *         the nodes is important and should be the same as the order found
	 *         in the original file.
	 * @throws EdgeNotFoundException
	 *             Thrown when the startNodeID and endNodeID do not represent a
	 *             legal edge in the graph. This is a custom exception class
	 *             written for this lab
	 */
	public static List<Long> getEdgeSubNodes(long startNodeID, long endNodeID)
			throws EdgeNotFoundException {
		if (!nodes.containsKey(startNodeID) || !nodes.containsKey(endNodeID)) {
			throw new EdgeNotFoundException();
		}
		return null;
	}

	/**
	 * Get the total number of vertices in the graph.
	 * 
	 * @return |V| This (does <b>not</b> include the vertices contained "within"
	 *         each edge as subnodes)
	 */
	public static int getNumberOfVertices() {
		return numberVertices;
	}

	/**
	 * Get the total number of edges in the graph.
	 * 
	 * @return |E|
	 */
	public static int getNumberOfEdges() {
		return numberEdges;
	}

	/**
	 * Find a path through the graph from the given starting node ID and ending
	 * at the given end node ID. This can be any valid path -- there are no
	 * constraints on which path is found.
	 * 
	 * @param startNodeID
	 *            The node ID to start the path from
	 * @param endNodeID
	 *            The node ID to end the path to
	 * @return A list of node ID's that form the path from the starting node to
	 *         the end node. The list should include both the starting and
	 *         ending nodes, <b>but no sub-nodes</b>. Returns an empty list if
	 *         no such path can be found.
	 */
	public static List<Long> getPath(long startNodeID, long endNodeID) {
		ArrayList<Long> notFound = new ArrayList<Long>();
		if (!nodes.containsKey(startNodeID) || !nodes.containsKey(endNodeID)) {
			return notFound;
		}
		DijkstraSP dij = new DijkstraSP(graph, nodeIDs.indexOf(startNodeID));	
		Iterable<DirectedEdge> iter = dij.pathTo(nodeIDs.indexOf(endNodeID));
		List<Long> result = new LinkedList<Long>();
			for(DirectedEdge e: iter){
				result.add(nodeIDs.get(e.from()));
				result.add(nodeIDs.get(e.to()));
			}
		
		return result;
	}
	


	/**
	 * Find a path through the graph from the given starting node ID and ending
	 * at the given end node ID. This can be any valid path -- there are no
	 * constraints on which path is found.
	 * 
	 * @param startNodeID
	 *            The node ID to start the path from
	 * @param endNodeID
	 *            The node ID to end the path to
	 * @return A list of node ID's that form the path from the starting node to
	 *         the end node. The list should include both the starting and
	 *         ending nodes, <b>and does include all sub-nodes in the correct
	 *         order</b>. Returns an empty list if no such path can be found.
	 */
	public static List<Long> getDetailedPath(long startNodeID, long endNodeID) {
		ArrayList<Long> notFound = new ArrayList<Long>();
		if (!nodes.containsKey(startNodeID) || !nodes.containsKey(endNodeID)) {
			return notFound;
		}
		DijkstraSP dij = new DijkstraSP(graph, nodeIDs.indexOf(startNodeID));	
		Iterable<DirectedEdge> iter = dij.pathTo(nodeIDs.indexOf(endNodeID));
		System.out.println(iter);
		List<Long> result = new LinkedList<Long>();
			for(DirectedEdge e: iter){
				result.add(nodeIDs.get(e.from()));
				result.add(nodeIDs.get(e.to()));
			}
			System.out.println(result);
		
		return result;
	}

	/**
	 * Write a KML file for the given path
	 * 
	 * @param filename
	 *            File name for the file to write
	 * @param path
	 *            List of node ID's that hold the path
	 */
	public static void writeKMLFile(String filename, List<Long> path) {
		java.io.PrintWriter fout = null;
		try {
			fout = new java.io.PrintWriter(new java.io.BufferedWriter(
					new java.io.OutputStreamWriter( // need to get to one that
							// let's us set the Charset
							new java.io.FileOutputStream(filename), "UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		} // don't do any handling
			// quick and dirty way to make a valid KML file
		fout.print(kmlHeader);
		for (Long nid : path) {
			fout.printf("%f,%f\n", getLongitude(nid.longValue()),
					getLatitude(nid.longValue()));
		}
		fout.print(kmlFooter);
		fout.close();
	}

	// from KML reference example
	static String kmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">" + "  <Document>"
			+ "    <name>CS 311 Lab 2 Path</name>"
			+ "    <description>Path between two nodes.</description>"
			+ "    <Style id=\"yellowLineGreenPoly\">" + "      <LineStyle>"
			+ "        <color>7f00ffff</color>" + "        <width>4</width>"
			+ "      </LineStyle>" + "      <PolyStyle>"
			+ "        <color>7f00ff00</color>" + "      </PolyStyle>"
			+ "    </Style>" + "    <Placemark>"
			+ "      <name>Follows surface</name>"
			+ "      <description>Transparent green</description>"
			+ "      <styleUrl>#yellowLineGreenPoly</styleUrl>"
			+ "      <LineString>" + "        <extrude>0</extrude>"
			+ "        <tessellate>0</tessellate>"
			+ "        <altitudeMode>clampToGround</altitudeMode>"
			+ "        <coordinates> ";

	static String kmlFooter = "</coordinates>" + "      </LineString>"
			+ "    </Placemark>" + "  </Document>" + "</kml>";

	/**
	 * Find a shortest path from the given start node ID to the end node ID. A
	 * shortest path is one that has the shortest possible path length, which is
	 * the sum of the edge weights along the path. This is the sum of the edge
	 * weights given in the file and not one calculated from GPS coordinates.
	 * 
	 * The shortest path should be calculated with Dijkstra's algorithm.
	 * 
	 * @param startNodeID
	 *            the node to start the search from
	 * @param endNodeID
	 *            the node to end the search at
	 * @return A list of node ID's that identify the shortest path through the
	 *         graph. This list of node ID's is for "major" nodes in the graph
	 *         only, not subnodes. Returns an empty list if no path exists.
	 */
	public static List<Long> getShortestPath(long startNodeID, long endNodeID) {
		ArrayList<Long> notFound = new ArrayList<Long>();
		if (!nodes.containsKey(startNodeID) || !nodes.containsKey(endNodeID)) {
			return notFound;
		}
		DijkstraSP dij = new DijkstraSP(graph, nodeIDs.indexOf(startNodeID));	
		Iterable<DirectedEdge> iter = dij.pathTo(nodeIDs.indexOf(endNodeID));
		List<Long> result = new LinkedList<Long>();
			for(DirectedEdge e: iter){
				result.add(nodeIDs.get(e.from()));
				result.add(nodeIDs.get(e.to()));
			}		
		return result;
	}

	/**
	 * Find a shortest path from the given start node ID to the end node ID. A
	 * shortest path is one that has the shortest possible path length, which is
	 * the sum of the edge weights along the path. This is the sum of the edge
	 * weights given in the file and not one calculated from GPS coordinates.
	 * 
	 * The shortest path should be calculated with Dijkstra's algorithm.
	 * 
	 * @param startNodeID
	 *            the node to start the search from
	 * @param endNodeID
	 *            the node to end the search at
	 * @return A list of node ID's that identify the shortest path through the
	 *         graph. This list of node ID's is for all nodes including
	 *         subnodes. Returns an empty list if no path exists.
	 */
	public static List<Long> getDetailedShortestPath(long startNodeID,
			long endNodeID) {
		DijkstraSP dij = new DijkstraSP(graph, nodeIDs.indexOf(startNodeID));	
		Iterable<DirectedEdge> iter = dij.pathTo(nodeIDs.indexOf(endNodeID));
		List<Long> result = new LinkedList<Long>();
			for(DirectedEdge e: iter){
				result.add(nodeIDs.get(e.from()));
				result.add(nodeIDs.get(e.to()));
			}
			System.out.println(result);
		return result;
//		return null;
	}

	/**
	 * Determine the longest of all shortest paths between every pair of nodes
	 * in the graph (not subnodes). This is often called the "diameter" of the
	 * graph (see e.g. <a
	 * href="http://en.wikipedia.org/wiki/Distance_(graph_theory)">Distance
	 * (graph theory)</a>)
	 * 
	 * Let's say that the distance between two vertices in a graph is defined to
	 * be the length of the shortest path between them. What we're after is the
	 * greatest distance between any two vertices, i.e. the longest of all
	 * distances in the graph. Usually this distance is the count of the edges
	 * or vertices along this path, however we want the distance to be in meters
	 * which comes from the edge weights. Returns an empty list if no path
	 * exists.
	 * 
	 * @return A list of node ID's that identify the path that corresponds to
	 *         the diameter of the graph.
	 */
	public static List<Long> getLongestShortestPath() {
		return null;
	}

	/**
	 * Calculate the length of a given path. This calculates the path length
	 * based on the weights of the edges (not GPS coordinates).
	 * 
	 * @param path
	 *            A list of node ID's forming a path in the graph
	 * @return the length of the path in meters
	 */
	public static double pathLength(List<Long> path) {

		return Double.NaN;
	}

}// end class Main
