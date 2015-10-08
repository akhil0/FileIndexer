import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


public class BlurbsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HashMap<String, HashMap> posmap = new HashMap<String,HashMap>();
		HashMap<String, ArrayList> termmap = new HashMap<String,ArrayList>();

		termmap.put("aki", new ArrayList<>(Arrays.asList(18, 20,40,90,130)));
		termmap.put("vig", new ArrayList<>(Arrays.asList(14, 58,91,109,120)));
		termmap.put("rev", new ArrayList<>(Arrays.asList(8, 16,50,85,96)));
		termmap.put("abhi", new ArrayList<>(Arrays.asList(17, 19,45,89,145)));
		termmap.put("maha", new ArrayList<>(Arrays.asList(1, 98,107,114,135)));

		ArrayList<Integer> a = new ArrayList<>();

		System.out.println(termmap.toString());

		boolean b = IsNullSizeMap(termmap);
		//Iterator<String> itr = termmap.keySet().iterator();
		while(!IsNullSizeMap(termmap))
		{
			removemin(a,termmap);

		}

		a.remove(new Integer(0));
		//System.out.println(termmap.size());
		System.out.println(a.toString());
		int minvalue = Collections.min(a);
		System.out.println("Minimum Span = " + minvalue);
		System.out.println(termmap.toString());


	}



	private static HashMap<String, ArrayList> removemin(
			ArrayList<Integer> a, HashMap<String, ArrayList> termmap) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		boolean reachedend = false;
		Iterator<String> itr = termmap.keySet().iterator();
		String id = "";
		while(itr.hasNext())
		{
			String term = itr.next();
			ArrayList list = termmap.get(term);
			if((int)list.get(0) < min && list.size()!= 0)
			{
				id = term;
				min = (int)list.get(0) ;
			}
			if((int)list.get(0) > max && list.size()!= 0)
			{
				max = (int)list.get(0) ;
			}

		}
		System.out.println(id);
		int span = max-min;
		a.add(span);
		System.out.println("span = " + span);
		ArrayList templist = termmap.get(id);

		templist.remove(new Integer(min));


		if(templist.size() == 0)
		{
			termmap.remove(id);
		}
		else
		{
			termmap.put(id,templist);
		}
		return termmap;
	}



	private static boolean IsNullSizeMap(HashMap<String, ArrayList> termmap) {
		boolean b = true;
		Iterator<String> itr = termmap.keySet().iterator();
		while(itr.hasNext())
		{
			String term = itr.next();
			if ( termmap.get(term).size() == 0)
				b = b && true;
			else
				b = b && false;
		}		
		return b;
	}

}
