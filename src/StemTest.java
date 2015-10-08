import org.tartarus.snowball.ext.PorterStemmer;




public class StemTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		String[] words = {"all", "akhils", "this", "is", "a" , "tests", "allegations"};
		String text = String.join(" ", words);
		PorterStemmer stemmer1 = new PorterStemmer();
		stemmer1.setCurrent(text); //set string you need to stem
		stemmer1.stem();  //stem the word
		System.out.println(stemmer1.getCurrent());//get the stemmed word
		System.out.println(text);
		for (String s : words)
		{
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(s); //set string you need to stem
			stemmer.stem();  //stem the word
			s= stemmer.getCurrent();//get the stemmed word
		
		}
		for (String s : words)
		{
			System.out.println(s);
		
		}

		/*PorterStemmer stemmer = new PorterStemmer();
		stemmer.setCurrent(text); //set string you need to stem
		stemmer.stem();  //stem the word
		System.out.println(stemmer.getCurrent());//get the stemmed word
*/	
		}
	

}
