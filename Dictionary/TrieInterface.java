
public interface TrieInterface<E>
{
    /**
     * Returns the value associated with the given key.
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     *     and <tt>null</tt> if the key is not in the symbol table
     * @throws InvalidKeyException if <tt>key</tt> is <tt>null</tt> or if the key uses
     * 			an invalid character set
     */
	public E get(String key);

    /**
     * Does this symbol table contain the given key?
     * @param key the key
     * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
     *     <tt>false</tt> otherwise
     * @throws InvalidKeyException if <tt>key</tt> is <tt>null</tt> or if the key uses
     * 			an invalid character set
     */
	public boolean contains(String key);

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws InvalidKeyException if <tt>key</tt> is <tt>null</tt> or if the key uses
     * 			an invalid character set
     */
	public void put(String key, E val);

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
	public int size();

    /**
     * Is this symbol table empty?
     * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise
     */
	public boolean isEmpty();

    /**
     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
     * @return all keys in the symbol table as an <tt>Iterable</tt>
     */
	public Iterable<String> keys();

    /**
     * Returns all of the keys in the set that start with <tt>prefix</tt>.
     * @param prefix the prefix
     * @return all of the keys in the set that start with <tt>prefix</tt>,
     *     as an iterable
     * @throws NullPointerException if <tt>prefix</tt> is <tt>null</tt>
     * @throws InvalidKeyException if the prefix uses
     * 			an invalid character set
     */
	public Iterable<String> keysWithPrefix(String prefix);

    /**
     * Returns all of the keys in the symbol table that match <tt>pattern</tt>,
     * where . symbol is treated as a wildcard character.
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match <tt>pattern</tt>,
     *     as an iterable, where . is treated as a wildcard character.
     * @throws NullPointerException if <tt>pattern</tt> is <tt>null</tt>
     * @throws InvalidKeyException if the pattern uses
     * 			an invalid character set
     */
	public Iterable<String> keysThatMatch(String pattern);

    /**
     * Returns the string in the symbol table that is the longest prefix of <tt>query</tt>,
     * or <tt>null</tt>, if no such string.
     * @param query the query string
     * @return the string in the symbol table that is the longest prefix of <tt>query</tt>,
     *     or <tt>null</tt> if no such string
     * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
     * @throws InvalidKeyException if the query uses
     * 			an invalid character set
     */
	public String longestPrefixOf(String query);
	
	/**
	 * Get the current height of the tree used to represent this Trie.  This is a 
	 * diagnostic method used for this lab so you can see how tall your
	 * tree is and therefore what the maximum number of steps are that must be
	 * followed in a search.
	 * @return the current height of the tree
	 */
	public int getTreeHeight();
	
	/**
	 * Compute the average depth of all nodes in the Trie.  The depth for a particular
	 * node is the number of edges that are traversed when starting from any index of the
	 * multi-way root node.  Each root node index is at depth 0.
	 * 
	 * This value, together with the overall height of the tree will help to quantify
	 * real world "average" efficiencies for accessing items in this trie.
	 * @return the average depth for all nodes in the tree
	 */
	public double getAverageNodeDepth();

}
