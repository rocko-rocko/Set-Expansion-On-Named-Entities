import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ICTCombiner {

	public static void main(String[] args) throws IOException {
	//	indexer.PREFIX = "";
		//BufferedReader br = new BufferedReader(new FileReader(new File( "C:/"+indexer.PREFIX+"data/infocat_writer.txt")));
		BufferedReader br = new BufferedReader(new FileReader(new File("C:/"+indexer.PREFIX+"data/infocat_writer.txt")));
		BufferedWriter out = new BufferedWriter(new FileWriter(new File("C:/"+indexer.PREFIX+"data/infocat_writerCombined.txt")), 32768);
		String line;
		String previousDocID="-1";
		String cString = "",iString="",tString="";
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
			int one = line.indexOf(":");
			if(one < 0) continue;
			String thisDocID = line.substring(0, one);
			int two = line.indexOf(":",one+1);
			if(two < 0) continue;
			String thisCategory = line.substring(one+1, two);
			if(thisDocID.equals(previousDocID)){
				if(thisCategory.equals("i")){
					iString = line.substring(two+1);
				}else if (thisCategory.equals("c")){
					cString = line.substring(two+1);
				}else{
					tString = line.substring(two+1);
				}
			}else{
				if(previousDocID.equals("-1") == false){
					out.write(previousDocID+"!"+cString+"#"+iString+"#"+tString);
					out.newLine();
				}
				previousDocID = thisDocID;
				cString = "";
				iString = "";
				tString = "";
				if(thisCategory.equals("i")){
					iString = line.substring(two+1);
				}else if (thisCategory.equals("c")){
					cString = line.substring(two+1);
				}else{
					tString = line.substring(two+1);
				}
			}
		}
		br.close();
		out.close();
	}

}
