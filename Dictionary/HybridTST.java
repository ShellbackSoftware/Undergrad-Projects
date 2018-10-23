import java.util.LinkedList;
import java.util.Queue;

/**
 * Hybrid TST for CS 311 Lab 4
 */
public class HybridTST<E> implements TrieInterface<E> {
	private final static int R = 256;

	private int size; // size
	private int treeHeight; // Height of tree
	private double totalDepth; // Depth of tree
	@SuppressWarnings("unchecked")
	private Node[] rootArr = (HybridTST<E>.Node[]) new HybridTST.Node[R];

	private class Node {
		private char c; // character
		private Node left, mid, right; // left, middle, and right subtries
		private E val; // value associated with string
	}

	/**
	 * Initializes an empty TST.
	 */
	public HybridTST() {

	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(String key) {
		if (key == null)
			throw new InvalidKeyException();
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (c > R || c < 0)
				throw new InvalidKeyException();
		}
		return get(key) != null;
	}

	@Override
	public E get(String key) {
		if (key == null)
			throw new InvalidKeyException();
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (c > R || c < 0)
				throw new InvalidKeyException();
		}
		Node x = get(rootArr[(int) key.charAt(0)], key, 0);
		if (x == null)
			return null;
		return x.val;
	}

	// return subtrie corresponding to given key
	private Node get(Node x, String key, int d) {
		if (key == null)
			throw new NullPointerException();
		if (key.length() == 0)
			throw new IllegalArgumentException("key must have length >= 1");
		if (x == null)
			return null;
		char c = key.charAt(d);
		if (c < x.c)
			return get(x.left, key, d);
		else if (c > x.c)
			return get(x.right, key, d);
		else if (d < key.length() - 1)
			return get(x.mid, key, d + 1);
		else
			return x;
	}

	@Override
	public void put(String key, E val) {
		if (key == null || val == null) {
			throw new InvalidKeyException();
		}
		if (!contains(key)) {
			rootArr[key.charAt(0)] = put(rootArr[key.charAt(0)], key, val, 0, 0);
			size++;
		}
	}

	private Node put(Node x, String key, E val, int depth, int ht) {
		char c = key.charAt(depth);
		if (c > R || c < 0)
			throw new InvalidKeyException();
		if (x == null) {
			x = new Node();
			x.c = c;
		}
		if (c < x.c)
			x.left = put(x.left, key, val, depth, ht + 1);
		else if (c > x.c)
			x.right = put(x.right, key, val, depth, ht + 1);
		else if (depth < key.length() - 1)
			x.mid = put(x.mid, key, val, depth + 1, ht + 1);
		else {
			x.val = val;
			if (ht >= treeHeight) {
				treeHeight = ht + 1;
			}
			totalDepth = totalDepth + ht;
		}
		return x;
	}

	@Override
	public String longestPrefixOf(String query) {
		if (query == null)
			throw new NullPointerException();
		for (int i = 0; i < query.length(); i++) {
			char c = query.charAt(i);
			if (c > R || c < 0)
				throw new InvalidKeyException();
		}
		int length = 0;
		Node x = rootArr[query.charAt(0)];
		int i = 0;
		while (x != null && i < query.length()) {
			char c = query.charAt(i);
			if (c < x.c)
				x = x.left;
			else if (c > x.c)
				x = x.right;
			else {
				i++;
				if (x.val != null)
					length = i;
				x = x.mid;
			}
		}
		if ((query.substring(0, length)).isEmpty())
			return null;
		return query.substring(0, length);
	}

	@Override
	public Iterable<String> keys() {
		Queue<String> queue = new LinkedList<String>();
		for (int i = 0; i < rootArr.length; i++) {
			collect(rootArr[i], new StringBuilder(), queue);
		}
		return queue;
	}

	@Override
	public Iterable<String> keysWithPrefix(String prefix) {
		if (prefix == null)
			throw new NullPointerException();
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if (c > R || c < 0)
				throw new InvalidKeyException();
		}
		Queue<String> queue = new LinkedList<String>();
		Node x = get(rootArr[prefix.charAt(0)], prefix, 0);
		if (x == null)
			return queue;
		if (x.val != null)
			queue.add(prefix);
		collect(x.mid, new StringBuilder(prefix), queue);
		return queue;
	}

	// all keys in subtrie rooted at x with given prefix
	private void collect(Node x, StringBuilder prefix, Queue<String> queue) {
		if (x == null)
			return;
		collect(x.left, prefix, queue);
		if (x.val != null)
			queue.add(prefix.toString() + x.c);
		collect(x.mid, prefix.append(x.c), queue);
		prefix.deleteCharAt(prefix.length() - 1);
		collect(x.right, prefix, queue);
	}

	@Override
	public Iterable<String> keysThatMatch(String pattern) {
		if (pattern == null)
			throw new NullPointerException();
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (c > R || c < 0)
				throw new InvalidKeyException();
		}
		Queue<String> queue = new LinkedList<String>();
		if (pattern.charAt(0) == '.') {
			for (int i = 0; i < rootArr.length; i++) {
				collect(rootArr[i], new StringBuilder(), 0, pattern, queue);
			}
		}
		collect(rootArr[pattern.charAt(0)], new StringBuilder(), 0, pattern, queue);
		return queue;
	}

	private void collect(Node x, StringBuilder prefix, int i, String pattern,
			Queue<String> queue) {
		if (x == null)
			return;
		char c = pattern.charAt(i);
		if (c == '.' || c < x.c)
			collect(x.left, prefix, i, pattern, queue);
		if (c == '.' || c == x.c) {
			if (i == pattern.length() - 1 && x.val != null)
				queue.add(prefix.toString() + x.c);
			if (i < pattern.length() - 1) {
				collect(x.mid, prefix.append(x.c), i + 1, pattern, queue);
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
		if (c == '.' || c > x.c)
			collect(x.right, prefix, i, pattern, queue);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int getTreeHeight() {
		return treeHeight;
	}

	@Override
	public double getAverageNodeDepth() {
		return totalDepth/size;
	}
}
