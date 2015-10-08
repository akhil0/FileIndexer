import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.tartarus.snowball.ext.PorterStemmer;
import java.io.*;


public class lapsmoothing {

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



		// Reading List of Queries into List
		List<String> querylist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\setquery.txt"));
		//List<String> querylist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\new.txt"));
		Iterator<String> queritr = querylist.iterator();
		while(queritr.hasNext())
		{
			String[] strarray = queritr.next().split("     ");
			System.out.println("Should be 2 = " + strarray.length);
			String queryno = strarray[0];
			String mainstr = strarray[1];
			String[] words = mainstr.split(" ");
			System.out.println(queryno + " - " + words.length);
			HashMap<String, Double> resultmap = new HashMap<String, Double>();
			HashMap<String, Double> staticmap = new HashMap<String, Double>();
			staticmap.putAll(lengthmap);
			Iterator<String> itr = staticmap.keySet().iterator();
			while(itr.hasNext())
			{
				staticmap.put(itr.next(),(double) 0);
			}
			for(int i = 0; i < words.length ; i++)
			{
				String qb = words[i];
				//System.out.println("Done with = " + qb);
				PorterStemmer stemmer = new PorterStemmer();
				stemmer.setCurrent(qb); //set string you need to stem
				stemmer.stem();
				String local = stemmer.getCurrent();

				resultmap =  tfidf(local, resultmap, idmap, lengthmap, catalogmap,staticmap);
			}
			System.out.println("done with" + queryno);
			System.out.println("resultmap size is " + resultmap.size());
			printmap(resultmap,queryno);
		}

	}

	private static HashMap<String, Double> tfidf(String local,HashMap<String, Double> resultmap,
			HashMap<Integer, String> idmap, HashMap<String, Double> lengthmap,
			HashMap<String, String> catalogmap, HashMap<String, Double> staticmap) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("C:\\Users\\AKI\\workspace\\Ass2\\index.txt", "rw");
		Iterator itrnext = staticmap.keySet().iterator();
		while(itrnext.hasNext())
		{
			String id = itrnext.next().toString();
			double oldtf = staticmap.get(id);
			double v = 178081 ;
			double len = lengthmap.get(id);
			double newtf = 1/(len + v);
			double lap = Math.log(newtf);
			staticmap.put(id, oldtf+lap);
		}
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
			//System.out.println(r);

			System.out.println(al.length - 1);

			for (int i = 1; i < al.length ; i++)
			{
				String[] lop = al[i].split("#");
				System.out.println(Integer.parseInt(lop[0]));
				String doc = idmap.get(Integer.parseInt(lop[0]));
				System.out.println(doc);
				double len = lengthmap.get(doc);
				System.out.println(len);
				int tf = lop.length - 1;
				int df = al.length - 1;
				double v = 178081 ;
				double lap = (tf+1) / (len+v) ;
				double inc = 1/(len+v);
				lap = Math.log(lap);
				inc = Math.log(inc);
				double okapitf = (tf/(tf + 0.5 + 1.5*(len/262)));
				if(resultmap.containsKey(doc))
				{
					double oldtf = staticmap.get(doc);
					staticmap.put(doc, oldtf + lap - inc);
				}
				else
					staticmap.put(doc, okapitf);
			}

		}
		raf.close();
		return staticmap;
	}



	//Write HashMap to a File
	private static void printmap(HashMap<String, Double> resultmap, String string) {

		HashMap<String, Double> newmap = new HashMap<String, Double>();
		newmap = (HashMap<String, Double>) sortByValues(resultmap);
		Iterator newmapitr = newmap.keySet().iterator();
		int id0 = 0;
		File log = new File("C:\\Users\\AKI\\workspace\\Ass2\\lap.txt");
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
			System.out.println("newmap size is " + newmap.size());

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


}
