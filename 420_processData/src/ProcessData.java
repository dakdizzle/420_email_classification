import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessData {
	
	private static final String STOP_WORDS_FILE = "./stopWords.txt";
	private static final int NUM_PARTS_IN_EMAIL = 3;
	private static final String[] STOPWORDS = getStopWords(STOP_WORDS_FILE);
	private static final String[] DOC_TYPES = {"-sentTo", "-subject", "-message"};
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	

	
	//Note: deleted 1103 from presto-k and 158 from forney-j because
	//		invlaid email ... also was 1000+KB rather than under5KB
	
	public static void main(String args[]) throws Exception{		
		String rootFolderLocation = "enron_mail_20150507/maildir/";
		PreProcessor stemmer = new PreProcessor();
		File rootFolder = new File(rootFolderLocation);	
		File[] users = rootFolder.listFiles();	
		
/*
		for(File user : users){			
			//System.out.println("Processing : " + user.getName());
			File[] userEmails = getUserEmails(user);
			preProcessFully(userEmails, stemmer, user.getName(), STOPWORDS);			
		}
		*/
		
		emptyFiles(new File("enron_mail_20150507/Bdocs-by-email/").listFiles());
		
		
		//writeAllEmails(users, stemmer, STOPWORDS);
	}
	
	
	static void emptyFiles(File[] users){
		for(File user : users){
			for(File doc : user.listFiles()){
				try {
					BufferedReader br = new BufferedReader(new FileReader(doc));
					
					String currentLine = br.readLine();				
					
					if(currentLine.contains("0")){
						System.out.println(doc.getAbsolutePath());
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	
	
	/**
	 * Get an array containing all of the files representing a users emails
	 * from the 'sent_items' folder within the inputed user parent folder
	 * @param user user parent folder containing 'sent_items' folder which
	 * contains the email files
	 * @return Array of files representing emails. Note: this is All
	 * files in the 'sent_items' directory, including any folders
	 * which mau appear
	 * @throws Exception throws exception if there is no 'sent_items' folder
	 */
	static File[] getUserEmails(File user) throws Exception{
		File sentItemsFolder = null;
		File[] userFolders = user.listFiles();
		
		for(File file : userFolders){
			if(file.getName().equals("sent_items")){
				sentItemsFolder = file;
				break;
			}
		}
		if(sentItemsFolder == null)
			throw new Exception("sent_items folder not found");
		
		return sentItemsFolder.listFiles();
	}
	
	
	static void writeAllEmails(File[] users, PreProcessor stemmer, String[] stopWords){
		for(File user : users){
			String userName = user.getName();
			BowDocument[] docs = null;
			try {
				docs = preprocessEmailsForUser(getUserEmails(user), stemmer, userName, stopWords);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(BowDocument doc : docs){
				if(!(doc == null)){
					doc.sort();
					writeBdocToFile(doc, userName, "Bdocs-by-email");
				}
			}
		}
		
	}
	
	
	static BowDocument[] preprocessEmailsForUser(File[] userEmails,
			PreProcessor stemmer, String userName, String[] stopWords){
		BowDocument[] docs = new BowDocument[userEmails.length];
		
		for(int i = 0; i < userEmails.length; i++){

			String[] parts = getEmailParts(userEmails[i]);
			ArrayList<String> messageVocab = null;
			try {
				messageVocab = cleanMessageForUser(parts[4], stopWords, stemmer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(userName + userEmails[i]);
				e.printStackTrace();
			}
			docs[i] = new BowDocument(userEmails[i].getName());
			for(String term : messageVocab){
				docs[i].addTerm(term);
			}
			if(docs[i].getNumWords() <= 0){
				docs[i] = null;
			}
		}
		return docs;
	}

	/**
	 * 
	 * @param userEmails 
	 * @param stemmer
	 * @param userName
	 * @param stopWords
	 */
	static void preProcessFully(File[] userEmails,
				PreProcessor stemmer, String userName, String[] stopWords){

		//get email parts
		ArrayList<ArrayList<String>> emailsParts = readAndSeperateEmails(userEmails);
		
		//clean email parts
		emailsParts = cleanAndPreprocessParts(emailsParts, stemmer);

		//process in to bdocs		
		String[] docIds = new String[NUM_PARTS_IN_EMAIL];
		
		for(int i = 0; i < NUM_PARTS_IN_EMAIL; i++)
			docIds[i] = userName + DOC_TYPES[i];
		
		BowDocument[] docs = makeBdocs(emailsParts, docIds);
		
		
		//sort and write bdocs to file
		for(BowDocument doc : docs)
			sortBdocAndWriteToFile(doc, userName);
		
		System.out.println("Completed : " + userName);		
	}
	
	
	
	/*********************************** BowDocument interaction ***********************/
	
	/**
	 * Sorts inputed BowDocument object by frequency of terms then writes
	 * it allong with numWords and numTerms to a .txt
	 * @param doc BowDocument to sort and write
	 * @param userName name of user who bdoc belongs to 
	 */
 	private static void sortBdocAndWriteToFile(BowDocument doc, String userName){
		doc.sort();
		writeBdocToFile(doc, userName, "Bdocs");
	}
	
	/**
	 * Make BowDocument form inputted terms which have been preprocessed
	 * @param terms ArrayList<String>  of preprocessed terms
	 * @param docName name to assign to objcet
	 * @return populated bow document
	 */
	static BowDocument makeBdoc(ArrayList<String> terms, String docName){
		BowDocument doc = new BowDocument(docName);
		for(String term : terms)
			doc.addTerm(term);
		return doc;
	}
	
	static BowDocument[] makeBdocs(ArrayList<ArrayList<String>> cleanedEmailParts,
			String[] docIDs){
		BowDocument[] docs = new BowDocument[NUM_PARTS_IN_EMAIL];
		for(int i = 0; i < NUM_PARTS_IN_EMAIL; i++){
			docs[i] = makeBdoc(cleanedEmailParts.get(i), docIDs[i]);
		}
		return docs;
	}
	

	
	/*********************************** Cleaning ***************************************/
	
	static ArrayList<ArrayList<String>> cleanAndPreprocessParts(ArrayList<ArrayList<String>> parts,
			PreProcessor stemmer){
		//clean email parts
		ArrayList<String> sentTo = cleanSentTo(parts.get(0));
		ArrayList<String>  subjectTerms = cleanSubject(parts.get(1), STOPWORDS, stemmer);		
		ArrayList<String>  messageTerms = cleanAllMessagesForUser(parts.get(2), STOPWORDS, stemmer);
		
		parts.clear();
		parts.add(sentTo);
		parts.add(subjectTerms);
		parts.add(messageTerms);
		return parts;
	}
	
	/**
	 * Test if an inputed string has eamil address formatting
	 * @param address String - email address to be tested
	 * @return True if the inputed address if has email address formatting
	 * false if otherwise
	 */
	static boolean emailAddressIsValid(String address){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(address);
        return matcher.find();
	}	
	
	
	/**
	 * Remove email line tags then check that all non null entries are of a
	 * valid email format. If they are then add them to the returned array list
	 * @param sentTo raw strings (to,cc,bcc) from email files
	 * @return cleaned to strings in an arraylist
	 */
	static ArrayList<String> cleanSentTo(ArrayList<String> sentTo){		
		
		ArrayList<String> clean = new ArrayList<String>();
		
		for(int i = 0; i < sentTo.size(); i++){
			String current = sentTo.get(i);
			
			if(current != null){
				current = removeLineTitle(current);
				
				if(emailAddressIsValid(current))
					clean.add(current);
			}
		}		
		return clean;
	}
	

	static ArrayList<String> cleanSubject(ArrayList<String> subject,
								String[] stopWords, PreProcessor stemmer){
		
		ArrayList<String> subjectVocab = new ArrayList<String>();
		
		for(int i = 0; i < subject.size(); i++){
			String current = subject.get(i);
			current = removeLineTitle(current);
			
			ArrayList<String> splitSubject = cleanSplitStopStemString(current, stopWords, stemmer);			
			subjectVocab.addAll(splitSubject);
		}	
		
		return subjectVocab;		
	}
	
	/**
	 * Remove line titles e.g "to:". Only works of title end with a ':'.
	 * Then removes leading white space.
	 * @param s
	 * @return
	 */
	static String removeLineTitle(String s){
		String regex = ":";
		if(s.contains(regex)){			
			s = s.substring(s.indexOf(regex) + 1); //get rid of space also
			s = s.trim();
		}
		return s;
	}
	
	/**
	 * 
	 * @param messages
	 * @param stopWords
	 * @param stemmer
	 * @return
	 */
	static ArrayList<String> cleanAllMessagesForUser(ArrayList<String> messages, String[] stopWords, PreProcessor stemmer){
		
		ArrayList<String> messageVocab = new ArrayList<String>();
		
		for(String message : messages){
			try {
				messageVocab.addAll(cleanMessageForUser(message,stopWords, stemmer));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		return messageVocab;
	}
	
	static ArrayList<String> cleanMessageForUser(String message, String[] stopWords, PreProcessor stemmer) throws Exception{
		String endMessageFlag = "-----Original Message-----";
		
		if(message != null){
			if(message.contains(endMessageFlag))
				message = message.substring(0, message.indexOf(endMessageFlag));
			return cleanSplitStopStemString(message,stopWords, stemmer);
		}else{
			throw new Exception("Input message is null for :");
		}
	}
	
	
	static ArrayList<String> cleanSplitStopStemString(String s, String[] stopWords, PreProcessor stemmer){
		
		s = PreProcessor.removeNonAlphabeticalChars(s);
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(s.split(" ")));
		list = PreProcessor.removeStopWords(stopWords, list);
		return stemmer.stemTerms(list);
	}
	
	
	/*********************************** File read/write ***************************************/

	/**
	 * Read all emails in inputed file array, then separate them in to 
	 * 3 arrayLists: to, subject, message. These are then put in to 
	 * an  ArrayList<ArrayList<String>> in the above order. This also 
	 * filters out any folders in the inputted array.
	 * @param emails File[] of emails
	 * @return ArrayList<ArrayList<String>> where [0] = to, [1] = subject,
	 * [2] = message
	 */
	static ArrayList<ArrayList<String>> readAndSeperateEmails(File[] emails){
		ArrayList<String> to = new ArrayList<String>();
		ArrayList<String> subject = new ArrayList<String>();
		ArrayList<String> message = new ArrayList<String>();
		
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		
		//seperate
		for(File email : emails){
			
			if(!email.isDirectory()){
				String[] parts = getEmailParts(email);
				to.add(parts[0]);
				to.add(parts[2]);
				to.add(parts[3]);
				subject.add(parts[1]);
				message.add(parts[4]);
			}
		}
		
		list.add(to);
		list.add(subject);
		list.add(message);
		
		return list;
	}
	
	/**
	 * Returns str array where 0 = to, 1 = subject,2 = cc, 3 = bcc,
	 * 4 = message 
	 * @param email
	 * @return
	 */
	static String[] getEmailParts(File email){		
		
		
		
		
		int numParts = 5;
		String emailStrings[] = new String[numParts];
		BufferedReader br = null;
		String currentLine = "";
		
		int emailIndex = 0;
		
		int[] linesNeeded = {2, 3, 8,9,10};
		
		int messageLine = 16;
		
		try {
			br = new BufferedReader(new FileReader(email));
			
			int lineNum = 0;
			while((currentLine = br.readLine()) != null){				
				
				boolean needLine = false;
				
				if(lineNum >= messageLine){
					needLine = true;
				}else{
					for(int neededLine : linesNeeded){
						if(lineNum == neededLine){
							needLine = true;
							break;
						}
					}	
				}				
				
				if(needLine){		
					if(emailIndex < numParts-1){						
						emailStrings[emailIndex] = currentLine;
						emailIndex++;
					}else{
						if(emailStrings[emailIndex] == null)
							emailStrings[emailIndex] = "";
						emailStrings[emailIndex] += currentLine;
					}
				}
				lineNum++;
			}		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return emailStrings;
	}
	
	/**
	 * Write a bdoc to a file
	 * @param doc
	 * @param userName
	 */
	static void writeBdocToFile(BowDocument doc, String userName, String folderName){
		String bdocsFolder = ".\\enron_mail_20150507\\" + folderName + "\\";
		String writeTo = bdocsFolder + userName + "\\";
		
		Path path = Paths.get(writeTo);
		
		if(Files.notExists(path))
			new File(writeTo).mkdir();
		
	    File f = new File(writeTo + doc.toString() + ".txt");
	    //System.out.println(f.getAbsolutePath());
		try {
			
			BufferedWriter writer = new BufferedWriter( new FileWriter(f));
			
			writer.write("NumWords\t" + doc.getNumWords());
			writer.newLine();
			writer.write("NumTerms\t" + doc.getNumTerms());
			writer.newLine();
			writer.newLine();
			
			for(String term : doc.getTermList()){
				String tab = "\t";
				if(term.length() <= 7)
					tab += tab;
				
				writer.write(term + tab + doc.getTerms().get(term));
				writer.newLine();				
			}
		    
		    writer.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list of stop words contained in a 	comma separated file
	 * @param file containing the stop words
	 * @return String array of stop words
	 */
	public static String[] getStopWords(
			String file){
		
		String rawString = getDocString(new File(file));
		return rawString.split(",");
	}
	
	/**
	 * 
	 * @param location directory path of  file
	 * @return single String with all raw text from the file in it
	 */
	private static String getDocString(
			File file){
		
		String thisLine = null;
		String line = "";
		
		try {
		     BufferedReader br = new BufferedReader(new FileReader(file));
		     while ((thisLine = br.readLine()) != null) {
	    		 line += thisLine;	   
		     }
		     br.close();
		  } catch(Exception e) {
		     e.printStackTrace();
		  }
		return line;
	}
	
	
	/***************************************** Delete folders ************************************************/
	/***
	 * Delete all folder and files that are deemed to be unnecessary
	 * @author harry
	 *
	 */
	private class RemoveUnWantedFolders{
		/**
		 * Delete all folders and files for each user in the rootFolder
		 * except those specified in deletionExceptions input
		 * @param rootFolder folder of users
		 * @param deletionExceptions String[] of folder names to keep
		 */
		void deleteFolders(File rootFolder, String[] deletionExceptions){
			
			for(File userFolder : rootFolder.listFiles()){			
				for(File indivUserFile : userFolder.listFiles()){
					
					boolean deletionFlag = true;
					for(String exeptString : deletionExceptions){
						if(indivUserFile.getName().equals(exeptString)){
							deletionFlag = false;
							break;
						}
					}
					
					if(deletionFlag)
						deleteFolder(indivUserFile);
				}
				
				//if user didnt hvae any of the excempted deletion folders
				//delete the user
				if(userFolder.listFiles().length == 0)
					userFolder.delete();
			}	
		}
		
		/**
		 * First delete all files and folders with in inputed folder
		 * Then delete inputed folder.
		 * @param folder folder to delete
		 */
		void deleteFolder(File folder){
			File[] files= folder.listFiles();
			
			if(files != null){
				for(int i = 0; i < files.length; i++){			
					if(files[i].isDirectory()){
						deleteFolder(files[i]);
					}else{
						files[i].delete();
					}			
				}
			}
			folder.delete();
		}	

	}
}









/*********************************** CSV stuff / graveyard***************************************/
/**
 * 
 * @param emails
 * @param u
 * @return
 */
/*
static String[] makeCsvStrings(File[] emails, String u){
	
	ArrayList<String> to = new ArrayList<String>();
	ArrayList<String> subject = new ArrayList<String>();
	ArrayList<String> message = new ArrayList<String>();
	
	//seperate
	for(File email : emails){
		
		if(!email.isDirectory()){
			String[] parts = getEmailParts(email);
			to.add(parts[0]);
			to.add(parts[2]);
			to.add(parts[3]);
			subject.add(parts[1]);
			message.add(parts[4]);
		}
	}
	
	
	ArrayList<ArrayList<String>> emailParts = readAndSeperateEmails(emails);
	
	//clean
	to = cleanSentTo(to);
	
	
	
	
	for(String s : subject)
		s = removeLineTitle(s);
	
	
	
	String endMessageFlag = "-----Original Message-----";		
	
	ArrayList<String> tempMessage = new ArrayList<>();
	
	for(int i = 0; i < message.size(); i++){		
		String m = message.get(i);
		if(m != null){
			if(m.contains(endMessageFlag))
				m = m.substring(0, m.indexOf(endMessageFlag));
			tempMessage.add(m);
		}else{
			tempMessage.add(" ");
		}
	}
	message = tempMessage;
	
	//join
	
	String[] csv = new String[emails.length];

	
	for(int i = 0; i < bcc.size(); i++){
		csv[i] = to.get(i) + "," + bcc.get(i) + "," + cc.get(i) + "," 
				+ subject.get(i) + "," + message.get(i);			
	}
	return csv;
}


static void writeCVSToFile(String[] emailCSVs, String user){
	
    File f = new File(".\\enron_mail_20150507\\csv\\" + user + ".txt");
    System.out.println(f.getAbsolutePath());
	try {
		
		BufferedWriter writer = new BufferedWriter( new FileWriter(f));
		for(String email : emailCSVs){
			if(email != null){
				writer.write(email);
			    writer.newLine();
			}
			
		}
	    
	    writer.close();
	    
	} catch (IOException e) {
		e.printStackTrace();
	}
}*/
