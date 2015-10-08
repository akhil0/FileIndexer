import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.tartarus.snowball.ext.PorterStemmer;

public class MapTestRaf {
	//public static HashMap<String, ArrayList> map;
	static int count = 0;
	static int docid = 1;
	static HashMap<String,HashMap<Integer, ArrayList<Integer>>> wordmap = new HashMap<String,HashMap<Integer, ArrayList<Integer>>>();
	static HashMap<String,Integer> doclenmap = new HashMap<String,Integer>();
	static HashMap<String, String> newcatalogmap = new HashMap<String,String>();
	static HashMap<String, String> catalogmap = new HashMap<String,String>();
	public static void main(String[] args) throws IOException {


		// Reading DocIds and Ids into hashmap
		List<String> a = Files.readAllLines(Paths.get("C:\\Users\\AKI\\Downloads\\AP89_DATA\\AP_DATA\\doclist_new_0609.txt"));

		HashMap<Integer, String> idmap = new HashMap<Integer, String>();
		Iterator stritr = a.iterator();
		while(stritr.hasNext())
		{
			String[] a1 = ((String) stritr.next()).split("  ");
			String id1 = a1[1];
			int id = Integer.valueOf(a1[0]);
			idmap.put(id,id1);
		}
		System.out.println("Read Doc & ID Map. Success");

		List<String> stoplist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\stoplist.txt"));

		Files.walk(Paths.get("C:\\Users\\AKI\\Downloads\\Test\\test")).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				String filePath1 = filePath.toString();
				String testHtml="";
				// Writing All Text to a String.

				try {
					testHtml = String.join("\n", Files.readAllLines(Paths.get(filePath1) ,Charset.forName("ISO-8859-1")));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String[] tds = StringUtils.substringsBetween(testHtml, "<DOC>", "</DOC>");

				//fold doc-content
				for (String td : tds) {

					String title = StringUtils.substringBetween(td, "<DOCNO>", "</DOCNO>");
					title = title.trim();
					System.out.println(title);
					String[] texts = StringUtils.substringsBetween(td, "<TEXT>", "</TEXT>");
					String combinedtext = StringUtils.join(texts);

					combinedtext = combinedtext.replaceAll(" _"," ").replaceAll("_"," ").replaceAll("'", "")
							.replaceAll(" +", " ").trim().toLowerCase(); 


					List<String> listOfWords = new ArrayList<String>();

					Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
					Matcher matcher = pattern.matcher(combinedtext.toLowerCase());
					while (matcher.find()) {
						// USUALLY ZERO
						for (int i = 0; i < matcher.groupCount(); i++) {
							String term = matcher.group(i);
							listOfWords.add(term);
						}
					}

					//Updating word map before dumping
					Iterator<String> itr = listOfWords.iterator();
					int termpos = 1; 
					int doclen = 0;
					while (itr.hasNext()) { 
						String s = itr.next();
						PorterStemmer stemmer = new PorterStemmer();
						stemmer.setCurrent(s); //set string you need to stem
						stemmer.stem();
						String local = stemmer.getCurrent();
						//System.out.println(local);


						if (stoplist.contains(local)) {

						}
						else {
							if (wordmap.containsKey(local)) {
								HashMap<Integer, ArrayList<Integer>> tempmap = wordmap.get(local);
								if (tempmap.containsKey(docid)) {
									ArrayList<Integer> templist = tempmap.get(docid);
									templist.add(termpos);
									tempmap.put(docid,templist);
									wordmap.put(local,tempmap);
								}
								else {
									ArrayList<Integer> list = new ArrayList<Integer>();
									list.add(termpos);
									tempmap.put(docid, list);
									wordmap.put(local, tempmap);
								}


							}
							else {
								HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>(); 
								ArrayList<Integer> list = new ArrayList<Integer>();
								list.add(termpos);

								map.put(docid, list);
								wordmap.put(local, map);

							}

						}
						termpos++;
						doclen++;
					}
					doclenmap.put(title,doclen);

					docid++;	
				}
				wordmap.remove("");
				count = count + 1;
				//System.out.println(count);

				if (count == 4){
					try {
						dumpmap(wordmap,catalogmap,newcatalogmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
					count = 0;
					Set<String> seto = wordmap.keySet();
					wordmap.keySet().removeAll(seto);
					Set<String> setop = newcatalogmap.keySet();
					newcatalogmap.keySet().removeAll(setop);

					System.out.println(wordmap.size());
					System.out.println(catalogmap.size());
					System.out.println(newcatalogmap.size());
					//System.exit(0);
				}
			}
		});

		//System.out.println("alleg" + wordmap.get("alleg").toString());
		//System.out.println( wordmap.get("alleg").size());
		System.out.println(doclenmap.size());
		WritetoFile(doclenmap);
		System.out.println("Doc Lengths Success");

		WriteCatalogtoFile(catalogmap);
		System.out.println("Catalog Success");

	}

