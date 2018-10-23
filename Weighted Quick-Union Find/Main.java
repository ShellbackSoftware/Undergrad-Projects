import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Union-find lab CS 311 Lab 1 Spring 2015
 */
public class Main {

	private static WeightedQuickUnionUF union;
	private static int max;

	/**
	 * Default constructor for the class.
	 */
	public Main() {
	}

	/**
	 * Constructor for class. Parameter allows how large the
	 * WeightedQuickUnionUF object is.
	 * 
	 * @param N
	 *            - Desired size of WeightedQuickUnionUF.
	 */
	public Main(int N) {
		union = new WeightedQuickUnionUF(N);
		max = N;
	}

	/**
	 * Find the number of distinct individuals in the relationship data, i.e.
	 * how many users are there?
	 */
	public static int numberOfIndividuals() {
		return max;
	}

	/**
	 * Get the number of distinctly connected groups of individuals.
	 *
	 */
	public static int numberOfGroups() {
		return union.count();
	}

	/**
	 * Is the user whose ID is a connected to ID b?
	 *
	 * @return true if a is connected to b, otherwise false
	 */
	public static boolean isConnected(int a, int b) {
		return union.connected(a, b);
	}

	/**
	 * Given the indicated user ID, return a list of all the other user ID�s
	 * that are connected to this individual.
	 *
	 * @param id
	 *            The id of the user to search for
	 * @return A list of other user ID�s, sorted low to high that are connected
	 *         to id
	 */
	public static List<Integer> getConnectedGroup(int id) {
		ArrayList<Integer> users = new ArrayList<Integer>();
		for (int i = 0; i < max; i++) {
			int a = union.find(id);
			if (isConnected(a, i)) {
				users.add(i);
			}
		}
		Collections.sort(users);
		return users;
	}

	/**
	 * Find out which individuals are in the largest connected group, i.e. the
	 * largest friend network.
	 *
	 * @return A list of user ID�s, sorted low to high that are in the largest
	 *         group
	 */
	public static List<Integer> getLargestGroup() {
		List<Integer> users = new ArrayList<Integer>();
		int maxGroup = 0;
		for (int i = 0; i < numberOfGroups(); i++) {
			if ((getConnectedGroup(i).size() > maxGroup) && i <= numberOfGroups()) {
				maxGroup = getConnectedGroup(i).size();
			} 
			users = getConnectedGroup(maxGroup);
		}
		Collections.sort(users);
		return users;
	}

	/*
	 * Main method to allow access to program from the command line.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Input a filename [main method]");
		}
		try {
			Scanner scan = new Scanner(new FileInputStream(args[0]));
			int max = scan.nextInt();
			while (scan.hasNextInt()) {
				int curr = scan.nextInt();
				if (curr > max) {
					max = curr;
				}
			}
			max++;
			scan.close();
			@SuppressWarnings("unused")
			Main main = new Main(max);
		} catch (FileNotFoundException e) {
			System.out.println("File not found[main method]");
		}
		try {
			Scanner r = new Scanner(new FileInputStream(args[0]));
			while (r.hasNextInt()) {
				int a = r.nextInt();
				int b = r.nextInt();
				if (!union.connected(a, b)) {
					union.union(a, b);
				}
			}
			r.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found[main method]");
		}
	}
}