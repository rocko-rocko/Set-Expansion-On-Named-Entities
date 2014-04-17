import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.RowFilter.Entry;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class indexer
{
	// MODE
	//public static String PREFIX = "small/";
	public static final String PREFIX = "";
	
	static String stopwords[] = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount", "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as", "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
	static int stoplen;
	static HashSet hashstop = new HashSet();
	static int j;
	static TreeMap<String, ArrayList<wordfrequency>> tm = new TreeMap<String, ArrayList<wordfrequency>>();
	static TreeMap<Integer, String> Ttree = new TreeMap<Integer, String>();
	static boolean file_flag = false;

   public static void main(String argv[])
	//public void file(String file) throws SAXException
   {
	   stoplen = stopwords.length;
	   for(j=0;j<stoplen;j++)			hashstop.add(stopwords[j]);

	   try
	   {
		   SAXParserFactory factory = SAXParserFactory.newInstance();
		   SAXParser saxParser = factory.newSAXParser();
		   DefaultHandler handler = new DefaultHandler()
		   {

			   boolean title_flag = false;
			   boolean text_flag = false;
			   boolean page_flag = false;
			   boolean id_flag = false;
			   boolean immediate_id = false;
			   boolean infobox = false;
			   boolean category = false;
			   boolean internal = false;
			   boolean external = false;
			   boolean status = false;
			   boolean cite = false;
			   int count = 0;
			   
			   String cont = new String();
			   String temp = new String();
			   int docid = 0;
			   String tit = new String();

			   public void startElement(String uri, String localName,String qName,Attributes attributes) throws SAXException
			   {

				   if (qName.equalsIgnoreCase("file"))		   					file_flag = false;
				   else if (qName.equalsIgnoreCase("title"))
				   {
					   															title_flag = true;
					   															immediate_id = true;
				   }
				   else if (qName.equalsIgnoreCase("text"))
				   {
					   															cont = new String();
					   															text_flag = true;
				   }
				   else if (qName.equalsIgnoreCase("page"))						page_flag = true;
				   else if (immediate_id && qName.equalsIgnoreCase("id"))		id_flag = true;
			   }

			   public void endElement(String uri, String localName, String qName) throws SAXException
			   {
				   if (qName.equalsIgnoreCase("file"))				file_flag = true;
				   else if (qName.equalsIgnoreCase("title"))		title_flag = false;
				   else if (qName.equalsIgnoreCase("text"))			text_flag = false;
				   else if (qName.equalsIgnoreCase("page")) 	    page_flag = false;
				   else if (qName.equalsIgnoreCase("id"))
				   {
					   id_flag = false;
					   immediate_id = false;
				   }
			   }

			   public void characters(char ch[], int start, int length) throws SAXException
			   {
				   int i,len;
				   String text = new String(ch, start, length);
				   String temp = new String();
				   if (immediate_id && id_flag)
				   {
					   docid = Integer.parseInt(text);
					   Ttree.put(docid, tit);
					   len = tit.length();
					   for(i=0;i<len;i++)
					   {
						   if((tit.charAt(i) >= 97 && tit.charAt(i) <= 122) )//|| (tit.charAt(i) >= 48 && tit.charAt(i) <= 57))
							   temp = temp + tit.charAt(i);
						   else
						   {
							   stopStemWord(temp,false,false,false,false,true,false);
							   temp = new String();
						   }
					   }
					   stopStemWord(temp,false,false,false,false,true,false);
				   }
				   else if (title_flag)	   tit = text.toLowerCase();
				   else if (text_flag)
				   {
					   cont = text.toLowerCase();
					   analize();
				   }
			   }

			   public void analize()
			   {
				   int i=0;
				   int len = cont.length();

				   for(i=0;i<len;i++)
				   {
					   if(cite)
					   {
						   if(cont.charAt(i) == '}' && i+1 < len && cont.charAt(i+1) == '}' )
						   {
							   cite = false;
							   i++;
						   }
						   continue;
					   }
					   builbWord(i,infobox,category,internal,external,false);
					   if(cont.charAt(i) == '{' && i+5 < len && cont.charAt(i+1) == '{' && cont.charAt(i+2) == 'c' && cont.charAt(i+3) == 'i' && cont.charAt(i+4) == 't' && cont.charAt(i+5) == 'e')
					   {
						   cite = true;
						   i = i+5;
					   }
					   else if(cont.charAt(i) == '*' && i+3 < len && cont.charAt(i+1) == ' ' && cont.charAt(i+2) == '[' && cont.charAt(i+3) == 'h')
					   {
						   external = true;
						   status = true;
						   i=i+2;
					   }
					   else if(cont.charAt(i) == '{' && i+1 < len && cont.charAt(i+1) == '{')
					   {
						   if(cont.charAt(i) == '{' && i+8 < len && cont.charAt(i+1) == '{' && cont.charAt(i+2) == 'i' && cont.charAt(i+3) == 'n' && cont.charAt(i+4) == 'f' && cont.charAt(i+5) == 'o' && cont.charAt(i+6) == 'b' && cont.charAt(i+7) == 'o' && cont.charAt(i+8) == 'x')
						   {
							   infobox = true;
							   i=i+8;
						   }
						   if(infobox)	count++;
					   }
					   else if(cont.charAt(i) == '[' && i+1 < len && cont.charAt(i+1) == '[')
					   {
						   if(cont.charAt(i) == '[' && i+9 < len && cont.charAt(i+1) == '[' && cont.charAt(i+2) == 'c' && cont.charAt(i+3) == 'a' && cont.charAt(i+4) == 't' && cont.charAt(i+5) == 'e' && cont.charAt(i+6) == 'g' && cont.charAt(i+7) == 'o' && cont.charAt(i+8) == 'r' && cont.charAt(i+9) == 'y')
						   {
							   category = true;
							   i=i+9;
						   }
						   else
						   {
							   internal = true;
							   i++;
						   }
					   }
					   if(cont.charAt(i) == ']' && external)	external=false;
					   else if(cont.charAt(i) == '}' && i+1 < len && cont.charAt(i+1) == '}')
					   {
						   if(infobox)
						   {
							   count--;
							   if(count == 0)		infobox = false;
						   }
						   i++;

					   }
					   else if(cont.charAt(i) == ']' && i+1 < len && cont.charAt(i+1) == ']')
					   {
						   i++;
						   if(category)			category = false;
						   else 				internal = false;
					   }
				   }
				   cont = new String();
			   }

			   public void builbWord(int i, boolean infobox, boolean category, boolean internal, boolean external,boolean title)
			   {
				   if(title && ((cont.charAt(i) >= 97 && cont.charAt(i) <= 122) ))//|| (cont.charAt(i) >= 48 && cont.charAt(i) <= 57)))
				   {
					   temp = temp + cont.charAt(i);
				   }
				   else if(/*!external && */((cont.charAt(i) >= 97 && cont.charAt(i) <= 122) /*|| (cont.charAt(i) >= 48 && cont.charAt(i) <= 57)*/))
					   temp = temp + cont.charAt(i);
				  // else if(external && ((cont.charAt(i) >= 97 && cont.charAt(i) <= 122) || (cont.charAt(i) >= 48 && cont.charAt(i) <= 57) || cont.charAt(i) == '-')) 
					//   temp = temp + cont.charAt(i);
				   else
				   {
					   /*if(cont.charAt(i) == '.')
					   {
						   if(temp.matches("[+-]?\\d*(\\.\\d+)?"))
						   {
							   temp = temp + cont.charAt(i);
						   }
					   }
					   else
					   {*/
						   stopStemWord(temp,infobox,category,internal,external,title,status);
						   if(external)	status=false;
					  // }
					   temp = new String();
				   }
			   }

			   public void stopStemWord(String temp1, boolean infobox, boolean category, boolean internal, boolean external,boolean title,boolean status)
			   {
				   int title_count = 0;
				   int infobox_count = 0;
				   int category_count = 0;
				   int internal_count = 0;
				   int external_count = 0;
				   int text_count = 0;
				   int l = temp1.length();
				   if((l > 2 && l < 17 && !(hashstop.contains(temp1))) || (status))
				   {
						   if(title)		   												title_count = 1;
						   if(infobox)		   												infobox_count = 1;
						   if(category)		   												category_count = 1;
						   if(internal)		   												internal_count = 1;
						   if(external)		   												external_count = 1;
						   if(!title && !infobox && !category && !internal && !external)	text_count = 1;

						   Stemmer s = new Stemmer();
						   s.add(temp1.toCharArray(),temp1.length());
						   s.stem();
						   temp1 = s.toString();
						   if(!(hashstop.contains(temp1)))
						   writetotreemap(temp1,title_count,infobox_count,category_count,internal_count,external_count,text_count);
					}
			   }

			   public void writetotreemap(String storeword, int title_count,int infobox_count, int category_count, int internal_count,int external_count, int text_count)
			   {
				   if(tm.get(storeword)==null)
				   {
					   ArrayList<wordfrequency> objlist=new ArrayList<wordfrequency>();
					   wordfrequency w1=new wordfrequency();
					   w1.docid=docid;
					   w1.title=title_count;
					   w1.infobox=infobox_count;
	                   w1.category=category_count;
	                   w1.internal=internal_count;
	                   w1.external=external_count;
	                   w1.text=text_count;
	                   objlist.add(w1);
					   tm.put(storeword, objlist);
				   }
				   else
				   {
					   ArrayList<wordfrequency> objlist=(ArrayList<wordfrequency>) tm.get(storeword);
					   wordfrequency w = objlist.get(objlist.size()-1);
					  // if(objlist.size() < 2500)
					  // {
						   if(w.docid == docid)
						   {
							   	w.title = w.title + title_count;
							   	w.infobox = w.infobox + infobox_count;
			                   	w.category = w.category + category_count;
			                   	w.internal = w.internal + internal_count;
			                   	w.external = w.external + external_count;
			                   	w.text = w.text + text_count;
						    }
						   	else
						    {
		                       	wordfrequency w1=new wordfrequency();
							   	w1.docid=docid;
							   	w1.title=title_count;
							   	w1.infobox=infobox_count;
			                   	w1.category=category_count;
			                   	w1.internal=internal_count;
			                   	w1.external=external_count;
			                   	w1.text=text_count;
						        objlist.add(w1);
						    }
					  // }
					   /*else
					   {
						   tm.remove(storeword);
						   hashstop.add(storeword);
					   }*/
				   }
			   }
		   };
		   //saxParser.parse("C://Users/Vivek/Desktop/asdf/sample1" + "" + "" + ".xml", handler);
		   //saxParser.parse(file/* + "" + "" + ".xml"*/, handler);
		  // String input = "Wiki_Split_Files/";
		   long startTime = System.currentTimeMillis();
		   //String inputParam = argv[0];
		  // String indexFolderParam = argv[1];
		   
		   
		   String input = "C:/"+PREFIX+"Wiki_Split_Files";//inputParam;//inputFile ;//"E:/Wiki_Split_Files";	//*******************************
		   //String finalop = "C:/Users/vivek/Desktop/output";//argv[0];
		   String output =  input+"Indexed";//indexFolderParam+".txt"; //OUTPUTPATH
		   //System.out.println(output);

		/*   //String path=chunks[j].getAbsolutePath();
			   File file = new File(inputParam);
			   FileInputStream inputStream = new FileInputStream(file);
			   Reader reader = new InputStreamReader(inputStream,"UTF-8");
			   InputSource is = new InputSource(reader);
			   is.setEncoding("UTF-8");
			   saxParser.parse(is,handler);
			   makeFile(indexFolderParam);
			   reader.close(); inputStream.close(); */
			   
			   
			   
		   File[] chunks = new File(new String(input.getBytes("UTF-8"), "UTF-8")).listFiles();
		   (new File(output)).mkdirs();
		   int len = chunks.length;
		   int vivek;
		   
		  // long startTime = System.currentTimeMillis();
		   for (j = 0; j < len ; j++)
		   {
			   vivek = j+1;
			   String path=chunks[j].getAbsolutePath();
			   File file = new File(path);
			   FileInputStream inputStream = new FileInputStream(file);
			   Reader reader = new InputStreamReader(inputStream,"UTF-8");
			   InputSource is = new InputSource(reader);
			   is.setEncoding("UTF-8");
			   if(j % 2 == 0 && j > 0)
			   {
				   if(j <= 9)	   makeFile(output + "/store"+"00"+j);
				   else if(j<=99)	makeFile(output + "/store"+"0"+j);
				   else				makeFile(output + "/store"+j);
				   tm = new TreeMap<String, ArrayList<wordfrequency>>();
				   Ttree = new TreeMap<Integer,String>();
			   }
			   saxParser.parse(is,handler);
			   reader.close(); inputStream.close();
			  // if(j == 5) break;
			   System.out.println("File read "+vivek+"/"+len+":"+path);
		   	}
		   long stopTime = System.currentTimeMillis();
	   	makeFile(output + "/store"+j);
		   	long elapsedTime = (stopTime - startTime);
		   	System.out.println("\n\n"+elapsedTime+" Milliseconds");
		   System.out.println(output);
		   
		   java.util.Iterator iterator1 = hashstop.iterator(); 
		   File f=new File("stopwords123");
		   BufferedWriter	 bw=new BufferedWriter(new FileWriter(f));
		    while (iterator1.hasNext())
		    {
		    	bw.write(iterator1.next() + " ");
		    	bw.newLine(); 
		    }
		    bw.close();
		   
		   	merger m = new merger();
		   
		   m.run(output);//,finalop);
		   	
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	}

   public static void makeFile(String output)
   {
	   int count;
	   System.out.println("FILE WRITTEN");
	   //File f=new File("C://Users/Vivek/Desktop/asdf/store1.txt");
	   File f=new File(output);
	   try
	   {
		   BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		   for (java.util.Map.Entry<String, ArrayList<wordfrequency>> entry : tm.entrySet())
		   {
			   ArrayList<wordfrequency>  objlist=entry.getValue();
			   java.util.Iterator<wordfrequency> it=objlist.iterator();
			   bw.write(entry.getKey()+" ");
			   while(it.hasNext())
			   {
                             wordfrequency w=it.next();
                             bw.write(w.docid+" ");
                             count = w.internal + w.external;
                             if(w.title != 0)		bw.write("T"+w.title+" ");
                             if(w.infobox != 0)		bw.write("I"+w.infobox+" ");
                             if(w.category != 0)	bw.write("C"+w.category+" ");
                             if(count != 0)	bw.write("L"+w.internal+" ");
                             //if(w.internal != 0)	bw.write("L"+w.internal+" ");
                             //if(w.external != 0)	bw.write("E"+w.external+" ");
                             if(w.text != 0)		bw.write("D"+w.text+" ");
                             bw.write("#");
                             //bw.write(w.docid+" "+w.title+" "+w.infobox+" "+w.category+/*" "+w.external+*/" "+w.internal+" "+w.text+"*");
			   }
			   bw.newLine();
		   }
		   bw.close();
		   f=new File("Docid-Title");
		   bw=new BufferedWriter(new FileWriter(f,true));
		   	for (java.util.Map.Entry<Integer, String> entry : Ttree.entrySet())
			{
		   		bw.write(entry.getKey()+"#"+entry.getValue()+"#");
		   		bw.newLine(); 
			}
		   	bw.close();
	   }
	   catch (IOException e)
	   {
		   e.printStackTrace();
	   }
   }
}
