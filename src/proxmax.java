import groovy.ui.SystemOutputInterceptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TermAutomatonQuery;
import org.tartarus.snowball.ext.PorterStemmer;


public class proxmax {

	public static void main(String[] args) throws IOException {

		List<String> doclist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\Downloads\\AP89_DATA\\AP_DATA\\doclist.txt"));
		HashMap<Integer, String> idmap = new HashMap<Integer, String>();
		Iterator<String> docitr = doclist.iterator();
		while(docitr.hasNext())
		{
			String[] a1 = ((String) docitr.next()).split(" ");
			String id1 = a1[1]; 
			int id = Integer.parseInt(a1[0]);
			idmap.put(id,id1);
		}
		System.out.println("idmap - " + idmap.size());

		//Reading Doc Lengths to Hash Map
		List<String> a = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Ass2\\length.txt"));
		Iterator<String> stritr = a.iterator();
		HashMap<String, Double> lengthmap = new HashMap<String, Double>();
		while(stritr.hasNext())
		{
			String line = stritr.next().toString();
			String[] quearra = line.split(" ");
			lengthmap.put(quearra[0], Double.parseDouble(quearra[1]));
		}
		System.out.println("lengthmap - "  + lengthmap.size());

		List<String> catlist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Ass2\\catalog.txt"));
		Iterator<String> catitr = catlist.iterator();
		HashMap<String, String> catalogmap = new HashMap<String, String>();
		while(catitr.hasNext())
		{
			String aline = catitr.next();
			String[] quearra = aline.split("-");;
			catalogmap.put(quearra[0],quearra[1]);
		}
		System.out.println("catalogmap size - " + catalogmap.size());
		double vocabsize = catalogmap.size();

		
		List<String> querylist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\setquery.txt"));
		//List<String> querylist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\new.txt"));
		Iterator<String> queritr = querylist.iterator();
		while(queritr.hasNext())
		{
			String[] strarray = queritr.next().split("     ");
			String queryno = strarray[0];
			String mainstr = strarray[1];
			String[] words = mainstr.split(" ");
			System.out.println(queryno + " - " + words.length);
			HashMap<String, Double> resultmap = new HashMap<String, Double>();
			
			HashMap<String, HashMap> posmap = new HashMap<String,HashMap>();
			//System.out.println(Arrays.asList(words));
			for(int i = 0; i < words.length ; i++)
			{
				String qb = words[i];
				//System.out.println("Done with = " + qb);
				PorterStemmer stemmer = new PorterStemmer();
				stemmer.setCurrent(qb); //set string you need to stem
				stemmer.stem();
				String local = stemmer.getCurrent();

				RandomAccessFile raf = new RandomAccessFile("C:\\Users\\AKI\\workspace\\Ass2\\index.txt", "rw");
				if (catalogmap.containsKey(local))
				{
					
					String pos = catalogmap.get(local);
					String[] locs = pos.split(" ");
					long posint = Long.parseLong(locs[0]);
					int size = Integer.parseInt(locs[1]);
					raf.seek(posint);
					byte[] bnew = new byte[size];
					raf.read(bnew, 0, size);
					String r = new String(bnew);
					String[] al = r.split(":");
					for (int ij = 1; ij < al.length ; ij++)
					{
						
						HashMap<String, ArrayList> termmap = new HashMap<String, ArrayList>();
						String[] lop = al[ij].split("#");
						String doc = idmap.get(Integer.parseInt(lop[0]));
						ArrayList<Integer> alist = new ArrayList<Integer>();
						for(int ik = 1; ik < lop.length; ik++)
						{
							alist.add(Integer.parseInt(lop[ik]));
						}

						termmap.put(local, alist);
						posmap.put(doc, termmap);

					}
				}
				raf.close();
			}
			//System.out.println(posmap.get("AP890307-0141"));
			//System.out.println(posmap.get("AP890307-0131"));
			//System.out.println(termmap.size());
			
		/*	File log = new File("C:\\Users\\AKI\\workspace\\Ass2\\test.txt");
			FileWriter fileWriter = new FileWriter(log, true);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			Iterator<String> positr2 = posmap.keySet().iterator();
			while(positr2.hasNext())
			{
				bufferedWriter.write(posmap.get(positr2.next()).toString() + "\n");
			}
			bufferedWriter.close();*/
			
			Iterator<String> positr = posmap.keySet().iterator();
			while(positr.hasNext())
			{
				
				
				String docid = positr.next();
				double len = lengthmap.get(docid);
				//System.out.println(posmap.get(docid));
				//System.exit(0);
				HashMap<String,ArrayList> termmaplite = posmap.get(docid);
				//System.out.println(docid);
				int coll = termmaplite.size();
				int span = Integer.MAX_VALUE;
				//System.out.println(termmaplite);
				while(true)//termmaplite.size()!=0 && !IsNullSizeMap(termmaplite) )
				{
					span = removemin(span,termmaplite);
					//System.out.println(termmaplite);
					//System.out.println(span);
					//System.out.println(span);
					if(IsNullSizeMap(termmaplite))
						break;

				}
				//System.exit(0);
				//System.out.println("Final Span = " + span);
				//spanlist.remove(new Integer(0));
				
				double val = ((1500 - span)*coll)/(len + vocabsize);
				//double val = Math.pow(0.8, (span-coll)/coll);
				resultmap.put(docid,val);
				//System.out.println(docid + " - " + val);
			}
			//System.out.println(posmap.toString());
			//System.exit(0);
			System.out.println("done with" + queryno);
			System.out.println("resultmap size is " + resultmap.size());
			printmap(resultmap,queryno);
			}
		}
	

