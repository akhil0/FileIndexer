import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class doclen {

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
		
		
		Iterator<Integer> itr = idmap.keySet().iterator();
		double sum = 0;
		while(itr.hasNext())
		{
			String id = idmap.get(itr.next());
			
			sum = sum + lengthmap.get(id);
		}
		
		System.out.println(sum/84678);

	}

}
