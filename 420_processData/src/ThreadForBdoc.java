import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadForBdoc implements Runnable{

	private static final String STOP_WORDS_FILE = "./stopWords.txt";
	
	private Thread thread;
	private File[] userEmails;
	private PreProcessor stemmer;
	private String userName;
	private String[] stopWords;
	
	ThreadForBdoc(File[] _userEmails, String _userName){
		userEmails = _userEmails;
		userName = _userName;
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting : " + userName);
		ProcessData.preProcessFully(userEmails, stemmer, userName, stopWords);
		
		System.out.println("Completed: " + userName);
	}

	
	
	public void start () {
		 if (thread == null) {
			 thread = new Thread(this);
			 stemmer = new PreProcessor();
			 stopWords = ProcessData.getStopWords(STOP_WORDS_FILE);
			 thread.start ();
		 }
	}
	
	public Thread getThread(){
		return thread;
	}	
	
	
	public static void main(String args[]) throws Exception{
		
		int numThreads = 2;
		
		//ThreadForBdoc[] threads= new ThreadForBdoc[numThreads];
		
		String rootFolderLocation = "enron_mail_20150507/maildir/";
		File rootFolder = new File(rootFolderLocation);	
		File[] users = rootFolder.listFiles();	
		
		File[] users1 = new File[users.length/20];
		
		for(int i = 0; i < users1.length; i++){
			users1[i] = users[i];
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		
		for(File user : users1){
			System.out.println("Submitting : " + user.getName());
			executor.submit(new ThreadForBdoc(ProcessData.getUserEmails(user), user.getName()));			
		}
		System.out.println("All tasks submited");
		executor.shutdown();
		
		executor.awaitTermination(1, TimeUnit.DAYS);

		//System.out.println("All tasks completed");
		
	}
}
