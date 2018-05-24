


import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author harry
 * @since 05/06/2017
 * 
 * This is the class for the bag of words documents.
 * It contains a hashmap of where the key is a term (String) and the value is the number of 
 * Occurrences of the term in the document.
 */
public class BowDocument {
	
	private String documentID;		
	private HashMap<String, Integer> terms;	
	private static int totalDocLength = 0;	
	private int numWords, numTerms;
	
	
	 /**
	 * Constructor
	 * Set the ID of the document, and initiate an empty term:frequency map.
	 * call addTerm to add terms to map
	 * @param docId
	 */
	BowDocument(String _documentID){	
		
		documentID = _documentID;
		terms = new HashMap<>();
		numWords = 0;
		numTerms = 0;
	}

	 /**
	  * sets number of words for the document
	  * @param _numWords number f words in doc
	  */
	 public void setNumWords(int _numWords){
		 numWords = _numWords;
	 }	
	
	/**
	 *
	 * @param term
	 * @return the term occurrence count for the given term
	 * return 0 if the term does not appear in the document
	 */
	public int getTermCount(String term){	
		
		if(terms.containsKey(term)){
			return terms.get(term);
		}
		return 0;
	}
	
	/**
	 * 
	 * @return number of terms in document
	 */
	public int getNumTerms(){
		return numTerms;
	}
			 
	 /**
	  * @return map of term:freq pairs.
	  */
	 public HashMap<String, Integer> getTerms(){
		 return terms;
	 }
	
	 /**
	  * get the document ID
	  * @return documnetID
	  */
	public String toString(){
		return documentID;
	}
	
	/**
	 * Get an arraylist that contains all of the terms with in the BOWDoc
	 * @return ArrayList<String> 
	 */
	public ArrayList<String> getTermList(){
		
		ArrayList<String> termsList = new ArrayList<>();
		termsList.addAll(terms.keySet());
		return termsList;
	}
	
	/**
	 * checks if input terms exists in bowDoc
	 * @param term term to check
	 * @return true if terms exists, false in not
	 */
	public boolean containsTerm(String term){
		if(terms.containsKey(term)){
			return true;
		}
		return false;
	}
	 /**
	 * Add a term occurrence to the BOW representation
	 * @param term preprocessed  term to add to doc terms
	 */
	public void addTerm(String term){
		
		if(!terms.containsKey(term)){
			terms.put(term, 1);
			numTerms++;
		}else{
			terms.put(term, terms.get(term) + 1);
		}
		numWords++;
		totalDocLength++;
	}
	
	/**
	 * Prints the map (both term and term count) 
	 */
	private void printTerms(){
		
		for(String key : terms.keySet()){
			System.out.println(key + " : " + terms.get(key));
		}
		System.out.println("\n");
	}
	
	/**
	 * prints doc description with map
	 */
	 public void  displayDocInfo(){
		 
		 System.out.println(documentID + " contains " + numTerms + " terms and has " + numWords + " words.");
		 printTerms();
	 }
	 
	 //DELETE?
	 /**
	  * Accessor for docLength
	  * @return
	  */
	 public int getTotalDocLength(){
		 return totalDocLength;
	 }
	 
	 /**
	  * Get the number of words that have been added to the document
	  * @return Number of words in document
	  */
	 public int getNumWords(){
		 return numWords;
	 }
	 
	 public void sort(){
		 terms = SortMap.sortMapByValuesInt(terms);
	 }
}
