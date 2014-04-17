import java.util.HashMap;
import java.util.HashSet;

public class ICTWrapper {
	public HashSet<String> i;
	public HashSet<String> c;
	public HashSet<String> t;

	public ICTWrapper() {
		super();
		i = new HashSet<String>();
		c = new HashSet<String>();
		t = new HashSet<String>();
	}

	public void print() {
		System.out.println("PRINTING ICTWrapper");
		System.out.println("Printing C:");
		for(String s:c){
			System.out.print(s+",");
		}
		System.out.println("Printing I:");
		for(String s:i){
			System.out.print(s+",");
		}
		System.out.println("Printing T:");
		for(String s:t){
			System.out.print(s+",");
		}

	}

	public void union(ICTWrapper ictWrapper) {
		i.addAll(ictWrapper.i);
		c.addAll(ictWrapper.c);
		t.addAll(ictWrapper.t);
	}
}
