package com.hedgehog.kaggle.allstate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Arrays;
import java.util.List;

public class MakeArff {

	public enum State {		   
		 AL, AK, AZ, AR, CA, CO, CT, DE, FL, GA, 
		 HI, ID, IL, IN, IA, KS, KY, LA, ME, MD,
		 MA, MI, MN, MS, MO, MT, NE, NV, NH, NJ,
		 NM, NY, NC, ND, OH, OK, OR, PA, RI, SC,
		 SD, TN, TX, UT, VT, VA, WA, WV, WI, WY,
		 AS, DC, FM, GU, MH, MP, PW, PR, VI		 
	}
	
	public static void main(String[] args) {
		
		String trainFilename = "src/train.csv";
		String classFilename = "src/trainClass.csv";
		String wekaTrainFilename = "src/train.arff";
		String wekaTrainResultFilename = "src/preTrainResult.arff";
		List<Integer> classColumns = Arrays.asList(17, 18, 19, 20, 21, 22, 23);	
		String[] classNames = {"AClass", "BClass", "CClass", "DClass", "EClass", "FClass", "GClass"};

		makeClassFile(trainFilename, classFilename, 2, 1, classColumns, classNames);
		combineAttributesAndClassesToArff(trainFilename, classFilename, wekaTrainFilename);
		//combineAttributesAndWordsToArff(trainFilename, wekaTrainResultFilename, "?", classNames);
	}
	
	
	/**
	 * Make a classFile that contains only the classes columns
	 * @param filename
	 * @param classFilename
	 * @param purchaseColumn
	 * @param purchaseIndiciator
	 * @param classColumns
	 */
	private static void makeClassFile(String filename, String classFilename, int purchaseColumn, 
			int purchaseIndicator, List<Integer> classColumns, String[] classNames) {
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			line = br.readLine();

			File classFile = new File(classFilename);
			if (!classFile.exists()) {
				classFile.createNewFile();	
			}
			FileWriter fw = new FileWriter(classFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
	
			String newLine = System.getProperty("line.separator");
			
			String app = "";
			for (String className: classNames) {
				fw.write(app + className);
				app = ",";
			}
			fw.write(newLine);
			
			int numberOfLines = 0;
			while (line != null) {
				String[] tokens = line.split(",");
				numberOfLines++;
				if (Integer.parseInt(tokens[purchaseColumn]) == purchaseIndicator) {
					StringBuilder sb = new StringBuilder();
					String ap = "";
					
					for (int classColumn: classColumns) {
						sb.append(ap);
						ap = ",";
						sb.append(tokens[classColumn]);			
					}
					String classes = sb.toString();
					for (int i = 0; i < numberOfLines; i++) {
						bw.write(classes + newLine);
					}
					numberOfLines = 0;
				}
				line = br.readLine();
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	private static void combineAttributesAndClassesToArff(String attributeFilename, String classFilename, 
			String wekaFilename) {
		BufferedReader brAttribute = null;
		BufferedReader brClass= null;
		try {
			brAttribute = new BufferedReader(new FileReader(attributeFilename));
			brClass = new BufferedReader(new FileReader(classFilename));
			String lineAttribute = brAttribute.readLine();
			String lineClass = brClass.readLine();
			
			File wekaFile = new File(wekaFilename);
			if (!wekaFile.exists()) {
				wekaFile.createNewFile();	
			}
			FileWriter fw = new FileWriter(wekaFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			String newLine = System.getProperty("line.separator");
			String titleLine = "@relation " + wekaFilename.substring(4
					, wekaFilename.length() - 5);
			bw.write(titleLine + newLine + newLine);
			writeAttributes(bw, lineAttribute.split(","));
			writeClassAttributes(bw, lineClass.split(","));
			bw.write(newLine + "@data" + newLine);
			
			lineAttribute = brAttribute.readLine();	
			lineClass = brClass.readLine();
			while (lineAttribute != null) {
				StringBuilder sb = new StringBuilder();
				String ap = "";
				
				String[] tokensAttribute = lineAttribute.split(",");
				for (int i = 0; i < tokensAttribute.length; i++) {
					tokensAttribute[i] = getAttribute(tokensAttribute, i);				
					sb.append(ap);
					ap = ",";
					sb.append(tokensAttribute[i]);
				}
				
				String[] tokensClass = lineClass.split(",");
				for (int i = 0; i < tokensClass.length; i++) {
					sb.append(ap);
					sb.append(tokensClass[i]);
				}
				
				bw.write(sb.toString() + newLine);
				lineAttribute = brAttribute.readLine();
				lineClass = brClass.readLine();
			}
			brAttribute.close();
			brClass.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Write the @attributes to the arff file.
	 * @param bw
	 * @param line
	 */
	private static void writeAttributes(BufferedWriter bw, String[] attributeNames) {
		try {
			String newLine = System.getProperty("line.separator");
			
			for (int i = 0; i < attributeNames.length; i++) {
				String attributeLine = "@attribute ";
				attributeLine += attributeNames[i] + " ";
				
				switch(i) {
				case 0: attributeLine += "integer";				// applicant ID, 97009
						break;
				case 1: attributeLine += "integer";  			// shopping point, 1 to some number
						break;
				case 2: attributeLine += "{0, 1}";				// purchase or not
						break;
				case 3: attributeLine += "integer";				// day, 0 to some number
						break;
				case 4: attributeLine += "real";				// time, 24 hours
						break;
				case 5: attributeLine += 
						"{-1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, " +
						"11, 12, 13, 14, 15, 16, 17, 18, 19, 20, " +
						"21, 22, 23, 24, 25, 26, 27, 28, 29, 30, " +
						"31, 32, 33, 34, 35, 36, 37, 38, 39, 40, " +
						"41, 42, 43, 44, 45, 46, 47, 48, 49, 50, " +
						"51, 52, 53, 54, 55, 56, 57, 58, 59}";	// state, 36 states total
						break;
				case 6: attributeLine += "integer";				// location, 6248 locations
						break;
				case 7: attributeLine += "{1, 2, 3, 4}";		// group size
						break;
				case 8: attributeLine += "{0, 1}";				// home owner
						break;
				case 9: attributeLine += "real";				// car age, range from 1 to 85, total 67 ages
						break;
				case 10: attributeLine += "real";				// car value, -1, 1, 2 .. 9
						break;
				case 11: attributeLine += "real";				// risk factor, -1, 1, 2, 3, 4
						break;
				case 12: attributeLine += "real";				// oldest age, 18 to 75, totally 58 ages
						break;
				case 13: attributeLine += "real";				// youngest age, 16 to 75, total 60 ages
						break;
				case 14: attributeLine += "{0, 1}";				// married couple
						break;
				case 15: attributeLine += "{-1, 1, 2, 3, 4}";	// C_previous
						break;
				case 16: attributeLine += "real";				// previous duration
						break;
				case 17: attributeLine += "{0, 1, 2}";			// A
						break;
				case 18: attributeLine += "{0, 1}";				// B
						break;
				case 19: attributeLine += "{1, 2, 3, 4}";		// C
						break;
				case 20: attributeLine += "{1, 2, 3}";			// D
						break;
				case 21: attributeLine += "{0, 1}";				// E
						break;
				case 22: attributeLine += "{0, 1, 2, 3}";		// F
						break;
				case 23: attributeLine += "{1, 2, 3, 4}";		// G
						break;
				case 24: attributeLine += "real";				// cost
						break;
				default: break;
				}
				bw.write(attributeLine + newLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Write the attributes from the class file to the arff file
	 * @param bw
	 * @param line
	 */
	private static void writeClassAttributes(BufferedWriter bw, String[] classNames) {
		try {
			String newLine = System.getProperty("line.separator");
			
			for (int i = 0; i < classNames.length; i++) {
				String attributeLine = "@attribute ";
				attributeLine += classNames[i] + " ";
				
				switch(i) {
				case 0: attributeLine += "{0, 1, 2}";			// A
						break;
				case 1: attributeLine += "{0, 1}";				// B
						break;
				case 2: attributeLine += "{1, 2, 3, 4}";		// C
						break;
				case 3: attributeLine += "{1, 2, 3}";			// D
						break;
				case 4: attributeLine += "{0, 1}";				// E
						break;
				case 5: attributeLine += "{0, 1, 2, 3}";		// F
						break;
				case 6: attributeLine += "{1, 2, 3, 4}";		// G
						break;
				default: break;
				}
				bw.write(attributeLine + newLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the value of attribute. It must be a number, not a letter or letters.
	 * @param tokens
	 * @param i
	 * @return
	 */
	 public static String getAttribute(String[] tokens, int i) {
		 if (tokens[i].equals("") || tokens[i].equals("NA")) {
				tokens[i] = "-1";
			}
			
			if (i == 4) {
				String[] times = tokens[i].split(":");
				int hour = Integer.parseInt(times[0]);
				int min = Integer.parseInt(times[1]);
				tokens[i] = "" +  (hour + (double) min / 60);	
			}
			
			if (i == 5) {
				switch(State.valueOf(tokens[i])) {
				case AL: tokens[i] = "1"; break;
				case AK: tokens[i] = "2"; break;
				case AZ: tokens[i] = "3"; break;
				case AR: tokens[i] = "4"; break;
				case CA: tokens[i] = "5"; break;
				case CO: tokens[i] = "6"; break;
				case CT: tokens[i] = "7"; break;
				case DE: tokens[i] = "8"; break;
				case FL: tokens[i] = "9"; break;
				case GA: tokens[i] = "10"; break;
				case HI: tokens[i] = "11"; break;
				case ID: tokens[i] = "12"; break;
				case IL: tokens[i] = "13"; break;
				case IN: tokens[i] = "14"; break;
				case IA: tokens[i] = "15"; break;
				case KS: tokens[i] = "16"; break;
				case KY: tokens[i] = "17"; break;
				case LA: tokens[i] = "18"; break;
				case ME: tokens[i] = "19"; break;
				case MD: tokens[i] = "20"; break;
				case MA: tokens[i] = "21"; break;
				case MI: tokens[i] = "22"; break;
				case MN: tokens[i] = "23"; break;
				case MS: tokens[i] = "24"; break;
				case MO: tokens[i] = "25"; break;
				case MT: tokens[i] = "26"; break;
				case NE: tokens[i] = "27"; break;
				case NV: tokens[i] = "28"; break;
				case NH: tokens[i] = "29"; break;
				case NJ: tokens[i] = "30"; break;
				case NM: tokens[i] = "31"; break;
				case NY: tokens[i] = "32"; break;
				case NC: tokens[i] = "33"; break;
				case ND: tokens[i] = "34"; break;
				case OH: tokens[i] = "35"; break;
				case OK: tokens[i] = "36"; break;
				case OR: tokens[i] = "37"; break;
				case PA: tokens[i] = "38"; break;
				case RI: tokens[i] = "39"; break;
				case SC: tokens[i] = "40"; break;
				case SD: tokens[i] = "41"; break;
				case TN: tokens[i] = "42"; break;
				case TX: tokens[i] = "43"; break;
				case UT: tokens[i] = "44"; break;
				case VT: tokens[i] = "45"; break;
				case VA: tokens[i] = "46"; break;
				case WA: tokens[i] = "47"; break;
				case WV: tokens[i] = "48"; break;
				case WI: tokens[i] = "49"; break;
				case WY: tokens[i] = "50"; break;
				case AS: tokens[i] = "51"; break;
				case DC: tokens[i] = "52"; break;
				case FM: tokens[i] = "53"; break;
				case GU: tokens[i] = "54"; break;
				case MH: tokens[i] = "55"; break;
				case MP: tokens[i] = "56"; break;
				case PW: tokens[i] = "57"; break;
				case PR: tokens[i] = "58"; break;
				case VI: tokens[i] = "59"; break;
				default: tokens[i] = "-1"; break;
				}
			}
			
			if (i == 10) {
				char c = tokens[i].charAt(0);
				switch(c) {
				case 'a': tokens[i] = "1";
						break;
				case 'b': tokens[i] = "2";
						break;
				case 'c': tokens[i] = "3";
						break;
				case 'd': tokens[i] = "4";
						break;
				case 'e': tokens[i] = "5";
						break;
				case 'f': tokens[i] = "6";
						break;
				case 'g': tokens[i] = "7";
						break;
				case 'h': tokens[i] = "8";
						break;
				case 'i': tokens[i] = "9";
						break;
				default: break;
				}
			}
		 return tokens[i];
	 }
	 
	 
	 
	 private static void combineAttributesAndWordsToArff(String attributeFilename, String wekaFilename, 
			 String wordToReplaceClassValue, String[] classNames) {
		 BufferedReader br= null;
		 try {
			 br = new BufferedReader(new FileReader(attributeFilename));
			 String line = br.readLine();

			 File wekaFile = new File(wekaFilename);
			 if (!wekaFile.exists()) {
				 wekaFile.createNewFile();	
			 }
			 FileWriter fw = new FileWriter(wekaFile.getAbsoluteFile());
			 BufferedWriter bw = new BufferedWriter(fw);

			 String newLine = System.getProperty("line.separator");
			 String titleLine = "@relation " + wekaFilename.substring(4
					 , wekaFilename.length() - 5);
			 bw.write(titleLine + newLine + newLine);
			 writeAttributes(bw, line.split(","));
			 writeClassAttributes(bw, classNames);	
			 bw.write(newLine + "@data" + newLine);

			 line = br.readLine();	
			 while (line != null) {
				 StringBuilder sb = new StringBuilder();
				 String ap = "";

				 String[] tokensAttribute = line.split(",");
				 for (int i = 0; i < tokensAttribute.length; i++) {
					 tokensAttribute[i] = getAttribute(tokensAttribute, i);				
					 sb.append(ap);
					 ap = ",";
					 sb.append(tokensAttribute[i]);
				 }

				 for (int i = 0; i < classNames.length; i++) {
					 sb.append(ap);
					 sb.append(wordToReplaceClassValue);
				 }

				 bw.write(sb.toString() + newLine);
				 line = br.readLine();
			 }
			 br.close();
			 bw.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }
}
