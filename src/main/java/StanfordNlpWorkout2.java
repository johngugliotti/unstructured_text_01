import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.IllegalFormatException;

import org.apache.lucene.document.Document;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

//http://nlp.stanford.edu/nlp/javadoc/javanlp/  

class StanfordNlpWorkout2 {
	public static void main(String[] args) {
		extractArticleTextTest();
	}
	
	public static void SentenceDetectTest() throws FileNotFoundException  { //throws IllegalFormatException {
		String testText = "How are you?  This is Mike"; 
		System.out.println(testText);
	}
	
	public static void extractArticleTextTest() {
		String plaintext=null;
		URL url=null;
		try {
			String pdf="http://ebiquity.umbc.edu/_file_directory_/papers/126.pdf";
			String html ="http://www.bbc.com/news/world-africa-31960926";
			String lookup=pdf;
			url = new URL(lookup);
			String contentType = getUrlContentType(url);
			System.out.println(contentType);
			
			if (contentType.equalsIgnoreCase("application/pdf")) {
				plaintext=getPdfText(url);
			} else if (contentType.indexOf("text")==0 && 
				contentType.indexOf("html") > 3) 
			{
				plaintext=etractHtmlText(url);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(plaintext); 
	}
	
	/**
	 * 
	 * @param url of the source
	 * @return text extracted from the resource referenced by the url
	 */
	public static String etractHtmlText(URL url) {
		System.out.println("extract article using boilerpipe"); 
		String text = null;
		try {
			text = ArticleExtractor.INSTANCE.getText(url);
		} catch (BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return text;
	}
	
	public static String getUrlContentType(URL url) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)  url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String contentType = connection.getContentType();	
		return contentType;
	}
	
	/**
	 * 
	 * @param url of the pdf resource
	 * @return string extracted from the PDF resource	
	 * @see http://thottingal.in/blog/2009/06/24/pdfbox-extract-text-from-pdf/
	 * @see https://pdfbox.apache.org/docs/1.8.8/javadocs/
	 * @see http://thottingal.in/blog/2009/06/24/pdfbox-extract-text-from-pdf/
	 * @see http://stackoverflow.com/questions/216894/get-an-outputstream-into-a-string
	 * @see http://www.printmyfolders.com/Home/PDFBox-Tutorial
	 */
    public static String getPdfText(URL url) {
    	System.out.println("PDF Extractor");
    	PDDocument pd;
	    String text = null;
	    try {
        	pd=PDDocument.load(url.openStream());
        	PDDocumentInformation metadata = PdfDocumentMetadata(pd);
        	int pagecount = pd.getNumberOfPages();  
        	boolean encryption = pd.isEncrypted(); 
            
            PDFTextStripper stripper = new PDFTextStripper();
            //define output stream class
            OutputStream output = new ByteArrayOutputStream(); 
            OutputStream ostest = new OutputStream()
            {
                private StringBuilder string = new StringBuilder();
                @Override
                public void write(int b) throws IOException {
                    this.string.append((char) b );
                }

                public String toString(){
                    return this.string.toString();
                }
                
                public int size() {
                	return this.string.length();
                }
            };
            //wrap output stream in output stream writer to channel data to it
            OutputStreamWriter osw = new OutputStreamWriter(output);

            //set start/end page for pdf extract
            stripper.setStartPage(1);
            stripper.setEndPage(pd.getNumberOfPages());
            //capture the stripped pdf text to the outputstream 
            stripper.writeText(pd,osw);
            
            if (pd != null) {
                pd.close();  //close() to flush the stream.
            }
            //capture the pdf text contents from the buffer
            text = output.toString();
            osw.close();
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
    	return text;
	}	
    
    public static PDDocumentInformation PdfDocumentMetadata(PDDocument document) {
    	PDDocumentInformation info = document.getDocumentInformation();
    	try {
	    	System.out.println( "Page Count=" + document.getNumberOfPages() );
	    	System.out.println( "Title=" + info.getTitle() );
	    	System.out.println( "Author=" + info.getAuthor() );
	    	System.out.println( "Subject=" + info.getSubject() );
	    	System.out.println( "Keywords=" + info.getKeywords() );
	    	System.out.println( "Creator=" + info.getCreator() );
	    	System.out.println( "Producer=" + info.getProducer() );
	    	System.out.println( "Creation Date=" + info.getCreationDate() );
			System.out.println( "Modification Date=" + info.getModificationDate());
			System.out.println( "Trapped=" + info.getTrapped() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    	return info;
    }
}
