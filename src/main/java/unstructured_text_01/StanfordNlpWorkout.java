package unstructured_text_01;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.IllegalFormatException;

//http://nlp.stanford.edu/nlp/javadoc/javanlp/  

class StanfordNlpWorkout2 {
	public static void main(String[] args) {
		try {
			SentenceDetectTest();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void SentenceDetectTest() throws FileNotFoundException  { //throws IllegalFormatException {
		String testText = "How are you?  This is Mike"; 
		System.out.println(testText);
	}
}
