import java.io.*;
public class Demo {

	public static void main(String[] args) throws IOException {
		
		InvertedIndex index = new InvertedIndex();
	        String phrase = "";

	        index.buildIndex(new String[]{
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\100.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\101.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\102.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\103.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\104.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\105.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\106.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\107.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\108.txt", 
	            "C:\\Users\\dell\\eclipse-workspace\\IR Project\\src\\tmp\\109.txt", 
	        });
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	        phrase = in.readLine();
	        index.compare(phrase.toLowerCase());
	         
	        do {
	        	
	            System.out.println("Print search phrase: ");
	            phrase = in.readLine();
	            System.out.println(index.find(phrase.toLowerCase()));
	           
	        } while (!phrase.isEmpty());
	        
	}

}


