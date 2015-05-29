package com.annie.brumeuxannie.textstego;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.InputStream;

public class L {

    /**
     * variable to store eqt binary bit, used in diff places
     * equivalent binary of input characters
     */
    public int eqtBin = 0;

    /**
     * My variable to sTOre the entered data
     */
    public String enteredString;

    public Toolbar toolbar;


    /**
     * edit text to enter data
     */
    public EditText ethideText;

    /**
     * imageview to display the chosen image
     */
    public ImageView myImage;

    /**
     * to accept input image
     */
    public InputStream stream;

    /**
     * length of input string
     */
    int len;

    /**
     * to handle the running state of async task
     */
    boolean running = true;
    /**

    /**
     * to store the changed decimal value of pixel
     * to store decimal ascii of character
     */
    int decimalPixel[] = new int[5];

    /**
     * to count the no of pixels to be extracted after finding length of the
     * hidden text
     */
    int extractCount = 0;

    /**
     * to keep count of no of rows and columns to b accessed in d image for
     * extraction
     */
    int width = 0, height = 0;
    int row, column;

    /**
     * to keep count of pixels
     */
    int m = 0;

    int xDim, yDim;


    /**
     * explicit intent
     */
    Intent i;

    /**
     * array to store the ascii values of each input character
     */
    int[] messageData = new int[10];

    /**
     * array to store the 8 bit binary eqt of the red pixel values extracted
     */
    int[][] bin8BitRedPixel = new int[50 * 50][8];

    /**
     * array to store 15 bit eqt of string length
     */
    int binary15DigLength[] = new int[15];

    /**
     * array to store binary equivalent of the string in 8 bit,of 100 characters
     */
    int binary8Dig[][] = new int[10][8];


    /**
     * button to reveal text
     */
    public Button reveal;
    /**
     * button to hide text
     */
    public Button hideTxt;
    /**
     * button to choose pic from gallery
     */
    public Button picLibChoose;
    /**
     * button to take new pic
     */
    public Button picTakeNew;

    /**
     * to store the image
     */
    Bitmap bmp;

    public InterstitialAd interstitial;

    AdView adView;


    public static void l(String tag, String message) {
        Log.i(tag, message);

    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public void countRowColumn(){

        // to count the no of rows and columns of the image to be accessed
        if (extractCount <  width) {
            //if the no of bits<width of image, no of columns to access =no of bits
            //it is over in the 1st row itself
            column = extractCount;
            row = 1;
        } else {
            //if bits>width, whole column is to b accessed
            //no of rows =
            column =  width;
            row =  extractCount/ width;
        }
    }
    public void advertisement() {


// Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();


// Load ads into Banner Ads
        adView.loadAd(adRequest);

// Load ads into Interstitial Ads
        interstitial.loadAd(adRequest);

// Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
// Call displayInterstitial() function
                displayInterstitial();
            }
        });

    }

    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
