package com.annie.brumeuxannie.textstego;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;

public class L {
	
	
	/** equivalent binary of input characters */
	public int eqtBin = 0;
	
	/** My variable to sTOre the entered data */
	public String enteredString;
	
	/** edit text to enter data */
	public EditText ethideText;
	
ProgressBar seePogress;
	
	/** imageview to display the chosen image */
	public ImageView myImage;

	/** to accept input image */
	public InputStream stream;
	
	/** length of input string */
	int len;

	/** to store the changed decimal value of pixel */
	int dec;
	
	/** to count the no of pixels to b extracted */
	int extractCount = 0;

	/** explicit intent */
	Intent i;
	
	/** array to store the ascii values of each input character */
	int[] messageData = new int[10];
	
	/** array to store the 8 bit binary eqt of the red pixel values extracted */
	int[][] bin8BitRedPixel = new int[50 * 50][8];
	
	/** array to store 15 bit eqt of string length */
	int binary15DigLength[] = new int[15];

	/**
	 * array to store binary equivalent of the string in 8 bit,of 100 characters
	 */
	int binary8Dig[][] = new int[10][8];
	
	
	/** button to reveal text */
	public Button reveal;
	/** button to hide text */
	public Button hideTxt;
	/** button to choose pic from gallery */
	public Button picLibChoose;
	/** button to take new pic */
	public Button picTakeNew;
	
	/** to store the image */
	Bitmap bmp;
	
	public static void l(String tag, String message) {
		Log.i(tag, message);

	}

	public static void t(Context context,  String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
