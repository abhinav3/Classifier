package classifier;



import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;



import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Classifier {
	
	private static DocumentCategorizerME classifier;
	//private static final String onlpModelPath = "/path/to/store/your/model";
	private static final String trainingDataFilePath = "J:/Walker and beautifier/train.txt";//en-doccat.bin";
	private static boolean INITIALIZED = false;

	public static void main(String[] args) throws InvalidFormatException, IOException, Exception {
		
		
		int cnt=0;
		int scnt=0;
		int nscnt=0;
		File folder = new File("C:/Users/abhinavravi/Downloads/test/test/");
		String filepath="C:/Users/abhinavravi/Downloads/test/test/";
		File[] listOfFiles = folder.listFiles();
		File wfile = new File("J:/Walker and beautifier/IR_GROUP20151.csv");
		if (!wfile.exists()) {
			wfile.createNewFile();
		}

		FileWriter fw = new FileWriter(wfile.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (File file : listOfFiles) {
			if (file.isFile()) {
				
				 String text = new String(Files.readAllBytes(Paths.get(filepath+file.getName())), StandardCharsets.UTF_8);
				if(!text.isEmpty()){
				text = text.replace("\n"," ").replace("\r","");
				//bw.write("non-spam "+text+"\n");
				//System.out.println(content);
				
				
				String content = text;
				cnt++;
				String result=getcategory(content);
				if(result.equals("spam")){
					System.out.println(file.getName());
					bw.write(file.getName()+","+"0"+"\n");
					System.out.println("Predicted category is " + "spam");
					scnt++;
				}else{ 
					nscnt++;
					System.out.println(file.getName());
					bw.write(file.getName()+","+"1"+"\n");
					System.out.println("Predicted category is " + "non-spam");
				}
				
				//bw.write(file.getName()+","+text+"\n");
				 
				 
				}
			}
		}
		fw.close();
		bw.close();
		System.out.println("Total cnt="+cnt+"\tnscnt="+nscnt+"\tscnt="+scnt);
	
		
		
		
		
		
		
	}

	public static String getcategory(String input) throws Exception {
		if (!INITIALIZED) {
			init();



		}
		/*if (input.split(" ").length < 1) {
			throw new Exception("Not enough data for classification");
		}*/
		double[] classDistribution = classifier.categorize(input);

		return classifier.getBestCategory(classDistribution);
	}

	/**
	 * We are storing all training data in a single text file and
	 * training data should be in the following format
	 * category_of_data1 data1
	 * category_of_data2 data2
	 * category_of_dataN dataN
	 */
	public static void init() {
		InputStream dataInputStream = null;
		OutputStream onlpModelOutput = null;
		try {
			dataInputStream = new FileInputStream(trainingDataFilePath);
			ObjectStream<String> lineStream = new PlainTextByLineStream(dataInputStream, "ISO-8859-1");//ISO-8859-1
			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
			// Calculate the training model
			DoccatModel classificationModel = DocumentCategorizerME.train("en", sampleStream);
			classifier = new DocumentCategorizerME(classificationModel);

			INITIALIZED = true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

}
