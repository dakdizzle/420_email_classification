

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/*
 * This class can sort hashmaps with the following k,v; <String, Double>,<BowDocument, Double>,<String, Integer>
 */
public class SortMap {
	
	/**
	 * Sorts double map
	 * @param aMap
	 * @return
	 */
	public static HashMap<String, Double> sortMapValueStringDouble(Map<String, Double> aMap) {
        
        Set<Entry<String,Double>> mapEntries = aMap.entrySet();       
                
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,Double>> aList = new LinkedList<Entry<String,Double>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,Double>>() {

            @Override
            public int compare(Entry<String, Double> ele1, Entry<String, Double> ele2) {                
                return ele1.getValue().compareTo(ele2.getValue());
            }
        });      
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        HashMap<String,Double> aMap2 = new LinkedHashMap<String, Double>();
        
        for(int i = aList.size()-1; i >= 0; i--){
        	aMap2.put(aList.get(i).getKey(), aList.get(i).getValue());
        }        
        return aMap2;       
    }
	
	/**
	 * Sorts double map
	 * @param aMap
	 * @return
	 */
	public static HashMap<BowDocument, Double> sortBowDoc(Map<BowDocument, Double> aMap) {
        
        Set<Entry<BowDocument,Double>> mapEntries = aMap.entrySet();       
                
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<BowDocument,Double>> aList = new LinkedList<Entry<BowDocument,Double>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<BowDocument,Double>>() {

            @Override
            public int compare(Entry<BowDocument, Double> ele1, Entry<BowDocument, Double> ele2) {                
                return ele1.getValue().compareTo(ele2.getValue());
            }
        });      
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        HashMap<BowDocument,Double> aMap2 = new LinkedHashMap<BowDocument, Double>();
        
        for(int i = aList.size()-1; i >= 0; i--){
        	aMap2.put(aList.get(i).getKey(), aList.get(i).getValue());
        }        
        return aMap2;       
    }
	
	/**
	 * Sorts double map
	 * @param aMap
	 * @return
	 */
	public static HashMap<String, Integer> sortMapByValuesInt(Map<String, Integer> aMap) {
        
        Set<Entry<String,Integer>> mapEntries = aMap.entrySet();       
                
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,Integer>> aList = new LinkedList<Entry<String,Integer>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,Integer>>() {

            @Override
            public int compare(Entry<String, Integer> ele1, Entry<String, Integer> ele2) {                
                return ele1.getValue().compareTo(ele2.getValue());
            }
        });      
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        HashMap<String,Integer> aMap2 = new LinkedHashMap<String, Integer>();
        
        for(int i = aList.size()-1; i >= 0; i--){
        	aMap2.put(aList.get(i).getKey(), aList.get(i).getValue());
        }        
        return aMap2;       
    }
}