	private static void printmap(HashMap<String, Double> resultmap, String string) {

		HashMap<String, Double> newmap = new HashMap<String, Double>();
		newmap = (HashMap<String, Double>) sortByValues(resultmap);
		Iterator newmapitr = newmap.keySet().iterator();
		
		int id0 = 0;
		File log = new File("C:\\Users\\AKI\\workspace\\Ass2\\prox.txt");
		//File log = new File("C:\\Users\\AKI\\workspace\\Elasticsearch\\okapitf.txt");

		try{
			FileWriter fileWriter = new FileWriter(log, true);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			while(newmapitr.hasNext())
			{
				id0++;
				String nextnew = newmapitr.next().toString();
				bufferedWriter.write(string + " Q0 " + nextnew + " " + id0 + " " + newmap.get(nextnew) + " Exp" + "\n");
			}
			bufferedWriter.close();

			System.out.println("Done");
			//System.out.println("newmap size is " + newmap.size());

		} catch(IOException e) {
			System.out.println("COULD NOT LOG!!");
		}


	}

	/*Reference : StackOverflow*/
	//Sort HashMap by value and return top 1000
	private static HashMap sortByValues(HashMap map) { 
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
				
				
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		} 

		HashMap newresult = new LinkedHashMap<>();
		Iterator atr = sortedHashMap.keySet().iterator();
		int id =0 ;

		if(sortedHashMap.size() < 1000)
			newresult.putAll(sortedHashMap);
		else
		{
			while(atr.hasNext() && id < 1000)
			{
				id++;
				String next = atr.next().toString();
				newresult.put( next, sortedHashMap.get(next));
			}
		}

		return newresult;
	}

	private static int removemin(
			int span2, HashMap<String, ArrayList> termmaplite) {
		//System.out.println(termmaplite);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		//boolean reachedend = false;
		Iterator<String> itr = termmaplite.keySet().iterator();
		String id = "";
		//System.out.println(termmaplite.size());
		//int ido = 1;
		while(itr.hasNext())
		{
			//System.out.println("in loop");
			//System.out.println(termmaplite);
			String term = itr.next();
			//System.out.println(term + " - " + ido);
			//ido++;
			ArrayList list = termmaplite.get(term);
			if((int)list.get(0) < min)
			{
				id = term;
				min = (int)list.get(0) ;
			}
			if((int)list.get(0) > max)
			{
				max = (int)list.get(0) ;
			}
			

		}
		//System.out.println(id);
		int span ;
		if(max == min)
			span =  min;
		else
			span =  max-min;
		
			
		//spanlist.add(span);
		//System.out.println("span = " + span);
		ArrayList templist = termmaplite.get(id);

		templist.remove(new Integer(min));


		if(templist.size() == 0)
		{
			termmaplite.remove(id);
			termmaplite.put(id, new ArrayList(Arrays.asList(Integer.MAX_VALUE)));
		}
		else
		{
			termmaplite.put(id,templist);
		}
		
		if(span < span2 && span != 0)
			return span;
		else
			return span2;
	}



	private static boolean IsNullSizeMap(HashMap<String, ArrayList> termmap) {
		boolean b = true;
		Iterator<String> itr = termmap.keySet().iterator();
		while(itr.hasNext())
		{
			
			String term = itr.next();
			if ( termmap.get(term).contains(Integer.MAX_VALUE))
				b = b && true;
			else
				b = b && false;
			
		}		
		return b;
	}


}


