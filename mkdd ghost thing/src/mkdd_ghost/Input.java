package mkdd_ghost;

import java.nio.ByteBuffer;

import java.nio.ByteOrder;
import java.util.ArrayList;

import java.lang.Byte;

public class Input {
	public static final byte[] joystickLeft  = {00, 0, 28, 33, (byte)  25, (byte) 128, (byte) 135, (byte) 128};
	public static final byte[] joystickRight = {00, 0, 28, 33, (byte) 225, (byte) 128, (byte) 135, (byte) 128};
	public static final byte[] joystickUp    = {00, 0, 28, 33, (byte) 128, (byte) 225, (byte) 135, (byte) 128};
	public static final byte[] joystickDown  = {00, 0, 28, 33, (byte) 128, (byte)  25, (byte) 135, (byte) 128};
	public static final byte[] buttonA       = {02, 0, 28, 33, (byte) 128, (byte) 128, (byte) 135, (byte) 128};
	public static final byte[] buttonStart   = {01, 0, 28, 33, (byte) 128, (byte) 128, (byte) 135, (byte) 128};
	public static final byte[] nothing       = {00, 0, 28, 33, (byte) 128, (byte) 128, (byte) 135, (byte) 128};
	
	public static final int[][] characterCoordinates = { {0, 3}, {1, 3}, {0,4}, {1,4}, {0,5}, {1,5}, {1,6}, {1,7}};
	public static final int[] charactertoBT= { 4, 3, -2, -3, 2, 1, 0, -1};
	
	public static byte[] makeInput(byte[] joystick, byte button) {
		
		//convert buttons
		byte[] dtmInput = new byte[8];
		dtmInput[2] = 28;
		dtmInput[3] = 28;
		dtmInput[6] = (byte) 134;
		dtmInput[7]= (byte) 128;
		
		//int[] guiButtons = 		  {  1,   2,   4,   8,  16,  32,  64};
		//char[] potentialButtons = {'A', 'B', 'X', 'Y', 'L', 'R', 'Z'};
		//ArrayList<Integer> buttonsPressed = new ArrayList<Integer>();
		
		
		int[] dtmValues = {512, 1024, 2048, 4096, 12, 8, 8192};
		int buttonTotal = 0;
		int newButton =  Byte.toUnsignedInt(button);
		
		
		
		for(int i = 6; i > -1; i --) {
			//System.out.println(newButton);
			if(newButton >= Math.pow(2, i)) {
				//buttonsPressed.add(i);
				buttonTotal += dtmValues[i];
				newButton = (int) (newButton - Math.pow(2, i));
			}
		}
		
		//System.out.println(buttonTotal);
		
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(buttonTotal);
		//b.order(ByteOrder.LITTLE_ENDIAN);
		byte[] buttonBytes = b.array();
			
		dtmInput[0] = buttonBytes[2];
		dtmInput[1] = buttonBytes[3];
			
		dtmInput[4] = joystick[0];
		dtmInput[5] = joystick[1];
		
		
		return dtmInput;
		
	}
	public static int[] fixstick(byte joystick) {
		int[] validHorizontal = 
		{136, 144, 152, 160, 168, 176, 184, 192, 200, 208, 216, 224, 232, 240, 248, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120}; //<- gci inputs
		//{1, 2, 3, ..............................................................................................................................255} <- dtm inputs
		int[] validVertical = {5, 6, 7, 0, 1, 2, 3};
		int[] ret = new int[2];
		int newJoystick = Byte.toUnsignedInt(joystick);
		
		
			for(int i = 0; i < validHorizontal .length; i ++) {
				for (int j = 0; j < validVertical.length; j ++) {
					if(newJoystick == validHorizontal[i] + validVertical[j]) {
						
					
						if(i == 15 ) {
							ret[0] = 128;
						} else {
							int horiz = validHorizontal[i] - validVertical[j];
							if (horiz > 120) { horiz = horiz - 128;}
							if(i < 15) {
								horiz = Math.max(horiz, 37);
								ret[0] = horiz;
							}
							if(i > 15) {
								ret[0] = horiz + 129;
							}
						}
						/*
						if(i < 15) { //left side
							ret[0] = (int) ((90 * ((double) (i)/ (double) (14) ) ) + 37);
						} else if (i > 15){
							ret[0] = (int) ((90 * ((double) (i - 16)/ (double) (14) ) ) - 37);
						} else {
							ret[0] = 126;
							ret[1] = 132;
						}
						*/
						ret[1] = 0;
						break;
						
						
						
					}
				}
			}
		if (joystick ==0) {
			
		}
		
		return 	ret;
	}		
	
}
