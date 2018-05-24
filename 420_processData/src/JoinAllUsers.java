import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinAllUsers {

	private static BowDocument masterBdoc;
	
	public static void main(String[] args){
		String rootFolder = "enron_mail_20150507/Bdocs/";
		//String docLoc = rootFolder + "allen-p/allen-p-message.txt";
		
		File[] userFiles = (new File(rootFolder)).listFiles();
		
		int numUsers = userFiles.length;
		File [] uf = new File[numUsers];
		
		for(int i = 0; i < numUsers; i++)
			uf[i] = userFiles[i];
		
		
		masterBdoc = new BowDocument("masterBdoc");
		User[] users = populateUsers(uf);
		
		System.out.println();
		
		users[0].getMessage().displayDocInfo();

		masterBdoc.sort();
		writeMasterTable(users);
	}
	
	private static void writeMasterTable(User[] users){
		
		String bdocsFolder = ".\\enron_mail_20150507\\";
		String writeTo = bdocsFolder + masterBdoc.toString() + ".csv";
		
	    File f = new File(writeTo);
	    
	    String tab = ",";

		try {
			
			BufferedWriter writer = new BufferedWriter( new FileWriter(f));
			
			writer.write("NumWords"+ tab + masterBdoc.getNumWords());
			writer.newLine();
			writer.write("NumTerms" + tab + masterBdoc.getNumTerms());
			writer.newLine();
			writer.newLine();
			
			String tableHeadings = "terms" + tab;
			
			for(User user : users)
				tableHeadings += user.getName() + tab;
			
			tableHeadings += "TotalNumInDataSet";
			writer.write(tableHeadings);
			writer.newLine();
			
			
			for(String term : masterBdoc.getTermList()){
				
				String line = term + tab;

				
				for(User user : users){		
					int tc = user.getMessage().getTermCount(term);
					line += tc + tab;
				}
				
				line += masterBdoc.getTermCount(term);
				
				writer.write(line);
				writer.newLine();				
			}
		    
		    writer.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private static User[] populateUsers(File[] userFiles){
		//only setting message docs for now...
		User[] users = new User[userFiles.length];
		
		for(int i = 0; i < userFiles.length; i++){
			String userName = userFiles[i].getName();
			File[] userDocs = userFiles[i].listFiles();
			File messageDoc = null;
			
			for(File d : userDocs){
				if(d.getName().contains("message"))
					messageDoc = d;
			}
			
			BowDocument bdoc = null;				
			
			try {
				bdoc = makeBdocFromMap(readBdocFile(messageDoc), messageDoc.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}				
			
			users[i] = new User(userName);
			users[i].setMessage(bdoc);
			
			
		}		
		
		return  users;
	}	
	
	private static HashMap<String, Integer> readBdocFile(File doc) throws Exception{
		HashMap<String, Integer> map;
		if(!doc.isDirectory()){		
			
			map = new HashMap<String, Integer>();
			
			int firstTermLine = 3;
			ArrayList<String> lines = getDocLines(doc);
			
			int numLinesWanted = 40; // get top 50 terms
			
			for(int i = firstTermLine; i < numLinesWanted + firstTermLine && i < lines.size(); i++){
				String tab = "\t";
				String regex = tab;
			
				if(lines.get(i).contains(tab+tab)){
					regex += tab;
				}
				
				String[] termFreqPair= lines.get(i).split(regex);
				
							
				for(String s : termFreqPair){
					s = s.trim();
				}
				if(termFreqPair.length != 2)
					System.out.println();
				int freq = Integer.parseInt(termFreqPair[1]);
				map.put(termFreqPair[0], freq);
			}		
			
		}else{
			throw new Exception("Input file is a directory");
		}
		
		return  map;
	}
	
	/**
	 * 
	 * @param location directory path of  file
	 * @return 
	 */
	public static ArrayList<String> getDocLines(
			File file){
		
		String thisLine = null;
		ArrayList<String> lines = new ArrayList<>();
		
		Pattern p = Pattern.compile("[a-z0-9]");
		
		Matcher matcher;
		
		try {
		     BufferedReader br = new BufferedReader(new FileReader(file));
		     while ((thisLine = br.readLine()) != null) {
		    	 matcher = p.matcher(thisLine);
		    	 if(matcher.find())
		    		 lines.add(thisLine);	   
		     }
		     br.close();
		  } catch(Exception e) {
		     e.printStackTrace();
		  }
		return lines;
	}
	
	private static BowDocument makeBdocFromMap(HashMap<String, Integer> map, String userName){
		
		BowDocument doc = new BowDocument(userName);
		
		ArrayList<String> l = new ArrayList<>();
		
		for(String term : map.keySet()){
			for(int i = 0; i < map.get(term); i++)	
				l.add(term);
		}
		
		for(String t : l){
			masterBdoc.addTerm(t);
			doc.addTerm(t);
		}
		return doc;
	}
	
	private User makeUser(String userName, BowDocument sent, 
			BowDocument subject, BowDocument message){
		
		User user = new User(userName);
		user.setSentTo(sent);
		user.setSubject(subject);
		user.setMessage(message);
		return user;
	}
}
