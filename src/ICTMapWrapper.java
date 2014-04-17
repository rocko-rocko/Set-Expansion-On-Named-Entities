import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ICTMapWrapper {
	private static final Integer SCALINGFORPRESENT = 100;
	public TreeMap<String, Integer> i;
	public TreeMap<String, Integer> c;
	public TreeMap<String, Integer> t;

	public ICTMapWrapper() {
		super();
		i = new TreeMap<String, Integer>();
		c = new TreeMap<String, Integer>();
		t = new TreeMap<String, Integer>();
	}

	public void print() {
//		System.out.println("PRINTING ICT MAP Wrapper");		//Comment removal
//		System.out.println("Printing C:");				//Comment removal
		Iterator it = sortByComparator(c).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");
			it.remove(); // avoids a ConcurrentModificationException
		}
	//	System.out.println("Printing I:");					//Comment removal
		it = sortByComparator(i).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
	//		System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");		//Comment removal
			it.remove(); // avoids a ConcurrentModificationException
		}
	//	System.out.println("Printing T:");			//Comment removal
		it = sortByComparator(t).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
	//		System.out.print(pairs.getKey() + ":" + pairs.getValue() + "-");			//Comment removal
			it.remove(); // avoids a ConcurrentModificationException
		}

	}

	public void union(ICTWrapper ictWrapper) {
		for (String s : ictWrapper.i) {
			if (i.containsKey(s)) {
				int cnt = i.get(s);
				i.put(s, cnt + 1);
			} else {
				i.put(s, 1);
			}
		}
		for (String s : ictWrapper.c) {
			if (c.containsKey(s)) {
				int cnt = c.get(s);
				c.put(s, cnt + 1);
			} else {
				c.put(s, 1);
			}
		}
		for (String s : ictWrapper.t) {
			if (t.containsKey(s)) {
				int cnt = t.get(s);
				t.put(s, cnt + 1);
			} else {
				t.put(s, 1);
			}
		}
	}

	public void merge(ICTMapWrapper unionedICTMapWrapper) {
		Iterator<Entry<String, Integer>> it = unionedICTMapWrapper.i.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			if (i.containsKey(pairs.getKey())) {
				int wt = (i.get(pairs.getKey())) + SCALINGFORPRESENT
						+ pairs.getValue();
				i.put(pairs.getKey(), wt);
			} else {
				i.put(pairs.getKey(), (int) pairs.getValue());
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
		it = unionedICTMapWrapper.c.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			if (c.containsKey(pairs.getKey())) {
				int wt = (c.get(pairs.getKey())) + SCALINGFORPRESENT
						+ pairs.getValue();
				c.put(pairs.getKey(), wt);
			} else {
				c.put(pairs.getKey(), (int) pairs.getValue());
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
		it = unionedICTMapWrapper.t.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			if (t.containsKey(pairs.getKey())) {
				int wt = (t.get(pairs.getKey())) + SCALINGFORPRESENT
						+ pairs.getValue();
				t.put(pairs.getKey(), wt);
			} else {
				t.put(pairs.getKey(), (int) pairs.getValue());
			}
			it.remove(); // avoids a ConcurrentModificationException
		}

	}
	public static Map sortByComparator(Map unsortMap) {
		List list = new LinkedList(unsortMap.entrySet()); 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                                       .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
