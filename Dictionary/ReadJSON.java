import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Iterator;

public class ReadJSON
{	
	
	public static void main( String[] args )
	{
		String infile = "dictionary.json";
		JsonReader jsonReader;
		JsonObject jobj = null;
		HybridTST<String> trie = new HybridTST<String>();
		try
		{
			jsonReader = Json.createReader( new FileReader(infile) );
			// assumes the top level JSON entity is an "Object", i.e. a dictionary
			jobj = jsonReader.readObject();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Could not find the file to read: ");
			e.printStackTrace();	
		}
		catch(JsonParsingException e)
		{
			System.out.println("There is a problem with the JSON syntax; could not parse: ");
			e.printStackTrace();
		}
		catch(JsonException e)
		{
			System.out.println("Could not create a JSON object: ");
			e.printStackTrace();
		}
		catch(IllegalStateException e)
		{
			System.out.println("JSON input was already read or the object was closed: ");
			e.printStackTrace();
		}
		if( jobj == null )
			return;
				
        Iterator< Map.Entry<String,JsonValue> > it = jobj.entrySet().iterator();
        while( it.hasNext())
        {
            Map.Entry<String,JsonValue> me = it.next();
			String word = me.getKey();
			String definition = me.getValue().toString();
			trie.put(word,definition);
		}	
	}
}
