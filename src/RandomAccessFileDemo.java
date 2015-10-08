/*import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class RandomAccessFileDemo {

	public static void main(String[] args) throws IOException {
		String[] a = {"aki", "rev", "maha", "abhi", "vig", "sumit", "mohit"};
		HashMap<String, String> catalogmap = new HashMap<String,String>();
		RandomAccessFile raf = new RandomAccessFile("c:/old.txt", "rw");
		// create a new RandomAccessFile with filename Example
		long pointer = 0;
		for(String s : a)
		{
			raf.seek(pointer);
			raf.writeBytes(s z);
			long size = raf.getFilePointer() - pointer;


			catalogmap.put(s, String.valueOf(pointer)+ " " + String.valueOf(size));

			pointer = raf.getFilePointer();
		}


		Iterator<String> itr = catalogmap.keySet().iterator();


		HashMap<String, String> newcatalogmap = new HashMap<String,String>();

		String[] s2 = {"rev", "prav",  "aki","himi", "niki"};
		//ArrayList<String> newlist = new ArrayList<String>();
		//newlist.addAll(Arrays.asList(s2));

		RandomAccessFile rafnew = new RandomAccessFile("c:/new.txt", "rw");
		long newpointer = 0;

		//Adding all new terms into new catalog
		for(String s : s2)
		{
			if(catalogmap.containsKey(s))
			{
				String pos = catalogmap.get(s);
				String[] posar = pos.split(" ");
				String loc = posar[0];
				long posint = Long.valueOf(loc);
				raf.seek(posint);
				String content = raf.readLine().replaceAll("\n", "") + s;
				rafnew.seek(newpointer);
				rafnew.writeBytes(content + "\n");
				long size = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(s, String.valueOf(newpointer)+ " " + String.valueOf(size));
				newpointer = rafnew.getFilePointer();

			}
			else
			{
				rafnew.seek(newpointer);
				rafnew.writeBytes(s + "\n");
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
				long posint = Long.valueOf(loc);
				raf.seek(posint);
				String content = raf.readLine().replaceAll("\n", "");
				rafnew.seek(newpointer);
				rafnew.writeBytes(content + "\n");
				long size = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(term, String.valueOf(newpointer)+ " " + String.valueOf(size));
				newpointer = rafnew.getFilePointer();

			}
		}


		raf.close();
		rafnew.close();


		//Copying New File into Old File
		File file = new File("c:/new.txt");
		File file2 = new File("c:/old.txt");
		file2.delete();
		file.renameTo(file2);


		// Copying New Catalog into Old Catalog
		System.out.println(newcatalogmap.size());
		catalogmap.putAll(newcatalogmap);
		System.out.println(catalogmap.size());


		// Printing Catalog 
		RandomAccessFile raf2 = new RandomAccessFile("c:/old.txt", "rw");
		Iterator<String> stritr = catalogmap.keySet().iterator();
		while(stritr.hasNext())
		{
			String term = stritr.next();
			String pos = catalogmap.get(term);
			String[] posar = pos.split(" ");
			String loc = posar[0];
			long posint = Long.valueOf(loc);
			raf2.seek(posint);
			System.out.println(raf2.readLine() + " - " + posint + " - " + posar[1]);
		}


		//Using byte Array to store the data
		//byte[] b = new byte[(int)raf2.length()] ;
		byte[] b = new byte[5] ;
		raf2.seek(7);
		raf2.read(b, 0, 5);
		//System.out.println(raf2.readLine());
		String so = new String(b);
		System.out.println(so + "test");

		raf2.close();



	}
}*/

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class RandomAccessFileDemo {

	public static void main(String[] args) throws IOException {
		String[] a = {"aki", "rev", "maha", "abhi", "vig", "sumit", "mohit"};
		HashMap<String, String> catalogmap = new HashMap<String,String>();
		RandomAccessFile raf = new RandomAccessFile("c:/old.txt", "rw");
		// create a new RandomAccessFile with filename Example
		long pointer = 0;
		for(String s : a)
		{
			raf.seek(pointer);
			raf.writeBytes(s);
			long size = raf.getFilePointer() - pointer;


			catalogmap.put(s, String.valueOf(pointer)+ " " + String.valueOf(size));

			pointer = raf.getFilePointer();
		}


		Iterator<String> itr = catalogmap.keySet().iterator();


		HashMap<String, String> newcatalogmap = new HashMap<String,String>();

		String[] s2 = {"rev", "prav",  "aki","himi", "niki"};
		//ArrayList<String> newlist = new ArrayList<String>();
		//newlist.addAll(Arrays.asList(s2));

		RandomAccessFile rafnew = new RandomAccessFile("c:/new.txt", "rw");
		long newpointer = 0;

		//Adding all new terms into new catalog
		for(String s : s2)
		{
			if(catalogmap.containsKey(s))
			{
				String pos = catalogmap.get(s);
				String[] posar = pos.split(" ");
				String loc = posar[0];
				int size = Integer.valueOf(posar[1]);
				long posint = Long.valueOf(loc);
				raf.seek(posint);
				byte[] bnew = new byte[s.length()];
				raf.read(bnew, 0, size);
				String r = new String(bnew);
				String content = r + s;
				rafnew.seek(newpointer);
				rafnew.writeBytes(content);
				long newsize = rafnew.getFilePointer() - newpointer;
				newcatalogmap.put(s, String.valueOf(newpointer)+ " " + String.valueOf(newsize));
				newpointer = rafnew.getFilePointer();

			}
			else
			{
				rafnew.seek(newpointer);
				rafnew.writeBytes(s);
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
				byte[] bnew = new byte[term.length()];
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


		//Copying New File into Old File
		File file = new File("c:/new.txt");
		File file2 = new File("c:/old.txt");
		file2.delete();
		file.renameTo(file2);


		// Copying New Catalog into Old Catalog
		System.out.println(newcatalogmap.size());
		catalogmap.putAll(newcatalogmap);
		System.out.println(catalogmap.size());


		// Printing Catalog 
		RandomAccessFile raf2 = new RandomAccessFile("c:/old.txt", "rw");
		Iterator<String> stritr = catalogmap.keySet().iterator();
		while(stritr.hasNext())
		{
			String term = stritr.next();
			String pos = catalogmap.get(term);
			String[] posar = pos.split(" ");
			String loc = posar[0];
			long posint = Long.valueOf(loc);
			int size = Integer.valueOf(posar[1]);
			System.out.println(term + " : " + loc + " - " + size);
			raf2.seek(posint);
			/*byte[] bnew = new byte[term.length()];
			raf2.read(bnew, 0, size);
			String content = new String(bnew);
			System.out.println(content + " - " + posint + " - " + posar[1]);*/
		}


		raf2.close();
		
		List<String> list = Arrays.asList("one", "two", "three", "four", "five", "six");
		
		list.stream().forEach(s -> System.out.println(s));


	}
}