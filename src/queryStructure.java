import java.util.ArrayList;
import java.util.TreeMap;

public class queryStructure
{
	String word;
	Boolean Title = false;
	Boolean Category = false;
	Boolean Infobox = false;
	Boolean Links = false;
	Boolean Text = false;
	//ArrayList<DocCount> objlist = new ArrayList<DocCount>();
	TreeMap<Integer, DocCount> doclist = new TreeMap<Integer, DocCount>();
}
