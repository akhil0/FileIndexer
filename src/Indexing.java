import java.io.BufferedWriter;
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

public class Indexing {
	//public static HashMap<String, ArrayList> map;
	static int count,n = 0;
	static int docid = 1;
	public static void main(String[] args) throws IOException {

		HashMap<String,HashMap<Integer, ArrayList<Integer>>> wordmap = new HashMap<String,HashMap<Integer, ArrayList<Integer>>>();
		HashMap<String,Integer> doclenmap = new HashMap<String,Integer>();
		HashMap<String, String> newcatalogmap = new HashMap<String,String>();
		HashMap<String, String> catalogmap = new HashMap<String,String>();
		count = 0;
		// Reading DocIds and Ids into hashmap
		List<String> a = Files.readAllLines(Paths.get("C:\\Users\\AKI\\Downloads\\AP89_DATA\\AP_DATA\\doclist.txt"));
		System.out.println("Read DocList Success");
		HashMap<String, Integer> idmap = new HashMap<String, Integer>();
		Iterator stritr = a.iterator();
		while(stritr.hasNext())
		{
			String[] a1 = ((String) stritr.next()).split(" ");
			String id1 = a1[1];
			int id = Integer.valueOf(a1[0]);
			idmap.put(id1,id);
		}
		System.out.println("Read Doc & ID Map. Success");

		List<String> stoplist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\stoplist.txt"));
		System.out.println("Read Stoplist Success");

		Files.walk(Paths.get("C:\\Users\\AKI\\Downloads\\AP89_DATA\\AP_DATA\\ap89_collection")).forEach(filePath -> {
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
					int termpos = 0; 
					int doclen = 0;
					while (itr.hasNext()) { 
						String s = itr.next();

						//System.out.println(local);


						if (stoplist.contains(s)) {

						}
						else {
							PorterStemmer stemmer = new PorterStemmer();
							stemmer.setCurrent(s); //set string you need to stem
							stemmer.stem();
							String local = stemmer.getCurrent();
							if (wordmap.containsKey(local)) {
								HashMap<Integer, ArrayList<Integer>> tempmap = wordmap.get(local);
								docid = idmap.get(title);
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
							doclen++;
						}
						termpos++;

					}
					doclenmap.put(title,doclen);

					docid++;	
				}
				wordmap.remove("");
				count = count + 1;
				//System.out.println(count);

				if (count == 5){

					System.out.println(n);
					try {
						dumpmap(wordmap,catalogmap,newcatalogmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
					count = 0;
					Set<String> seto = wordmap.keySet();
					wordmap.keySet().removeAll(seto);
					n = n+1;
					/*System.out.println(wordmap.size());
					System.out.println(catalogmap.size());
					System.out.println(newcatalogmap.size());
					System.exit(0);*/
				}
			}
		});

		//System.out.println("alleg" + wordmap.get("alleg").toString());
		//System.out.println( wordmap.get("alleg").size());
		System.out.println(doclenmap.size());
		WritetoFile(doclenmap);
		System.out.println("Doc Lengths Success");

		Iterator<String> docitr = doclenmap.keySet().iterator();
		int sum = 0;
		while(docitr.hasNext())
		{
			String docid = docitr.next();
			sum = sum + doclenmap.get(docid);

		}
		System.out.println(sum/84678);

		WriteCatalogtoFile(catalogmap);
		System.out.println("Catalog Success");

	}

	private static void WriteCatalogtoFile(HashMap<String, String> catalogmap) throws IOException {
		File log = new File("C:\\Users\\AKI\\workspace\\Ass2\\catalog.txt");
		FileWriter fileWriter = new FileWriter(log, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		Iterator<String> lenitr = catalogmap.keySet().iterator();
		while(lenitr.hasNext()) {
			String id = lenitr.next();
			String len = catalogmap.get(id);
			bufferedWriter.write(id + "-" + len + "\n");
		}
		bufferedWriter.close();


	}

	private static void WritetoFile(HashMap<String, Integer> doclenmap) throws IOException {
		File log = new File("C:\\Users\\AKI\\workspace\\Ass2\\length.txt");
		FileWriter fileWriter = new FileWriter(log, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		Iterator<String> lenitr = doclenmap.keySet().iterator();
		while(lenitr.hasNext()) {
			String id = lenitr.next();
			int len = doclenmap.get(id);
			bufferedWriter.write(id + " " + len + "\n");
		}
		bufferedWriter.close();
	}

	private static void dumpmap(HashMap<String, HashMap<Integer, ArrayList<Integer>>> wordmap
			, HashMap<String, String> catalogmap, HashMap<String, String> newcatalogmap) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("C:\\Users\\AKI\\workspace\\Ass2\\index.txt", "rw");
		RandomAccessFile rafnew = new RandomAccessFile("C:\\Users\\AKI\\workspace\\Ass2\\new.txt", "rw");
		long newpointer = 0;
		Iterator<String> itr = wordmap.keySet().iterator();
		//Adding all new terms into new catalog
		while(itr.hasNext())
		{
			String s = itr.next();
			if(catalogmap.containsKey(s))
			{
				String pos = catalogmap.get(s);
				String[] posar = pos.split(" ");
				String loc = posar[0];
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(loc);
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
						str = str + "#" + sop ; 
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
						str = str + "#" + sop ; 
					}
				}
				rafnew.writeBytes(str);
				long size = rafnew.getFilePointer() - newpointer;				
				newcatalogmap.put(s, String.valueOf(newpointer)+ " " + String.valueOf(size));
				newpointer = rafnew.getFilePointer();
			}

		}

		Iterator<String> stritr = catalogmap.keySet().iterator();
		// Adding Terms present in old catalog but not in new
		while(stritr.hasNext())
		{
			String term = stritr.next();
			if(newcatalogmap.containsKey(term))
			{

			}
			else
			{
				String pos = catalogmap.get(term);
				String[] posar = pos.split(" ");
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(posar[0]);
				raf.seek(posint);
				byte[] bnew = new byte[size];
				raf.read(bnew, 0, size);
				String content = new String(bnew);
				rafnew.seek(newpointer);
				rafnew.writeBytes(content);
				long newsize = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(term, String.valueOf(newpointer)+ " " + String.valueOf(newsize));
				newpointer = rafnew.getFilePointer();

			}
		}


		raf.close();
		rafnew.close();

		File file = new File("C:\\Users\\AKI\\workspace\\Ass2\\new.txt");
		File file2 = new File("C:\\Users\\AKI\\workspace\\Ass2\\index.txt");
		file2.delete();
		file.renameTo(file2);


		// Copying New Catalog into Old Catalog
		catalogmap.putAll(newcatalogmap); 

		Set<String> seto = newcatalogmap.keySet();
		newcatalogmap.keySet().removeAll(seto);
	}

}


