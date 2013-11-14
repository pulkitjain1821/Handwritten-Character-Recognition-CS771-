import thinning.*;
import moments.*;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.features.Haralick;
import de.lmu.ifi.dbs.jfeaturelib.features.Histogram;
import de.lmu.ifi.dbs.jfeaturelib.features.Sift;
import de.lmu.ifi.dbs.jfeaturelib.features.Tamura;
import de.lmu.ifi.dbs.jfeaturelib.shapeFeatures.Eccentricity;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
public class MainFile {
	public static BufferedImage imageCrop(BufferedImage input){
		int height = input.getHeight();
		int width = input.getWidth();
		int hend = -1, hstart = Integer.MAX_VALUE, wend = -1, wstart = Integer.MAX_VALUE;
		for(int i=0; i<height; i++){
			for(int j=0; j<width; j++){
				if (input.getRGB(j, i) == Color.BLACK.getRGB()) {
					if(hstart > i){
						hstart = i;
					}
					if(wstart > j){
						wstart = j;
					}
					if(hend < i){
						hend = i;
					}
					if(wend < j){
						wend = j;
					}
				} 
			}
		}
		int heightNew = hend - hstart; 
		int widthNew = wend - wstart; 
		BufferedImage biCrop = new BufferedImage( widthNew, heightNew, input.getType());
		for(int i = hstart; i<hend; i++){
			for(int j=wstart; j<wend; j++){
				biCrop.setRGB(j-wstart, i-hstart, input.getRGB(j, i));
			}
		}
		return biCrop;
	}
	public static BufferedImage thinnedImage(BufferedImage input){
		int[][] imageData = new int[input.getHeight()][input.getWidth()];
        Color c;
        for (int y = 0; y < imageData.length; y++) {
            for (int x = 0; x < imageData[y].length; x++) {
                if (input.getRGB(x, y) == Color.BLACK.getRGB()) {
                    imageData[y][x] = 1;
                } else {
                    imageData[y][x] = 0;
                }
            }
        }
//        User Zhang Suen Thinning Algorithm
        ThinningService thinningService = new ThinningService();
        thinningService.doZhangSuenThinning(imageData);
        for (int y = 0; y < imageData.length; y++) { 
            for (int x = 0; x < imageData[y].length; x++) {

                if (imageData[y][x] == 1) {
                    input.setRGB(x, y, Color.BLACK.getRGB());
 
                } else {
                    input.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return input;
	}
	
	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static double[] getTamura(BufferedImage input, boolean print){
		ColorProcessor imagec = new ColorProcessor(input);
        Tamura descriptor = new Tamura();
        if(print){
			System.out.println("Sift: " + descriptor.getDescription());
		}
        descriptor.run(imagec);
        List<double[]> features = descriptor.getFeatures();
        for(int i=0; i<features.size(); i++){
        	for(int j=0 ;j<features.get(i).length; j++)
        		if(print){
        			System.out.println(j+ ": " + features.get(i)[j]);
        		}
        }
		return features.get(0);
	}
	public static double[] getHaralick(BufferedImage input, boolean print){
        ColorProcessor imagec = new ColorProcessor(input);
        Haralick descriptor = new Haralick();
        descriptor.run(imagec);
        List<double[]> features = descriptor.getFeatures();
        for(int i=0; i<features.size(); i++){
        	for(int j=0 ;j<features.get(i).length; j++)
        		if(print){
        			System.out.println(j+ ": " + features.get(i)[j]);
        		}
        }
		return features.get(0);
	}
	public static double[] getEccentricity(BufferedImage input, boolean print){
		 ColorProcessor imagec = new ColorProcessor(input);
	        Eccentricity descriptor = new Eccentricity();
	        descriptor.run(imagec);
	        if(print){
	        	System.out.println(descriptor.getDescription());
	        }
	        List<double[]> features = descriptor.getFeatures();
	        for(int i=0; i<features.size(); i++){
	        	for(int j=0 ;j<features.get(i).length; j++)
	        		if(print){
	        			System.out.println(j+ ": " + features.get(i)[j]);
	        		}
	        }
			return features.get(0);
	}
	
	public static double[] getHistogram(BufferedImage input, boolean print) throws IOException{
        ColorProcessor image = new ColorProcessor(input);
        // load the properties from the default properties file
        // change the histogram to span just 2 bins
        // and let's just extract the histogram for the RED channel as 
        // pixel value is same for all color channels
        LibProperties prop = LibProperties.get();
        prop.setProperty(LibProperties.HISTOGRAMS_BINS, 2);
        prop.setProperty(LibProperties.HISTOGRAMS_TYPE, "Red");
        // initialize the descriptor, set the properties and run it
        Histogram descriptor = new Histogram();
        descriptor.setProperties(prop);
        descriptor.run(image);
        // obtain the features
        List<double[]> features = descriptor.getFeatures();
        // print the features to system out
        for (int i=0; i<features.size(); i++){
            for(int j=0; j<features.get(i).length; j++){
            	if(print){
            		System.out.println(features.get(i)[j]);
            	}
            }
        }
        return features.get(0);
	}
	
	public static double[][] getMatrix(BufferedImage input){
		int height = input.getHeight(); 
		int width = input.getWidth();
		double [][] array = new double[height][width];
		for(int i=0; i<height; i++){
			for(int j=0; j<width; j++){
				if(input.getRGB(j, i) == Color.BLACK.getRGB()){
					array[i][j] = 0;
				}
				else{
					array[i][j] = 1;
				}
			}
		}
		return array;
	}
	
	public static double[][]zoning(double[][] matrix, int noOfZones){
		int length = matrix[0].length; 
		int zoneSize = length/noOfZones;
		double[][] zonedMatrix = new double[noOfZones][noOfZones];
		for(int i=0; i<length; i++){
			for(int j=0; j<length; j++){
//				System.out.println(i/zoneSize + " " + j/zoneSize);
				if(matrix[i][j] == 0){
					zonedMatrix[i/zoneSize][j/zoneSize]++;
				}
			}
		}
		return zonedMatrix;
	}
	
	public static double[] linearize(double[][] matrix){
		int width = matrix[0].length; 
		int heigth = matrix.length;
		int count = 0;
		double[] array = new double[width*heigth];
		for(int i=0; i<heigth; i++){
			for(int j=0; j<width; j++){
				array[count++] = matrix[i][j];
			}
		}
		return array;
		
	}

	public static int[] linearizeInt(double[][] matrix){
		int width = matrix[0].length; 
		int heigth = matrix.length;
		int count = 0;
		int[] array = new int[width*heigth];
		for(int i=0; i<heigth; i++){
			for(int j=0; j<width; j++){
				array[count++] = (int) matrix[i][j];
			}
		}
		return array;
		
	}
	
	public static double[] getPointFeature13(double[][]matrix){
		int height = matrix.length; 
		int width = matrix[0].length; 
		double[][] array = new double[4][2];
		for(int i=0; i<height; i++){
			for(int j=0; j<width; j++){
				if(matrix[i][j] == 0){
					array[i/8][j/16]++; 
				}
			}
		}
		double[] featurePointArray = new double[13];
		double [] f = linearize(array);
		for(int i=0; i<f.length; i++){
			featurePointArray[i] = f[i];
		}
		featurePointArray[8]= array[1][0] + array[1][1];
		featurePointArray[9] = array[2][0] + array[2][1];
		double f11 =0 ;
		double f12 =0 ;
		for(int i=0; i<4; i++){ 
			featurePointArray[10] =+ array[i][0];
			featurePointArray[11] =+ array[i][1];
		}
		featurePointArray[12] = f11 + f12;
		
		return featurePointArray;
	}
	
	public static double[] initializeArray(int length, int number){
		double[] array = new double[length];
		for(int i=0; i<length; i++){
			array[i] = number; 
		}
		return array;
	}
	
	public static double[] concatenateArrays(double[] ... arrays){
		int totalSize = 0;
		for(double[] array: arrays){
			totalSize += array.length;
		}
		double[] concatenatedArray = new double[totalSize];
		int count=0;
		for(double[] array: arrays){
			for(int i=0; i<array.length; i++){
				concatenatedArray[count++] = array[i];
			}
		}
		return concatenatedArray;
	}
	
	public static double[] getContourFeature(double[][] matrix){
		int length = matrix.length;
		double[] x1array = initializeArray(length, length);//new double[length];
		double[] x2array = new double[length];
		double[] y1array = initializeArray(length, length);//new double[length];
		double[] y2array = new double[length];
		
		for(int i=0; i<length; i++){
			for(int j=0; j<length; j++){
				if(matrix[i][j] == 0){
					if(i>y2array[j]){
						y2array[j] = i; 
					}
					if(j>x2array[i]){
						x2array[i] = j;
					}
					if(i<y1array[j]){
						y1array[j] = i;
					}
					if(j<x1array[i]){
						x1array[i] = j;
					}
				}
			}
		}
		double[] concatenatedArray = concatenateArrays(x1array, x2array, y1array, y2array);
		return concatenatedArray;
	}
	
	public static double[] getHistogramAxes(double[][] matrix){
		int dimension = matrix.length;
		double[] array = new double[dimension *2 ];
		double[] xaxis = new double[dimension];
		double[] yaxis = new double[dimension];
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(matrix[i][j] == 0){
					xaxis[i] ++;
					yaxis[j] ++;
				}
			}
		}
		for(int i=0; i<dimension; i++){
			array[i+13] = xaxis[i];
		}
		for(int j=0; j<dimension; j++){
			array[j+13] = yaxis[j];
		}
		return array;
	}
	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String args[]) throws URISyntaxException, IOException{
		String InputFolder = "Boundary";
		int rescaledSize = 28;
		String OutputFolder = "BoundaryOutput";
		
		long startTime = System.currentTimeMillis();
		File folder = new File(InputFolder);
		File[] listOfFiles = folder.listFiles();
	
//		for(int m=32; m>=32; m=m/2){
			System.out.println("hi");
			String arffFilename = "imageFeature_combined.arff";
			FeatureFile featureObject = new FeatureFile(arffFilename);
			rescaledSize=32;
			int noOfZones = 4;
//			featureObject.addAttribute("haralickFeatures",14);
//			featureObject.addAttribute("holes" , 1);
//			featureObject.addAttribute("countourFeature", rescaledSize*4);
//			featureObject.addAttribute("HistogramAxis", rescaledSize*2);
//			featureObject.addAttribute("13PointFeature", 13);
//			featureObject.addAttribute("histogramFeatures",2);
			featureObject.addAttribute("rawM00", 1);
			featureObject.addAttribute("rawM01", 1);
			featureObject.addAttribute("rawM10", 1);
			featureObject.addAttribute("normCM11", 1);
			featureObject.addAttribute("covariance", 1);
			featureObject.addAttribute("zoning", (int) Math.pow(noOfZones , 2));
//			featureObject.addAttribute("HUMomemnt", 6);
////			featureObject.addAttribute("tamura", 1);
////			featureObject.addAttribute("scale", 1);
//			featureObject.addAttribute("eccentricity", 1);
//			featureObject.addAttribute("pixel",(int)Math.pow(rescaledSize, 2));
			featureObject.startData();
			for(int i=0; i<listOfFiles.length; i++){
				if(i%55 == 0){
					System.out.println("Completed " + i + " images in " + (System.currentTimeMillis() - startTime));
				}
				if(listOfFiles[i].isFile()){
					String filename = listOfFiles[i].getName();
					String classname = filename.substring(3, 6);
					String instancename = filename.substring(7, 10);
	//				System.out.println("filename: " + filename + " classname: " + classname +						" instancename: " +	instancename);
					BufferedImage image = ImageIO.read(new File(InputFolder + "\\" + filename));
	//				System.out.println("Image " + filename + "-----------------------------------------");
	//				Crop the image
	//				image = imageCrop(image);
					
	//				Resize the image
					image = Scalr.resize(image, rescaledSize);
					
	//				Binary thinning algorithm
//					image = thinnedImage(image);
					String output = filename  +".png";
	//				ImageIO.write(image, "png", new File("/" + folderName + "/" +output));
					ImageIO.write(image, "png", new File(OutputFolder + "\\" + output));
					double[][]matrix = getMatrix(image);
					
	//		      	Haralick feature
//			      	double[] haralickFeatures = getHaralick(image, false);
//					featureObject.addFeature(haralickFeatures);
					
//					Connected Component
//					ConnectComponent cc = new ConnectComponent();
//					Dimension d = new Dimension();
//					d.height = matrix.length; 
//					d.width = matrix.length;
//					cc.compactLabeling(linearizeInt(matrix), d, true);
//					int holes = cc.getMaxLabel();
//					featureObject.addFeature(holes);
//					System.out.println("holes: "  + holes + " " + i);
					
//					Contour Feature
//					double[] countourFeature = getContourFeature(matrix);
//					featureObject.addFeature(countourFeature);
					
//					Histogram feature
//					double[] histogramAxes = getHistogramAxes(matrix);
//					featureObject.addFeature(histogramAxes);
					
//					13 Point Features
//					double[] pointFeature13 = getPointFeature13(matrix);
//					featureObject.addFeature(pointFeature13);
					
	//				Histogram feature
//					double[] histogramFeatures = getHistogram(image, false);
//					featureObject.addFeature(histogramFeatures);
					
//	//				Raw moments
					Moments moments = new Moments();
					double rawM00 = moments.getRawMoment(0, 0, matrix);
					double rawM10 = moments.getRawMoment(1, 0, matrix);
					double rawM01 = moments.getRawMoment(0, 1, matrix);
	//				System.out.println("rawM00: " + rawM00 + " rawM10: " + rawM10 + " rawM011: " + rawM01);
					featureObject.addFeature(rawM00, rawM10, rawM10);
					
//					centralmoment 00 = rawmoment 00/ central moment 01 = phi/
////					Covariance and Normal Central Moment
			        double normCM10 = moments.getNormalizedCentralMoment(1, 0, matrix);
	//		        System.out.println("normCM10: " + normCM10);
			        double covariance = moments.getCovarianceXY(0, 0, matrix);
	//		        System.out.println("covariance: " + covariance);
			        featureObject.addFeature(normCM10, covariance);
			        
	//		        Zoning
			        double[][]zonedMatrix = zoning(matrix,noOfZones);
			        double[] linearZonedMatrix = linearize(zonedMatrix);
			        featureObject.addFeature(linearZonedMatrix);
			        
//	//		        HU moments
//			        double [] huM = new double[6];
//			        for(int j=1; j<7; j++){
//			        	huM[j-1] =  moments.getHuMoment(matrix, j);
//	//		        	System.out.println("HUMoment: " + huM[j-1]);
//			        }
//			        featureObject.addFeature(huM);
			        
	//		        Scale invariant Transform feature
	//		        double[] sift = getSift(image, true);
	//		        featureObject.addFeature(sift);
			        
	//		        Tamura(Based on Human Perception)
	//		        double[] tamura = getTamura(image, true);
	//		        featureObject.addFeature(tamura);
			        
	//		        Eccentricity
//			        double[] eccentricity = getEccentricity(image, false);
//			        featureObject.addFeature(eccentricity);
			        
	//				Original Image
//					featureObject.addFeature(linearize(matrix));
			        featureObject.addClass(Integer.valueOf(classname));
			        featureObject.addNewFeature();
				}
			}
			featureObject.createArffFile(featureObject.getHeader(), featureObject.getFeatures());
//		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time for which the program ran: " + (endTime - startTime));		
	}
}

