import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
public class SecondaryArray
{

	ArrayList<BuildSecondaryIndexStructure> objlist = new ArrayList<BuildSecondaryIndexStructure>();
	ArrayList<BuildSecondaryIndexStructureLong> ictlist = new ArrayList<BuildSecondaryIndexStructureLong>();
	ArrayList<BuildSecondaryIndexStructure> classifierObjList = new ArrayList<BuildSecondaryIndexStructure>();
			
	ArrayList<BuildSecondaryIndexStructure> Titlelist = new ArrayList<BuildSecondaryIndexStructure>();
	HashSet<String> hashstop = new HashSet<String>();
	private int sizeOfResult = 1;
	public final int considerOnlyTopKTerms = 2;    //truened to 5 from 2
	public final int considerOnlyTopKTermsOfCategory = 3;
	ICTMapWrapper freqICT;
	private boolean debugMode = false;
	private String currentTitle;
	private boolean usePOS = false;
	
	public static void main(String argv[]) throws IOException
	{
		try
		{
		SecondaryArray m = new SecondaryArray();
	   	m.run();
		}
		catch (Exception e)
		{
		//	e.printStackTrace();
		}
	}
	
	public void run() throws IOException
	{
		String input = "stopwords123";
		String line = new String();
		String word = new String();
		String offset = new String();
		int index,count;
		
		File myFile = new File(input);
		FileInputStream myInputStream       	= new FileInputStream(myFile);
		InputStreamReader myInputStreamReader 	= new InputStreamReader(myInputStream);
		BufferedReader myBufferedReader    		= new BufferedReader(myInputStreamReader);
		
		line = myBufferedReader.readLine();
		while(line != null)
		{
			line = line.replace(" ", "");
			hashstop.add(line);
			line = myBufferedReader.readLine();
			//System.out.println(line);
		}
		myBufferedReader.close();
		//System.out.println(hashstop.size());
		
		input = indexer.PREFIX+"SecondaryIndex";
		myFile = new File(input);
		myInputStream       = new FileInputStream(myFile);
		myInputStreamReader = new InputStreamReader(myInputStream);
		myBufferedReader    = new BufferedReader(myInputStreamReader);
		
		line = new String();
		word = new String();
		offset = new String();
		line = myBufferedReader.readLine();
		count = 0;
		
		while(line != null)
		{			
				count++;
				index = line.indexOf('#');
				if(index > 0)
                {
                    word = line.substring(0, index);
                    offset = line.substring(index+1);
                    BuildSecondaryIndexStructure w1=new BuildSecondaryIndexStructure();
                    w1.word = word;
                    w1.offset =  Long.parseLong(offset);
                    objlist.add(w1);
                }
			line = myBufferedReader.readLine();
		}
		myBufferedReader.close();
	//	System.out.println("Secondary Index Size = "+count);		//comments Removal
			
		input = indexer.PREFIX+"Docid-TitleSecondaryIndex";
		myFile = new File(input);
		myInputStream       = new FileInputStream(myFile);
		myInputStreamReader = new InputStreamReader(myInputStream);
		myBufferedReader    = new BufferedReader(myInputStreamReader);
		
		line = new String();
		word = new String();
		offset = new String();
		line = myBufferedReader.readLine();
		count = 0;
		
		while(line != null)
		{			
				count++;
				index = line.indexOf('#');
				if(index > 0)
                {
                    word = line.substring(0, index);
                    offset = line.substring(index+1);
                    BuildSecondaryIndexStructure w1=new BuildSecondaryIndexStructure();
                    w1.word = word;
                    w1.offset =  Long.parseLong(offset);
                    Titlelist.add(w1);
                }
			line = myBufferedReader.readLine();
		}
		myBufferedReader.close();
	//	System.out.println("Secondary Index of Titla-Docid = "+Titlelist.size());	//comments Removal
		
		//SECONDARY ICT
		input = indexer.PREFIX+"SecondaryIndexOfICT";
		myFile = new File(input);
		myInputStream       = new FileInputStream(myFile);
		myInputStreamReader = new InputStreamReader(myInputStream);
		myBufferedReader    = new BufferedReader(myInputStreamReader);
		
		line = new String();
		word = new String();
		offset = new String();
		line = myBufferedReader.readLine();
		count = 0;
		
		while(line != null)
		{			
				count++;
				index = line.indexOf('#');
				if(index > 0)
                {
                    word = line.substring(0, index);
                    offset = line.substring(index+1);
                    BuildSecondaryIndexStructureLong w1=new BuildSecondaryIndexStructureLong();
                    try{
                    w1.word = Long.parseLong(word);
                    }catch(NumberFormatException e){
                    	w1.word = Long.MAX_VALUE;
                    }
                    //System.out.println(word);
                    boolean isProblem = false;
					try {
						w1.offset = Long.parseLong(offset);
					} catch (NumberFormatException e) {
						isProblem = true;
					}
					if(isProblem == false)
						ictlist.add(w1);
                }
			line = myBufferedReader.readLine();
		}
		myBufferedReader.close();
		//parser();
		queryer();
	}
	public void queryer() throws IOException
	{
		String query="";
		Scanner in = new Scanner(System.in);
		freqICT = new ICTMapWrapper();
		while(true)
		{
			System.out.print("\n\n"+"Enter valid Query or q/Q to quit : ");
			query = in.nextLine();
			long startTime = System.currentTimeMillis();
			//System.out.println("You entered string "+query);
			if(query.compareToIgnoreCase("q") == 0)	break;
			else parser(query);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = (stopTime - startTime);
		//    System.out.println("\n\n"+elapsedTime+" Milliseconds");    //Comment removal
		}
		freqICT.print();
		String nextQuery = "";
		System.out.println("GoodBye..... :)");
		//Next 
		int ctr =0;
		Iterator it = ICTMapWrapper.sortByComparator(freqICT.c).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> pairs = (Map.Entry) it.next();
			if(pairs.getKey().equals("")){
				continue;
			}
			if(hashstop.contains(pairs.getKey())){
				continue;
			}
			nextQuery+=pairs.getKey()+":c#";
			it.remove(); // avoids a ConcurrentModificationException
			ctr++;
			if(ctr >= considerOnlyTopKTermsOfCategory){
				break;
			}
		}
		
