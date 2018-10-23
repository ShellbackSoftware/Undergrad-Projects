/**
 * This exception indicates that when searching in the map data or
 * the graph, that the particular edge sought was not found.
 */
@SuppressWarnings("serial")
public class EdgeNotFoundException extends Exception
{
	public EdgeNotFoundException()
	{
		super();
	}

	public EdgeNotFoundException( String message )
	{
		super(message);
	}
}