package mkdd_ghost;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileWriter {
	private static ArrayList<Byte> allBytes;
	private static String filePath;
	//hi
	public static ArrayList<Byte> readInputs(String fileName) {	
		ArrayList<Byte> inputs = new ArrayList<Byte>();
		filePath = fileName;
		int resultCode = 0;	
		DataInputStream fromFile = null;
		try {	
			FileInputStream fis = new FileInputStream(fileName);
			fromFile = new DataInputStream(fis);	
			while (true){			
				byte joystickInput = fromFile.readByte();	
				inputs.add(joystickInput);	
			} // end while
		} catch (FileNotFoundException e){
			resultCode = 1; // Error opening file
		} catch (EOFException e){
		// Normal occurrence since entire file is read; ignore exception
		} catch (IOException e){
		 resultCode = 2; // Error reading file
		} finally{
			try	{
				 if (fromFile != null)
					 fromFile.close();
			} catch (IOException e) {
				resultCode = 3; // Error closing file
			}//end finally
		}//end initial try
		allBytes = inputs;
		return inputs;
		
	}//end method
	
	public static ArrayList<Byte> getInputs(){
		
		ArrayList<Byte> onlyInputs = new ArrayList<Byte>();
		int index = 5288;
		//Byte.toUnsignedInt(allBytes.get(index)) != 255 
		
		
		while (index < allBytes.size() && index < 18700 && Byte.toUnsignedInt(allBytes.get(index)) != 255 ) {
			onlyInputs.add(allBytes.get(index));
			index +=1;
		}
		
		return onlyInputs;
	}
	
	public static void makefile(ArrayList<Byte> demBytes) throws FileNotFoundException {	
		byte[] emBytes = listToArray(demBytes);
		
		File outputFile = new File(filePath + ".dtm");  
		FileOutputStream outputStream = new FileOutputStream(outputFile);
	    	
	    addHeader(outputStream, emBytes.length/2);
	    openingInputs(outputStream);
	    characterSelect(outputStream);
	    courseSelect(outputStream);
   	    	    	
	    ArrayList<Byte> onlyInputs = FileWriter.getInputs();
	    
	    ArrayList<Byte> onlyButtons = new ArrayList<Byte>();
	    for(int i = 1; i < onlyInputs.size(); i +=2) {
	    	onlyButtons.add(onlyInputs.get(i));
	    	onlyButtons.add(onlyInputs.get(i));
	    	onlyButtons.add(onlyInputs.get(i));
	    }
	    
	    
	    ArrayList<Byte> onlySticks = new ArrayList<Byte>();
	    
	    for(int i = 0; i < onlyInputs.size(); i +=2) {
	    	onlySticks.add(onlyInputs.get(i));
	    	
	    }
	    ArrayList<byte[]> adsf = normalizeStick(onlySticks);
	    
	    for(int i = 0; i < adsf.size()-2; i +=3) {
	    	System.out.print( ":" + Byte.toUnsignedInt(adsf.get(i)[0]));
	    	System.out.print(" "  + ":" + Byte.toUnsignedInt(adsf.get(i+1)[0]));
	    	System.out.println(" " + ":" + Byte.toUnsignedInt(adsf.get(i+2)[0]));
	    }
	    
	    for(int i = 0; i < onlySticks.size() -1; i ++) {
	    	byte[] currentInput = Input.makeInput(adsf.get(i), onlyButtons.get(i));
	    	addCopies(outputStream, currentInput, 1);
	    }
	    
	}

	private static ArrayList<byte[]> normalizeStick(ArrayList<Byte> values){
		ArrayList<int[]> sticks = new ArrayList<int[]>();
		for(int i = 0; i < values.size()-1; i++) {
			sticks.add(Input.fixstick(values.get(i)));
		}
		sticks.add(0, sticks.get(0));
		sticks.add(2, sticks.get(0));
		
		ArrayList<byte[]> endme = new ArrayList<byte[]>();
		
		byte[] firstval = {(byte)sticks.get(0)[0], (byte)sticks.get(0)[1]};
		byte[] secondval = {(byte)sticks.get(1)[0], (byte)sticks.get(1)[1]};
		
		endme.add(firstval); 
		endme.add(secondval);
		
		for(int i = 2; i < values.size(); i ++) {
			int base = sticks.get(i)[0];
			int ahead = sticks.get(i+1)[0];
			double difference = (double)(ahead - base) / (double) 3;
			
			double half = base + difference;
			double third = base + 2 * difference;
			
			byte[] med1 = {(byte)(half), (byte)sticks.get(i)[1]};
			byte[] med2 = {(byte)(third), (byte)sticks.get(i)[1]};
			byte[] med =  {(byte)sticks.get(i)[0], (byte)sticks.get(i)[1]};
			
			endme.add(med);
			endme.add(med1);
			endme.add(med2);
		}
		
		return endme;
		
	}
	/*
	 *for(int i = 3; i < values.size()-1; i+=3) {
			int base = Byte.toUnsignedInt(sticks.get(i)[0]);
			int behind = Byte.toUnsignedInt(sticks.get(i-1)[0]);
			int ahead = Byte.toUnsignedInt(sticks.get(i+1)[0]);
			double half =  (double)(base + behind)/ 2;
			double third = (double)(ahead - base) / (double) 3 ;
			//half = Byte.toUnsignedInt(sticks.get(i)[0]) - half;
			//half += base;
			third += base;
			//System.out.println((half));
			
			
			byte[] med1 = {(byte)(half), sticks.get(i)[1]};
			byte[] med2 = {(byte)(third), sticks.get(i)[1]};
			//System.out.println("" + (i-1)  + ":" + half + " "+ (i) + ":" + base + " " + (i+1)+ ":" + third);
			
			sticks.add(i-1, med1 );
			sticks.add(i+1, med2 );

		}
		return sticks;
	 */
	private static void addCopies(FileOutputStream outputStream, byte[] input, int numCopies) {
		for(int i = 0; i < numCopies; i++) {
    		try {
				outputStream.write(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}	
	}//end method
	private static byte[] listToArray(ArrayList<Byte> linputs) {
		byte[] ainputs = new byte[linputs.size()];
		for(int i = 0; i < linputs.size(); i ++) {
			ainputs[i] = linputs.get(i);
		}
		return ainputs;
	}//end method
	private static void addHeader(FileOutputStream outputStream, int numInputs) {
		 
	    try {    	
	    	byte[] headerPartOne = {68, 84, 77, 26, 71, 77, 52, 69, 48, 49, 0, 1, 1};
			ByteBuffer b = ByteBuffer.allocate(8);
			b.putInt(2387);
			b.order(ByteOrder.LITTLE_ENDIAN);
			byte[] visualFrames = b.array();
			
			
				
			ByteBuffer c = ByteBuffer.allocate(8);
			c.order(ByteOrder.LITTLE_ENDIAN);
			c.putInt(numInputs);
			
			byte[] inputFrames = c.array();
			
			byte[] headerPartTwo = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 88, 49, 49, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte)151,(byte) 249,
					(byte) 235, (byte) 195,(byte) 154,(byte) 180, 36, 78, 65,(byte) 152, 72, 121, 57,(byte) 136, 86, 26, (byte)221, 126, 108, 
					93, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, (byte)255, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 74, 54, 
					(byte)186,(byte) 219, (byte)111, (byte)243, (byte)237, 83, 60, 38, (byte)179,(byte) 178, 
					1, (byte)230,(byte) 211, 103, 58, 35, 83,(byte) 168,
					0, 0, 0, 0, 0, 0, 0, 0, (byte)144, (byte)85, 9, (byte)211, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			
	    	try {
	    		outputStream.write(headerPartOne);
	        	outputStream.write(visualFrames);
	    		outputStream.write(inputFrames);
	    		outputStream.write(headerPartTwo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	private static void openingInputs(FileOutputStream outputStream) {
		addCopies(outputStream, Input.nothing, 2000);
    	addCopies(outputStream, Input.buttonStart, 200);
    	addCopies(outputStream, Input.nothing, 500);
    	addCopies(outputStream, Input.buttonStart, 200);
    	addCopies(outputStream, Input.nothing, 100);
    	addCopies(outputStream, Input.buttonA, 100);
    	addCopies(outputStream, Input.nothing, 100);
    	addCopies(outputStream, Input.buttonA, 100);
    	addCopies(outputStream, Input.joystickDown, 60);
    	addCopies(outputStream, Input.buttonA, 50);
    	addCopies(outputStream, Input.nothing, 50);
    	addCopies(outputStream, Input.buttonA, 50);
    	addCopies(outputStream, Input.nothing, 50);
    	addCopies(outputStream, Input.buttonA, 50);
    	addCopies(outputStream, Input.nothing, 50);
    	addCopies(outputStream, Input.buttonA, 50);
    	addCopies(outputStream, Input.nothing, 50);
    	addCopies(outputStream, Input.buttonA, 50);
    	addCopies(outputStream, Input.nothing, 201);
	}
	private static void characterSelect(FileOutputStream outputStream) {
		//int[] selectedCharacters = GUI.getCharacters();
		int[] selectedCharacters = {allBytes.get(5248), allBytes.get(5249)};
		for(int i = 0; i < selectedCharacters.length; i ++) {
			//System.out.println(selectedCharacters[i]);
			switch(selectedCharacters[i]) {
				
				case 1: selectedCharacters[i] = 0; break;
				case 2: selectedCharacters[i] = 1;break; 
				case 17: selectedCharacters[i] = 2;break;
				case 18: selectedCharacters[i] = 3;break;
				case 4: selectedCharacters[i] = 4;break;
				case 3: selectedCharacters[i] = 5;break;
				case 14: selectedCharacters[i] = 6;break;
				case 16: selectedCharacters[i] = 7;break;
				
				
			}
			//System.out.println(selectedCharacters[i]);
		}
		int[][] destination = Input.characterCoordinates;
		destination[0] = Input.characterCoordinates[selectedCharacters[0]];
		destination[1] = Input.characterCoordinates[selectedCharacters[1]];
		int[][] directions = {destination[0], destination[1]};
		int[] currentPlace = {0,0};
		
		for(int i = 0; i< directions.length; i ++) {
			int[] currentGoal = destination[i];
			
			while(currentGoal[0] != currentPlace[0]) {
				
				addCopies(outputStream, Input.joystickDown, 40);
				addCopies(outputStream, Input.nothing, 40);
				if(currentPlace[0] ==0) { currentPlace[0] = 1;}
				else { currentPlace[0] = 0;}
				
				
			}//end while
			while(currentGoal[1] != currentPlace[1]) {
				if (currentGoal[1] < currentPlace[1]) {
					addCopies(outputStream, Input.joystickLeft, 40);
					addCopies(outputStream, Input.nothing, 40);
					currentPlace[1] = currentPlace[1] -1;
				} else {
					addCopies(outputStream, Input.joystickRight, 40);
					addCopies(outputStream, Input.nothing, 40);
					currentPlace[1] = currentPlace[1] + 1;
				}
			}
			addCopies(outputStream, Input.buttonA, 40);
			addCopies(outputStream, Input.nothing, 40);
			
			if(currentPlace[0] == 1) { currentPlace[0] = 0; currentPlace[1] = currentPlace[1] + 1;}
			else { currentPlace[0] = 1; }
			
		}//end for
		
		int btDirection = Input.charactertoBT[selectedCharacters[0]];
		
		while(btDirection != 0) {
			if(btDirection < 0) {
				addCopies(outputStream, Input.joystickLeft, 40);
				addCopies(outputStream, Input.nothing, 40);
				btDirection++;
			}else {
				addCopies(outputStream, Input.joystickRight, 40);
				addCopies(outputStream, Input.nothing, 40);
				btDirection--;
			}
			
		}//end while
		
		addCopies(outputStream, Input.buttonA, 40);
		addCopies(outputStream, Input.nothing, 300);
	}//end method
	private static void courseSelect(FileOutputStream outputStream) {
		int courseID =  allBytes.get(5251);
		
		int[] directions = new int[2];
		switch (courseID) {
			case 36: directions[0] = 0; directions[1] = 0; break;
			case 34: directions[0] = 0; directions[1] = 1; break;
			case 33: directions[0] = 0; directions[1] = 2; break;
			case 50: directions[0] = 0; directions[1] = 3; break;
			
			case 40: directions[0] = 1; directions[1] = 0; break;
			case 37: directions[0] = 1; directions[1] = 1; break;
			case 35: directions[0] = 1; directions[1] = 2; break;
			case 42: directions[0] = 1; directions[1] = 3; break;
			
			case 51: directions[0] = 2; directions[1] = 0; break;
			case 41: directions[0] = 2; directions[1] = 1; break;
			case 38: directions[0] = 2; directions[1] = 2; break;
			case 45: directions[0] = 2; directions[1] = 3; break;
			
			case 43: directions[0] = 3; directions[1] = 0; break;
			case 44: directions[0] = 3; directions[1] = 1; break;
			case 47: directions[0] = 3; directions[1] = 2; break;
			case 49: directions[0] = 3; directions[1] = 3; break;
			
			default: directions[0] = 1; directions[1] = 3; break;
		}
		
		for(int i = 0; i < directions[0]; i ++) {
			addCopies(outputStream, Input.joystickRight, 40);
			addCopies(outputStream, Input.nothing, 40);
		}
		addCopies(outputStream, Input.buttonA, 40);
		addCopies(outputStream, Input.nothing, 40);
		
		for(int i = 0; i < directions[1]; i ++) {
			addCopies(outputStream, Input.joystickDown, 40);
			addCopies(outputStream, Input.nothing, 40);
		}
		addCopies(outputStream, Input.buttonA, 40);
		addCopies(outputStream, Input.nothing, 40);
		addCopies(outputStream, Input.buttonA, 40);
		addCopies(outputStream, Input.nothing, 815);
	}
	
	
}//end class

