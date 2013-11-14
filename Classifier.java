import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.RemovePercentage;


public class Classifier {
	
	
//	Gives dataTest and dataTraining in dataDivided array
	public static Instances[] getData(Instances data) throws Exception{
		Random random = new Random(1);
		data.randomize(random);
		data.setClassIndex(data.numAttributes()-1);
		RemovePercentage removePercentage = new RemovePercentage();
		String[] options = weka.core.Utils.splitOptions("-P "+ 10);
		removePercentage.setOptions(options);
		removePercentage.setInputFormat(data);
		
		Instances[] dataDivided = new Instances[2];
		dataDivided[0] = Filter.useFilter(data, removePercentage);
		dataDivided[0].setClassIndex(dataDivided[0].numAttributes() - 1);
		 
		removePercentage.setInputFormat(data);
		removePercentage.setInvertSelection(true);
		dataDivided[1] = Filter.useFilter(data, removePercentage);
		
		dataDivided[1].setClassIndex(dataDivided[1].numAttributes() - 1);
		return dataDivided;
	}
	public static Instances getNormalizedData(Instances data) throws Exception{
		Normalize norm=new Normalize();
        norm.setInputFormat(data);
        Instances dataNormalized=Normalize.useFilter(data, norm);
        return dataNormalized;
	}
	
	public static void printMatrix(double[][] matrix){
		int height = matrix.length;			
		int width = matrix[0].length;
		for(int i=0; i<height; i++){
			for(int j=0; j<width; j++){
				System.out.print((int)matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String args[]) throws Exception{
//		for(int i=32; i>=1; i=i/2){
		
			BufferedReader br = new BufferedReader(new FileReader("imageFeature_combined.arff"));
			Instances data = new Instances(br);
			Instances dataNormalized = getNormalizedData(data);
			Instances[] dataDivided = getData(dataNormalized);
	//		This is for writing reports/relevant stuff for this trial run
			BufferedWriter bw = new BufferedWriter(new FileWriter("Report.txt", true));
			
	//		Random Forest Classifier
//			RandomForest randomForest = new RandomForest();
//			String[] options = weka.core.Utils.splitOptions("-I "+ 200);
//			randomForest.setOptions(options);
//			randomForest.buildClassifier(dataDivided[0]);
//			Evaluation eTest = new Evaluation(dataDivided[0]);
//		    eTest.evaluateModel(randomForest, dataDivided[1]);
//		    String strSummary = eTest.toSummaryString();
//		    System.out.println("Random Forest: \n" + strSummary);        
			
	//		SVM
			PolyKernel kernel = new PolyKernel();	
	        String[] options = weka.core.Utils.splitOptions("-E "+ 1);
	        kernel.setOptions(options);
			SMO svm = new SMO();
			options = weka.core.Utils.splitOptions("-N 0 -V 5 -C 4");
	        svm.setOptions(options);
	        svm.setKernel(kernel);
	        svm.buildClassifier(dataDivided[0]);
	        
	        Evaluation eTest = new Evaluation (dataDivided[0]);
	        eTest.evaluateModel(svm, dataDivided[1]);
	        
	        double misclassificationRate = eTest.errorRate();
	        System.out.println("Misclassification Rate " + misclassificationRate);
	        
	        String strSummary = eTest.toSummaryString();
	        String classDetailString = eTest.toClassDetailsString();
	        double[][] confusionMatrix= eTest.confusionMatrix();
	        System.out.println("Confusion Matrix");
	        printMatrix(confusionMatrix);
	        
	        System.out.println("svm: \n"  + classDetailString);
//	        --------------------------------------------------------------------
//	        Testing on whole Data Set
	        
//	        Evaluation eTest = new Evaluation (dataDivided[0]);
//	        eTest.evaluateModel(svm, dataNormalized);
//	        double misclassificationRate = eTest.errorRate();
//	        System.out.println("Misclassification Rate: " + misclassificationRate);
//	        String strSummary = eTest.toSummaryString();
//	        String classDetailString = eTest.toClassDetailsString();
//	        double[][] confusionMatrix= eTest.confusionMatrix();
//	        System.out.println("Confusion Matrix");
//	        printMatrix(confusionMatrix);
//	        System.out.println("svm: \n"  + classDetailString);
	        
//	        -------------------------------------------------------
//	        Cross Fold Validation 110 fold
//	        Evaluation eTest = new Evaluation (dataDivided[0]);
//	        eTest.crossValidateModel(svm, dataNormalized, 10, new Random(System.currentTimeMillis()));//(svm, dataNormalized);
//	        System.out.println(eTest.toSummaryString());
	        
//	    	System.out.println("svm: \n" + strSummary + classDetailString);
			
//			Knn
//			for(int i=1; i<16; i++){
//				IBk kNN = new IBk();			
//				String[] options = weka.core.Utils.splitOptions("-I ");
//				kNN.setOptions(options);
//				kNN.setKNN(6);	//set the number of neighbours in knn default is 1
//				kNN.buildClassifier(dataDivided[0]);
//				Evaluation eTest = new Evaluation(dataDivided[0]);
//		        eTest.evaluateModel(kNN, dataDivided[1]);
////		        String strSummary = eTest.toSummaryString();
////		    	System.out.println("knn: \n" + strSummary);
//		        double strSummary = eTest.errorRate();//.toSummaryString();
//		        System.out.println(strSummary);
//			}
			
	//      Neural Network
//	        MultilayerPerceptron mlp = new MultilayerPerceptron();
//	        String[] options;
//	        options = weka.core.Utils.splitOptions(" -V 20");
//	        mlp.setOptions(options);
//	        mlp.setHiddenLayers("30");
//	        mlp.buildClassifier(dataDivided[0]);
//	        Evaluation eTest = new Evaluation (dataDivided[0]);
//	        eTest.evaluateModel(mlp, dataDivided[1]);
////	        double strSummary = eTest.errorRate();//.toSummaryString();
////	        System.out.println(strSummary);
//	        String strSummary = eTest.toSummaryString();
//	    	System.out.println("NN: \n" + strSummary);
	//        
	//        String features = "-----------Using the initial feature set i.e. 1.  rawM00_0 rawM01_0 rawM10_0 normCM10_0 covariance_0 HUMomemnts eccentricity_0";
	        String features = "16x16 image pixels only: ";
//	        String relevantMessage = features and using the classifier:neurral network we get:\n";
	        
//	        bw.write(relevantMessage + strSummary);
			bw.close();
			br.close();
//		}
			
	}
}