		ctr =0;
		it = ICTMapWrapper.sortByComparator(freqICT.i).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> pairs = (Map.Entry) it.next();
			if(pairs.getKey().equals("")){
				continue;
			}
			if(hashstop.contains(pairs.getKey())){
				continue;
			}
				//	System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");
			nextQuery+=pairs.getKey()+":i#";
			it.remove(); // avoids a ConcurrentModificationException
			ctr++;
			if(ctr >= considerOnlyTopKTerms){
				break;
			}
		}
		
		ctr =0;
		it = ICTMapWrapper.sortByComparator(freqICT.t).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> pairs = (Map.Entry) it.next();
			if(pairs.getKey().equals("")){
				continue;
			}
			if(hashstop.contains(pairs.getKey())){
				continue;
			}
			//System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");
			nextQuery+=pairs.getKey()+":t#";
			it.remove(); // avoids a ConcurrentModificationException
			ctr++;
			if(ctr >= considerOnlyTopKTerms){
				break;
			}
		}
		nextQuery = nextQuery.substring(0, nextQuery.length()-1);
	//	System.out.println("Query = "+nextQuery);	//comments Removal
		
		SecondaryArrayOfClassifier secondaryArrayOfClassifier = new SecondaryArrayOfClassifier();
		// TORUN:
		ArrayList<Integer> res = secondaryArrayOfClassifier.run(nextQuery);
		for(Integer docID : res){
			//System.out.println("DOcID = "+docID);
			Titlesearch(docID);
	//		System.out.println("Current Title = "+currentTitle);
			// Initialize the tagger
if(usePOS  == true){

			MaxentTagger tagger = new MaxentTagger("english-bidirectional-distsim.tagger");
			 
			 
			// The tagged string
			String tagged = tagger.tagString(currentTitle);
			// if()
			//output the tagged sample string onto your console
		//	System.out.println("Input: " + currentTitle);
			System.out.println("Output: "+ tagged);
}
		}
		//runSecondaryClassifier(nextQuery);
		
		//parser(query);
	}