	private static void WriteCatalogtoFile(HashMap<String, String> catalogmap) throws IOException {
		File log = new File("C:\\Users\\AKI\\Downloads\\Test\\test\\catalog.txt");
		FileWriter fileWriter = new FileWriter(log, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		Iterator<String> lenitr = catalogmap.keySet().iterator();
		while(lenitr.hasNext()) {
			String id = lenitr.next();
			String len = catalogmap.get(id);
			bufferedWriter.write(len + "-" + len);
		}
		bufferedWriter.close();


	}

	private static void WritetoFile(HashMap<String, Integer> doclenmap) throws IOException {
		File log = new File("C:\\Users\\AKI\\Downloads\\Test\\test\\length.txt");
		FileWriter fileWriter = new FileWriter(log, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		Iterator<String> lenitr = doclenmap.keySet().iterator();
		while(lenitr.hasNext()) {
			String id = lenitr.next();
			int len = doclenmap.get(id);
			bufferedWriter.write(len + "-" + len);
		}
		bufferedWriter.close();
	}

	private static void dumpmap(HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordmap
			, HashMap<String, String> catalogmap, HashMap<String, String> newcatalogmap) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("C:\\Users\\AKI\\Downloads\\Test\\test\\old.txt", "rw");
		RandomAccessFile rafnew = new RandomAccessFile("C:\\Users\\AKI\\Downloads\\Test\\test\\new.txt", "rw");
		long newpointer = 0;
		Iterator<String> itr = wordmap.keySet().iterator();
		while(itr.hasNext())
		{
			String s = itr.next();
			if(catalogmap.containsKey(s))
			{

				String pos = catalogmap.get(s);
				String[] posar = pos.split(" ");
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(posar[0]);
				raf.seek(posint);
				byte[] bnew = new byte[size];
				raf.read(bnew, 0, size);
				String r = new String(bnew);
				HashMap<Integer, ArrayList<Integer>> temptermmap = wordmap.get(s);
				Iterator<Integer> tempitr = temptermmap.keySet().iterator();
				String str = "";
				while(tempitr.hasNext())
				{
					Integer docid = tempitr.next();
					str = str + ":" + docid;
					ArrayList<Integer> temlist = temptermmap.get(docid);
					for(Integer sop : temlist) {
						str = str + "|" + sop ; 
					}
				}
				String content = r + str;
				rafnew.seek(newpointer);
				rafnew.writeBytes(content);
				long newsize = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(s, String.valueOf(newpointer)+ " " + String.valueOf(newsize));
				newpointer = rafnew.getFilePointer();

			}
			else
			{
				rafnew.seek(newpointer);
				HashMap<Integer, ArrayList<Integer>> temptermmap = wordmap.get(s);
				Iterator<Integer> tempitr = temptermmap.keySet().iterator();
				String str = s;
				while(tempitr.hasNext())
				{
					Integer docid = tempitr.next();
					str = str + ":" + docid;
					ArrayList<Integer> temlist = temptermmap.get(docid);
					for(Integer sop : temlist) {
						str = str + "|" + sop ; 
					}
				}
				rafnew.writeBytes(str);
				long size = rafnew.getFilePointer() - newpointer;				
				newcatalogmap.put(s, String.valueOf(newpointer)+ " " + String.valueOf(size));
				newpointer = rafnew.getFilePointer();
			}

		}

		// Adding Terms present in old catalog but not in new
		while(itr.hasNext())
		{
			String term = itr.next();
			if(newcatalogmap.containsKey(term))
			{

			}
			else
			{
				String pos = catalogmap.get(term);
				String[] posar = pos.split(" ");
				String loc = posar[0];
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(loc);
				raf.seek(posint);
				byte[] bnew = new byte[size];
				raf.read(bnew, 0, size);
				String content = new String(bnew);
				//String content = raf.readLine().replaceAll("\n", "");
				rafnew.seek(newpointer);
				rafnew.writeBytes(content);
				long newsize = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(term, String.valueOf(newpointer)+ " " + String.valueOf(newsize));
				newpointer = rafnew.getFilePointer();

			}
		}


		raf.close();
		rafnew.close();

		File file = new File("C:\\Users\\AKI\\Downloads\\Test\\test\\new.txt");
		File file2 = new File("C:\\Users\\AKI\\Downloads\\Test\\test\\old.txt");
		file2.delete();
		file.renameTo(file2);


		// Copying New Catalog into Old Catalog
		catalogmap.putAll(newcatalogmap); 


	}

}


 */








public class ReadRAFTest {
	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("C:\\Users\\AKI\\workspace\\Ass2\\index.txt", "rw");

		/*raf.seek(163622887);
		byte[] b = new byte[15];
		raf.read(b, 0, 15);
		String ak = new String(b);
		System.out.println(ak);*/


		List<String> stoplist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Ass2\\catalog.txt"));
		Iterator<String> stritr = stoplist.iterator();
		while(stritr.hasNext())
		{
			String st = stritr.next();
			String[] a = st.split("-");
			String term = a[0];
			if (term!= null && term.equals("alleg"))
			{
				String[] posar = a[1].split(" ");
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(posar[0]);
				raf.seek(posint);
				byte[] bnew = new byte[size];
				raf.read(bnew, 0, size);
				String r = new String(bnew);
				System.out.println(r);
				String[] al = r.split(":");
				System.out.println(al.length - 1);
				
				/*ArrayList alnew = new ArrayList<>();
				for (int i = 1 ; i < al.length; al++)
				{
					
				}*/
			}
			
		}
	}
}