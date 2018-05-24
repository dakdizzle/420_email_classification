import java.util.ArrayList;

import org.tartarus.snowball.SnowballStemmer;

public class PreProcessor {
	
	
	//Snowball stemmer that will be used
			private static SnowballStemmer stemmer;
	
	/**
	 * Class constructor
	 */
	public PreProcessor(){
		
		//Initialize stemmer
		 Class<?> stemClass;
		try {
			stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
			stemmer = (SnowballStemmer)stemClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		 
	}
	
	/**
	 * Removes all words in stop words array from terms array list
	 * @param stopWords String array of stop words
	 * @param terms	String array list of terms
	 * @return terms array list with all words from stopWords removed
	 */
	static ArrayList<String> removeStopWords(
			String[] stopWords, 
			ArrayList<String> terms){
		
		for(String stopWord : stopWords){
			while(terms.contains(stopWord)){
				terms.remove(stopWord);
			}
		}
		return terms;
	}
	
	/**
	 * Removes all non a-z characters, makes string lower case, adds spacing around occurrences of "quot"
	 * @param doc String containing text section of document
	 * @return String containing only lowercase a-z text  
	 */
	static String removeNonAlphabeticalChars(
			String doc){	
		
	    String temp  = doc.toLowerCase();		
		
		temp = temp.replaceAll("[^a-z ]", " ");		
		temp = temp.replaceAll("quot", " quot ");
		temp = temp.replaceAll("-", " ");
		temp = temp.replaceAll("[.]", " ");
		temp = temp.replaceAll(" [a-z] ", " ");
		
		return temp;
	}
	
	public ArrayList<String> stemTerms(ArrayList<String> terms){
		for(String term : terms){
			try {
				term = stemTerm(term);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return terms;
	}
			
			
	 /**
	 *Stems a inputed term using the snowball tartarus stemmer
	 * @param term a String
	 * @param stemmer to be used
	 * @return a stemmed form of given word
	 * @throws Exception 
	 */
	static String stemTerm(
			String term) throws Exception {		
		if(stemmer != null){
			stemmer.setCurrent(term);		
			stemmer.stem();
		}else{
			throw new Exception("Stemmer not initialised");
		}
		
		
		return stemmer.getCurrent();
	}	 
}
