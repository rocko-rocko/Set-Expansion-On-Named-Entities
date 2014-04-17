import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xml.sax.InputSource;

public class merger
{
	int N = 300;

	//public static void main(String[] args) throws IOException
	public void run(String parent)/*,String finalop)*/ throws IOException
	{
        merger m = new merger();
        //parent = "C:/Users/Vivek/Desktop/qwe";
        String output = parent;
        String input = parent;
        String finalop = parent;
        int len = new File(input).listFiles().length;
        
        int level = 0;
        //int total = 31 - Integer.numberOfLeadingZeros(len);
        double total = Math.log(len) / Math.log(N);
        System.out.println("Total no.of levels to be merged: "+Math.ceil(total));
        while(len > 1)
        {
        	input = output;
        	level++;
        	System.out.println("Merging of Level "+level+" started ("+len+" files)");
        	output = parent+"/"+level;        	len = m.merge(input,output,len,finalop);
        	System.out.println("Merging of Level "+level+" completed ("+len+" files)");
        }
        System.out.println("Complete merging completed");
        System.out.println("Output is in folder: "+output);
       /* System.out.println(new String(finalop.getBytes("UTF-8"), "UTF-8"));
        File op = new File(output);
        op.renameTo(new File(finalop));*/
        
        
    }

	public int merge(String parent, String output, int len,String finalop) throws IOException
	{
		int i,j,even,len1,count;
		File[] chunks = new File(parent).listFiles();
		(new File(output)).mkdirs();
		//File[] output1 = new File(output).listFiles();
		//int len = chunks.length;
		even = len%N;
		len1 = len - even;
		count = 1;
		String[] file;
		//System.out.println(new String(output.getBytes("UTF-8"), "UTF-8"));


		for(i=0;i<len1;i=i+N)
		{
			file = new String[N];
			for(j=0;j<N;j++)
			{
				//System.out.println(i+j+count+new String(chunks[i+j].getAbsolutePath().getBytes("UTF-8"), "UTF-8"));
				file[j] = new String(chunks[i+j].getAbsolutePath().getBytes("UTF-8"), "UTF-8");
				//file[i+j] = new InputSource(new InputStreamReader(new FileInputStream(new File(chunks[i+j].getAbsolutePath())),"UTF-8")).setEncoding("UTF-8");
			}
			mergeNinto1(file,new String(output.getBytes("UTF-8"), "UTF-8") + "/store" + count,N,finalop);
			count++;
		}
		if(len1 != len)
		{
			file = new String[len%N];
			for(j=0;j<len%N;j++)
			{
				file[j] = new String(chunks[len1+j].getAbsolutePath().getBytes("UTF-8"), "UTF-8");
				//file[i+j] = new InputSource(new InputStreamReader(new FileInputStream(new File(chunks[i+j].getAbsolutePath())),"UTF-8")).setEncoding("UTF-8");
			}
			mergeNinto1(file,new String(output.getBytes("UTF-8"), "UTF-8") + "/store" + count,len%N,finalop);
			return 1 + len/N;
		}
		else	return len/N;

	}



	public void mergeNinto1(String[] file, String output,int size,String finalop) throws IOException
	{
        BufferedReader[] readers = new BufferedReader[size];
        String[] firstLines = new String[size];
        String[] keywords = new String[size];
        String[] values = new String[size];
        String small = null;
        int count = 0;
        int count1 = 0;
        
       // System.out.println(new String(output.getBytes("UTF-8"), "UTF-8"));
        File op = new File(output);
        BufferedWriter bw;//=new BufferedWriter(new FileWriter(op));
        int i,min,index,k=0;

        min = 0;
        boolean status = true;
        boolean[] st = new boolean[size];
        boolean buf = false;

        for(i=0;i<size;i++)
        {
        	System.out.println(file[i]);
        	readers[i] = new BufferedReader(new FileReader(file[i]));
        	st[i] = true;
        }
        bw=new BufferedWriter(new FileWriter(op,true));
        while(true)
        {
        	count++;
        	status = false;
        	for(i = 0; i < size; i++)
        	{
        		if(st[i])
        		{
        			firstLines[i] = readers[i].readLine();
        			if(firstLines[i] != null)	status = true;
        		}
        	}

        	if(!status)
        	{
        		System.out.println(count);
        		bw.close();
        		break;
        	}
        	for(i = 0; i < size; i++)
        	{
        		if(firstLines[i] != null)
        		{
        			index = firstLines[i].indexOf(' ');
                    if(index > 0)
                    {
                        keywords[i] = firstLines[i].substring(0, index);
                        values[i] = firstLines[i].substring(index+1);
                    }
        		}
        		else
        		{
        			keywords[i] = null;
                    values[i] = null;
        		}
        	}
        	//System.out.println(count+"**");
        	min = 0;
        	for(i = 0,count1=1; i < size; i++,count1++)
        	{
        		//System.out.println(count1+"##");
        		//if(firstLines[i] != null && keywords[i] != null && keywords[min].compareTo(keywords[i]) > 0)
        		if(firstLines[i] != null && (keywords[min] == null || (keywords[i] != null && keywords[min].compareTo(keywords[i]) > 0)))
        		{
        			min = i;
        		}
        	}
        	//System.out.println("Exit");
bw.write(keywords[min]+" ");
        	
        	for(i = 0; i< size; i++)
        	{
        		if(firstLines[i] != null && keywords[i] != null && keywords[min].compareTo(keywords[i]) == 0)
        		{
        			bw.write(values[i]);
        			st[i] = true;
        		}
        		else
        		{
        			st[i] = false;
        		}
        	}
        	bw.newLine();
        }
        //System.out.println("Finished");
	}

}