//	private void runSecondaryClassifier(String query) throws IOException {
//		System.out.println("You entered string "+query);
//		//StringTokenizer st = new StringTokenizer(query);
//		String word = new String();
//		Boolean colon = false;
//		queryStructure query1 = new queryStructure();
//		Boolean hash = false;
//		int i;
//		
//		String[] qs = query.split("#");
//		for(String singleQuery : qs){
//			singleQuery = singleQuery.replace("\\s+", "");
//			HashSet<String> docIDs= search2(singleQuery);
//			for(String docID : docIDs){
//				Integer docIDInt = Integer.parseInt(docID);
//				String type = singleQuery.substring(singleQuery.length()-1);
//				int score = 1;
//				if(type.equals("c"))
//					score = SCORE_FOR_CATEGORY;
//				else if(type.equals("i"))
//					score = SCORE_FOR_INFOCAT;
//				if (finalDocIDMap.containsKey(docIDInt)) {
//					int cnt = finalDocIDMap.get(docIDInt);
//					finalDocIDMap.put(docIDInt, cnt + score);
//				} else {
//					finalDocIDMap.put(docIDInt, score);
//				}
//				if(debugMode)
//					System.out.println("Docid="+docID);
//			}
//		}
//		if(debugMode)
//			System.out.println("DONE SEARCHING");
//
//		
//		Iterator it = ICTMapWrapper.sortByComparator(finalDocIDMap).entrySet().iterator();
//		int cnt = 0;
//		while (it.hasNext()) {
//			Map.Entry<Integer,Integer> pairs = (Map.Entry) it.next();
//			System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");
//			Titlesearch(pairs.getKey());
//			it.remove(); // avoids a ConcurrentModificationException
//			cnt++;
//			if(cnt >= noOfResultsToDisplay){
//				break;
//			}
//		}
//	}	
//	public HashSet<String> search2(String queryWord) throws IOException
//	{
//		int i,j,mid,size,index,num;
//		size = classifierObjList.size();
//		i = 0;
//		j = size-1;
//		String input = new String();
//		String line = new String();
//		String word = new String();
//		String poslist = new String();
//		
//		while(true)
//		{
//			if(i+1 == j)	break;
//			mid = (i+j)/2;
//			BuildSecondaryIndexStructure w = classifierObjList.get(mid);
//		//	if(debugMode)
//		//		System.out.println("queryWord="+queryWord);
//			if(queryWord.compareTo(w.word) <= 0)
//			{
//				j = mid;
//			}
//			else
//			{
//				i = mid;
//			}
//		}
//		long start,end;
//		BuildSecondaryIndexStructure w = classifierObjList.get(i);
//		start = w.offset;
//		w = classifierObjList.get(j+1);
//		end = w.offset;
//		
//		input = "C:/"+indexer.PREFIX+"data/sorted_classifier.txt";
//		File myFile = new File(input);		
//		RandomAccessFile rand = new RandomAccessFile(myFile,"r"); 
//		rand.seek(start);
//		Boolean st = start < end;
//		while(start < end)
//		{
//			line = rand.readLine();
//			index = line.indexOf(":", line.indexOf(':') + 1);
//			if(index > 0)
//			{
//				word = line.substring(0, index);
//				poslist = line.substring(index+1);
//				if(queryWord.compareTo(word) == 0)
//				{
//					if(debugMode  == true)
//						System.out.println(line);
//					String []arrLine = line.split(":");
//					//System.out.println(word);
//					String[] copyOfRange = Arrays.copyOfRange(arrLine, 2, arrLine.length);
//					rand.close();
//					return new HashSet<String>(Arrays.asList(copyOfRange));
//				}
//			}
//			start = start + line.length() + 1;
//		}
//		//System.out.println("END");
//		rand.close();
//		return new HashSet<String>();
//		
//	}

	public void parser(String query) throws IOException
	{
		//String query="usa";
	//	System.out.println("You entered string "+query); //comments Removal
		StringTokenizer st = new StringTokenizer(query);
		String word = new String();
		Boolean colon = false;
		queryStructure query1 = new queryStructure();
		Boolean hash = false;
		int i;
		
		ArrayList<queryStructure> q = new ArrayList<queryStructure>();
		
	     while (st.hasMoreTokens())
	     {
	    	 word = st.nextToken();
	    	 colon = false;
	    	 if(word.compareTo("t:") == 0)
	    	 {
	    		// System.out.println("Title");
	    		 query1.Title = true;
	    		// word = st.nextToken();
	    		 
//	    		 if(hashstop.contains(word) == false)
//	    		 {
//	    			 query1.word = word;
//	    			 query1 = search(query1);
//	    			 q.add(query1) ;
//	    		 }
	    		 query1 = new queryStructure();
	    		 colon = true;
	    	 }
	    	 else if( word.compareTo("b:") == 0 )
	    	 {
	    		// word = st.nextToken();
	    		 query1 = new queryStructure();
	    		 query1.Text = true;
	    		 colon = true;
	    	 }
	    	 else if(word.compareTo("i:") == 0)
	    	 {
	    		// word = st.nextToken();
	    		 if(hashstop.contains(word) == false)
	    			 q.add(query1) ;
	    		 query1 = new queryStructure();
	    		 query1.Infobox = true;
	    		 colon = true;
	    	 }
	    	 else if(word.compareTo("l:") == 0)
	    	 {
	    	//	 word = st.nextToken();
	    		 query1 = new queryStructure();
	    		 query1.Links = true;
	    		 colon = true;
	    	 }
	    	 else if( word.compareTo("c:") == 0)
	    	 {
	    	//	 word = st.nextToken();
	    		 query1 = new queryStructure();
	    		 query1.Category = true;
	    		 colon = true;
	    	 }
	    	 if(!colon)
	    	 {
	    		 hash = false;
	    		 if(!hashstop.contains(word))
	    		 {
		    		 Stemmer s = new Stemmer();
					 s.add(word.toCharArray(),word.length());
					 s.stem();
					 word = s.toString();
					 if(!hashstop.contains(word))
					 {
						 if(!colon)
			    		 {
							 query1.word = word;
							 query1.Title = true;
							 query1.Text = true;
							 query1.Infobox = true;
							 query1.Links = true;
							 query1.Category = true;
			    		 }
			    		 else
			    		 {
			    			 query1.word = word;
			    		 }
			    		 query1 = search(query1);
			    		 colon = false;
					 }
					 else
					 {
						 hash = true;
					 }
	    		 }
	    		 else
	    		 {
	    			 hash = true;
	    		 }
	    		 if(!hash)
	    		 {
	    			q.add(query1) ;
	    			query1 = new queryStructure();
	    		 }
	    	 }
	     }
	     //System.out.println("####################"+q.size());
	     Intersection(q);
	}
	public void Intersection(ArrayList<queryStructure> q) throws IOException
	{
		int i,size;
		size = q.size();
		Set<Integer> interset = new HashSet<Integer>();
	    interset.addAll(new HashSet<Integer>(q.get(0).doclist.keySet()));
	//    System.out.println(interset.size()+"##");			//comments Removal
		for(i=1;i<size;i++)
		{
			interset.retainAll(q.get(i).doclist.keySet());
		}
		//System.out.println(interset.size()+"##");
//		System.out.println("\n\nTop 15 DocId and Title Pairs are"+"\n");		//comments Removal
		Rank(interset,q,sizeOfResult );
		if(interset.size() < sizeOfResult)
		{
			//System.out.println("Union");
			Union(q,sizeOfResult-interset.size(),interset);
		}
	}
	
	public void Union(ArrayList<queryStructure> q,int size1,Set<Integer> interset) throws IOException
	{
		int i,size;
		size = q.size();
		Set<Integer> union = new HashSet<Integer>();
	    union.addAll(new HashSet<Integer>(q.get(0).doclist.keySet()));
	    //System.out.println(union.size()+"##");
		for(i=1;i<size;i++)
		{
			union.addAll(new HashSet<Integer>(q.get(i).doclist.keySet()));
		}
		union.removeAll(interset);
		//System.out.println(union.size()+"##");
		Rank(union,q,size1);
	}
	
	public void Rank(Set<Integer> set,ArrayList<queryStructure> q,int size1) throws IOException
	{
		int i,j,size2,num,size;
		double value;
		size2 = q.size();
		Iterator<Integer> it = (Iterator<Integer>) set.iterator();
		DocCount doc;
		i = 0;
		ArrayList<SortStructure> list=new ArrayList<SortStructure>();
		SortStructure s;
		while(it.hasNext())
		{
			value = 0;
			i++;
			num = (int) it.next();
			for(j=0;j<size2;j++)
			{
				doc = new DocCount();
				doc = q.get(j).doclist.get(num);
				if(doc != null)
				{
					size = q.get(j).doclist.size();
					if(q.get(j).Title && size > 1)		value = value + doc.Title*20*(Math.log(1000000000/size));
					if(q.get(j).Category && size > 1)	value = value + doc.Category*6*(Math.log(1000000000/size));
					if(q.get(j).Text && size > 1)		value = value + doc.Text*4*(Math.log(1000000000/size)); 
					if(q.get(j).Links && size > 1)		value = value + doc.Links*1*(Math.log(1000000000/size)); 
					if(q.get(j).Infobox && size > 1)	value = value + doc.Infobox*3*(Math.log(1000000000/size));
				}
				
			}
			s = new SortStructure();
			s.value = value;
			s.did = num;
			list.add(s);
			//System.out.println(value+"**"+i+"**"+num);
		}
		//Collections.sort(list.);
		if(list.size() > 1)
		{
			Collections.sort(list, new Comparator<SortStructure>() {	
	
			public int compare(SortStructure f1,SortStructure f2)
			{
				if(f1.value > f2.value)
					return -1;
				else if(f1.value < f2.value)
					return 1;
				else
					return 0;
			}
		    });
		}
		print(list,size1);
	}
	
	public void print(ArrayList<SortStructure> list,int size1) throws IOException
	{
		int i,size;
		//System.out.println("!@#$"+list.size()+"@@@@"+size1);
		size = list.size();
		Boolean st;
		ICTMapWrapper unionedICTMapWrapper = new ICTMapWrapper();
		for(i=0;i < size1 && i<size;i++)
		{
			//System.out.println(i+"!!!"+list.get(i).did);
			st = Titlesearch(list.get(i).did);
			ICTWrapper ictWrapper = Titlesearch_WithICT(list.get(i).did);
			unionedICTMapWrapper.union(ictWrapper);
			//System.out.println(i+"##"+st);
			if(!st)	size1++;
		}
	//	unionedICTMapWrapper.print();		//comments removal
		freqICT.merge(unionedICTMapWrapper);
	}
	
	private ICTWrapper Titlesearch_WithICT(int did) {
		String didString = new Integer(did).toString();
//		System.out.println("Titlesearch_WithICT: did="+did);		//comments Removal
		try {
			//##
			//ICTWrapper ictWrapper = ReaderICT.getICTForDocID(new Integer(did).toString());
			ICTWrapper ictWrapper = new ICTWrapper();
			int i,j,mid,size,index,num;
			size = ictlist.size();
			i = 0;
			j = size-1;
			String input = new String();
			String line = new String();
			String word = new String();
			String poslist = new String();
			
			while(true)
			{
				if(i+1 == j)	break;
				mid = (i+j)/2;
				BuildSecondaryIndexStructureLong w = ictlist.get(mid);
				if(did <= w.word)
				{
					j = mid;
				}
				else
				{
					i = mid;
				}
			}
			long start,end;
			BuildSecondaryIndexStructureLong w = ictlist.get(i);
			start = w.offset;
			w = ictlist.get(j+1);
			end = w.offset;
			
			input = "C:/"+indexer.PREFIX+"data/infocat_writerCombined.txt";
			File myFile = new File(input);		
			RandomAccessFile rand = new RandomAccessFile(myFile,"r"); 
			rand.seek(start);
			Boolean st = start < end;
			while(start < end)
			{
				line = rand.readLine();
				index = line.indexOf('!');
				if(index > 0)
				{
					word = line.substring(0, index);
					if(debugMode)
						System.out.println("w="+word);
					poslist = line.substring(index+1);
					if(didString.compareTo(word) == 0)
					{
//						System.out.println("FOUNDDD");
						int zeroIndex = line.indexOf("!");
						String thisDocID = line.substring(0,zeroIndex);
						int oneIndex = line.indexOf("#",zeroIndex+1);
						int twoIndex = line.indexOf("#",oneIndex+1);
						String cLine = line.substring(zeroIndex+1,oneIndex);
						if(debugMode)
							System.out.println("CLine="+cLine);
						String iLine = line.substring(oneIndex+1,twoIndex);
						String tLine = line.substring(twoIndex+1);
						
						String[] arrLine=cLine.split(":");
						String[] copyOfRange = Arrays.copyOfRange(arrLine, 0, arrLine.length);
						ictWrapper.c = new HashSet<String>(Arrays.asList(copyOfRange));
						if(debugMode){
							System.out.println("AFter c =");
							ictWrapper.print();
							System.out.println("End OF AFter c =");
						}
						arrLine=iLine.split(":");
						copyOfRange = Arrays.copyOfRange(arrLine, 0, arrLine.length);
						ictWrapper.i = new HashSet<String>(Arrays.asList(copyOfRange));
						
						arrLine=tLine.split(":");
						copyOfRange = Arrays.copyOfRange(arrLine, 0, arrLine.length);
						ictWrapper.t = new HashSet<String>(Arrays.asList(copyOfRange));
						
						System.out.println(line);
						break;
					}
				}
				start = start + line.length() + 2;
			}
			//System.out.println("END");
			rand.close();
			//##
	//		ictWrapper.print();
			return ictWrapper;
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return true;
		return new ICTWrapper();
	}
	public Boolean Titlesearch(int did) throws IOException
	{
		int i,j,mid,size,index,num;
		size = Titlelist.size();
		i = 0;
		j = size-1;
		String input = new String();
		String line = new String();
		String word = new String();
		String title = new String();
		
		while(true)
		{
			if(i+1 == j)	break;
			mid = (i+j)/2;
			BuildSecondaryIndexStructure w = Titlelist.get(mid);
			//String.valueOf();
			if(did <= Integer.parseInt(w.word))
			{
				j = mid;
			}
			else
			{
				i = mid;
			}
			//System.out.println(i+"**"+j+"**"+did+"**"+Integer.parseInt(w.word));
		}
		//System.out.println(i+"**"+j);
		long start,end;
		BuildSecondaryIndexStructure w = Titlelist.get(i);
		start = w.offset;
		w = Titlelist.get(j);
		end = w.offset;
		
		input = indexer.PREFIX+"Docid-Title";
		File myFile = new File(input);		
		RandomAccessFile rand = new RandomAccessFile(myFile,"r"); 
		rand.seek(start);
		Boolean st =false;
		
		//line = rand.readLine();
		//System.out.println(line);
		while(start < end)
		{
			//System.out.println("Entry");
			line = rand.readLine();
			index = line.indexOf('#');
			if(index > 0)
			{
				//System.out.println("Entry");
				word = line.substring(0, index);
				title = line.substring(index+1,line.length()-1);
				st = did == Integer.parseInt(word);
				//System.out.println(word+"$$"+did+st);
				if(did == Integer.parseInt(word))
				{
					// RJD CHANGE START
					if(title.startsWith("wikipedia:") || title.startsWith("template:"))
					{
						st = false;
						break;
					}
					currentTitle = title;
		//KeEP
					System.out.println(title);			//"DocId = "+ word +"Title = "+  //comments Removal
					// RJD CHANGE END
					st = true;
					break;
				}
			}
			start = start + line.length() + 2; // 2RJD
		}
		//System.out.println("END");
		rand.close();
		return st;
	}
	
	public queryStructure search(queryStructure query) throws IOException
	{
		int i,j,mid,size,index,num;
		size = objlist.size();
		i = 0;
		j = size-1;
		String input = new String();
		String line = new String();
		String word = new String();
		String poslist = new String();
		
		while(true)
		{
			if(i+1 == j)	break;
			mid = (i+j)/2;
			BuildSecondaryIndexStructure w = objlist.get(mid);
			if(query.word.compareTo(w.word) <= 0)
			{
				j = mid;
			}
			else
			{
				i = mid;
			}
		}
		long start,end;
		BuildSecondaryIndexStructure w = objlist.get(i);
		start = w.offset;
		w = objlist.get(j+1);
		end = w.offset;
		
		input = "C:/"+indexer.PREFIX+"Wiki_Split_Files/Indexed/1/store1";
		File myFile = new File(input);		
		RandomAccessFile rand = new RandomAccessFile(myFile,"r"); 
		rand.seek(start);
		Boolean st = start < end;
		while(start < end)
		{
			line = rand.readLine();
			index = line.indexOf(' ');
			if(index > 0)
			{
				word = line.substring(0, index);
				poslist = line.substring(index+1);
				if(query.word.compareTo(word) == 0)
				{
					//System.out.println(word);
					TokenizeDocid(poslist,query);
					break;
				}
			}
			start = start + line.length() + 2;
		}
		//System.out.println("END");
		rand.close();
		return query;
	}
	public void TokenizeDocid(String poslist,queryStructure query) throws IOException
	{
		String token = new String();
		String word = new String();
		String field = new String();
		String number = new String();
		StringTokenizer st = new StringTokenizer(poslist,"#");
		StringTokenizer stemtoken;
		int i,num,did,count,count1,count2;
		did = -1;
		count = 1;
		count1 = 1;
		count2 = 1;
		DocCount d = new DocCount();
		//System.out.println("Start");
		query.doclist = new TreeMap<Integer, DocCount>();
	    while (st.hasMoreTokens())
	    {
	    	token = st.nextToken();
	    	i = 0;
	    	stemtoken =  new StringTokenizer(token);
	    	while(stemtoken.hasMoreTokens())
	    	{
	    		i++;
	    		if(i == 1)
	    		{
	    			word = stemtoken.nextToken();
	    			d = new DocCount();
	    			d.docid = Integer.parseInt(word);
	    			did = d.docid;
	    		}
	    		else
	    		{
	    			field = stemtoken.nextToken();
	    			if(field.charAt(0)=='T')
	    			{
	    				number =  field.substring(1);
	    				d.Title = Integer.parseInt(number);
	    			}
	    			else if(field.charAt(0)=='I')
	    			{
	    				number =  field.substring(1);
	    				d.Infobox = Integer.parseInt(number);
	    			}
	    			else if(field.charAt(0)=='C')
	    			{
	    				number =  field.substring(1);
	    				d.Category = Integer.parseInt(number);
	    			}
	    			else if(field.charAt(0)=='L')
	    			{
	    				number =  field.substring(1);
	    				d.Links = Integer.parseInt(number);
	    			}
	    			else if(field.charAt(0)=='D')
	    			{
	    				number =  field.substring(1);
	    				d.Text = Integer.parseInt(number);
	    			}
	    		}
	    		
	    		if(did > 0)
	    		{
	    			if(!query.doclist.containsKey(did))
	    			{
	    				query.doclist.put(did,d);
	    			}
	    		}
	    	}
	    }
	}
}