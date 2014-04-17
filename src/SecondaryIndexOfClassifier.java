import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class SecondaryIndexOfClassifier 
{
	public static void main(String argv[]) throws IOException
	{
		String input = "C:/"+indexer.PREFIX+"data/sorted_classifier.txt";
		File myFile = new File(input);
				
		FileInputStream myInputStream       = new FileInputStream(myFile);
		InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);
		BufferedReader myBufferedReader    = new BufferedReader(myInputStreamReader);
		FileChannel   myFileChannel = myInputStream.getChannel();
		
		File op = new File(indexer.PREFIX+"SecondaryIndexOfClassifier");
        BufferedWriter bw = new BufferedWriter(new FileWriter(op));
		
		ArrayList<BuildSecondaryIndexStructure> objlist = new ArrayList<BuildSecondaryIndexStructure>();
		
		String line = new String();
		String word = new String();
		long myFilePosition = 0;
		line = myBufferedReader.readLine();
		int count = 1,index,i,j,mid;
		
		while(line != null)
		{
			
			if(count % 10 ==  1)
			{
				index = line.indexOf(':');
				if(index > 0)
                {
                    word = line.substring(0, index);
                    bw.write(word+"#"+myFilePosition);
                    bw.newLine();
                }
			}
			myFilePosition = myFilePosition + line.length() + 1;
			count++;
			line = myBufferedReader.readLine();
		}
		bw.close();
		 
//		SecondaryIndexTitle s = new SecondaryIndexTitle();
//		s.run();
		
//		SecondaryArray m = new SecondaryArray();
//	   	m.run();
	}
}
