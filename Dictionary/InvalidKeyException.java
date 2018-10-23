/**
 * An unchecked exception to use when the key parameter
 * is invalid, i.e. null or uses an invalid character
 */
@SuppressWarnings("serial")
public class InvalidKeyException extends RuntimeException
{
	public InvalidKeyException()
	{
		super();
	}

	public InvalidKeyException( String message )
	{
		super(message);
	}
}
