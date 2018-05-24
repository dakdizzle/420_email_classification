import java.util.HashMap;

public class User {
	private String name;
	private BowDocument sentTo;
	private BowDocument subject;
	private BowDocument message;
	
	User(String _name){
		name = _name;
	}
	
	
	public String getName(){
		return name;
	}
	public void setSentTo(BowDocument doc){
		sentTo = doc;
	}
	
	
	public BowDocument getSentTo(){
		return sentTo;
	}
	
	
	public void setSubject(BowDocument doc){
		subject = doc;
	}
	
	
	public BowDocument getSubject(){
		return subject;
	}
	
	
	public void setMessage(BowDocument doc){
		message = doc;
	}
	
	
	public BowDocument getMessage(){
		return message;
	}
}
