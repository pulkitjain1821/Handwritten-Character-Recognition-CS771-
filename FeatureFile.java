import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class FeatureFile {
	String header;
	String features;
	String filename;
	BufferedWriter bw;
	
	public FeatureFile(String filename) throws IOException{
		this.filename = filename;
		this.header = "@RELATION ImageFeatures\n";
		this.features = "";
		bw = new BufferedWriter(new FileWriter(filename));
	}
	
	public void addAttribute(String attributeName , int numberOfFeatures){
		for(int i=0; i<numberOfFeatures; i++){
			this.header += "@ATTRIBUTE " + attributeName + "_" + i +  " REAL\n";
		}
	}
	
	
	public void addFeature(double ... feature){
		for(double number: feature){
			features += number + ",";
		}
	}
	
	public void startData() throws IOException{
		bw.write(header);
		bw.write("@ATTRIBUTE CLASS {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62}\n");
		bw.write("@DATA\n");
	}
	
	public void addFeature(double[] ...feature){
		for(double[] numbers: feature){
			for(int i=0; i<numbers.length; i++){
				features += numbers[i] + ",";
			}
		}
	}
	public void addClass(int classname){
		features +=classname;
	}
	
	public void addNewFeature() throws IOException{
		features += "\n";
		bw.write(features);
		features = "";
		
	}
	
	public String getFeatures(){
		return features;
	}
	
	public String getHeader(){
		return header;
	}
	
	public void createArffFile(String header, String data) throws IOException{
		System.out.println("File Writing Completed " + filename);
		bw.close();
	}
}
