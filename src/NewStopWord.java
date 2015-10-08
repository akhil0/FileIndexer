import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.index.analysis.StopTokenFilterFactory;
import org.tartarus.snowball.ext.PorterStemmer;


public class NewStopWord {
	public static void main(String[] args) throws IOException {
		List<String> stoplist = Files.readAllLines(Paths.get("C:\\Users\\AKI\\workspace\\Elasticsearch\\stoplist.txt"));
		String[] sarray = {"_akhil", ")3", "3" , ".", "+"};
		List<String> listOfWord = new ArrayList<String>();
		List<String> listOfWords = new ArrayList<String>();
		listOfWord.addAll(Arrays.asList(sarray));
		Iterator<String> itr1 = listOfWord.iterator();
		
		while (itr1.hasNext())
		{
			String s = itr1.next();
			s = s.replace("[", "").replace("]", "").replace("(", "")
					.replace(")", "").replace("{", "").replace("}", "")
					.replace(",", "").replace(".", "").replace(":", "")
					.replace(";", "").replace(":", "").replace("?", "")
					.replace("!", "").replace("<", "").replace(">", "")
					.replace("%", "").replace("+", "").replace("_", "");
			listOfWords.add(s);
		
			
		}
		listOfWords = StopWordRemoval(listOfWords,stoplist);
		System.out.println(stoplist.size());
		System.out.println(stoplist.get(484));
		
		for (String s : listOfWords)
			System.out.println(s);
		//System.out.println("_akhil".replace("[(){},.;!?<>%]", ""));
	} 
	
	private static List<String> StopWordRemoval(List<String> slist, List<String> stoplist) {
		for(String str : stoplist)
		{
			slist.remove(str);
		}
		return slist;
	}

}
