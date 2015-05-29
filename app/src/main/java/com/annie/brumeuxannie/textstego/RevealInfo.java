package com.annie.brumeuxannie.textstego;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.FileNotFoundException;

public class RevealInfo extends ActionBarActivity implements OnClickListener {


    /**button to reveal message from image*/
    private Button mRevealBtn;

    /**button to choose image that contains message*/
    private Button  mChooseImageBtn;
    /**
     * to display selected image
     */
    ImageView myImage;

    /**to display the secret message*/
    TextView myTextView;

    public L obj = new L();

    /**
     * text displayed when no image is selected and revealed button is pressed
     */
    String myText = "You have not selected any image";

    public L object = new L();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reveal_page);

        obj.bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);


        //function containing widget declaration and listeners
        init();

        setUpButtonListeners();

        obj.advertisement();
    }


    /**
     * Get the size of the Image view after the Activity has completely loaded
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        obj.xDim = myImage.getWidth();
        obj.yDim = myImage.getHeight();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {


        @Override
        //to cancel the task if info is not available
        //called when cancel(true) is executed
        protected void onCancelled() {
            myImage.setAlpha(120);
            myTextView.setVisibility(View.VISIBLE);
            myTextView.setText("Your image does not contain any information");
            myText = "";
            obj.running = false;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            //to check if the selected image contains information
            checkData();

            /**to store 15 bit length, 15 pixels*/
            int[] pixelVal = new int[15];

            for (int i = 0; i <= 14; i++) {
                // storing pixel value of first 15 pixels to extract length
                pixelVal[i] = Color.red(obj.bmp.getPixel(0, i));

                obj.eqtBin = 0;//to store the binary eqt

                // converting extracted pixel to binary
                obj.eqtBin = Integer.parseInt(Integer.toBinaryString(pixelVal[i]));
                Log.i("pixel",
                        Integer.toString(pixelVal[i]) + "="
                                + Integer.toString(obj.eqtBin));

                /** storing lsb of 15 pixels to extract length */
                obj.binary15DigLength[i] = obj.eqtBin % 2;

            }

            //to determine the decimal value of extracted binary length
            obj.len = convertPixelsToDecimal(obj.binary15DigLength);

            Log.i("length",
                    Integer.toString(obj.len) + "="
                            + Integer.toString(obj.binary15DigLength[0])
                            + Integer.toString(obj.binary15DigLength[1])
                            + Integer.toString(obj.binary15DigLength[2])
                            + Integer.toString(obj.binary15DigLength[3])
                            + Integer.toString(obj.binary15DigLength[4])
                            + Integer.toString(obj.binary15DigLength[5])
                            + Integer.toString(obj.binary15DigLength[6])
                            + Integer.toString(obj.binary15DigLength[7])
                            + Integer.toString(obj.binary15DigLength[8])
                            + Integer.toString(obj.binary15DigLength[9])
                            + Integer.toString(obj.binary15DigLength[10])
                            + Integer.toString(obj.binary15DigLength[11])
                            + Integer.toString(obj.binary15DigLength[12])
                            + Integer.toString(obj.binary15DigLength[13])
                            + Integer.toString(obj.binary15DigLength[14]));

            /**no of characters=length
             * no of bits in in character=8
             * total no of bits to be extracted  = (length*8)*/
            obj.extractCount = obj.len * 8;

             obj.width = obj.bmp.getWidth();

            obj.countRowColumn();


            obj.m = 15;// 14 pixel already accessed for length

            /**to extract the pixels containing the infornation of length no of characters, each of 8 bits*/
            int[][] extractedPixel = new int[obj.len][8];

            obj.messageData = new int[obj.len];
            int c = 0;// to access through columns of lsbBit=8
            int len = 0;// to move rows of lsbBit=length
            int lsbBit[][] = new int[obj.len][8];

            for (int j = 0; j < obj.row; j++) {
                for (int k = 0; k < obj.column; k++) {
                    //pixel extracted
                    extractedPixel[len][c] = Color.red(obj.bmp.getPixel(j, obj.m));

                    //lsb of the extracted pixel taken
                    lsbBit[len][c] = takeLsbOfBinPixel(extractedPixel[len][c]);
                    if (obj.m <  obj.width)
                        obj.m++;
                    else
                        obj.m = 0;
                    if (c == 7) {
                        c = 0;
                        len++;// to move to next row for next character
                    } else {
                        c++;// to move to next column
                    }
                }

                //the lsb bits extracted
                //convert the binary information to decimal ascii value
                convertToDecimal(lsbBit);

                Log.i("info", "converted to decimal");
                for (int l = 0; l < obj.len; l++) {
                    Log.i("pixel",
                            Integer.toString(obj.messageData[l]) + "="
                                    + Integer.toString(lsbBit[l][0])
                                    + Integer.toString(lsbBit[l][1])
                                    + Integer.toString(lsbBit[l][2])
                                    + Integer.toString(lsbBit[l][3])
                                    + Integer.toString(lsbBit[l][4])
                                    + Integer.toString(lsbBit[l][5])
                                    + Integer.toString(lsbBit[l][6])
                                    + Integer.toString(lsbBit[l][7]));

                    myText = myText + (char) obj.messageData[l];
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            Log.i("result", "String is" + myText);
            myImage.setAlpha(120);
            myTextView.setVisibility(View.VISIBLE);
            myTextView.setText(myText);
            myText = "";
        }

        /**
         * to convert binary pixels to decimal
         */
        private void convertToDecimal(int[][] lsbBit) {

            for (int s = 0; s < obj.len; s++) {

                /**to store the eqt decimal value of 8 bit binary for length no of times*/
                int eqtPixelDec = 0;

                int y = 0;//y<=7, for exponent of 2 in b2d conversion

                //converting binary pixels to decimal
                //8 bit binary=1 decimal ascii value
                for (int i = 7; i >= 0; i--) {
                    eqtPixelDec = (eqtPixelDec)
                            + (int) (lsbBit[s][i] * Math.pow(2, y));
                    y++;
                }

                Log.i("pixel", Integer.toString(eqtPixelDec));

                //stores the decimal value of binry pixels in array
                obj.messageData[s] = eqtPixelDec;
            }
        }

        /**
         * convert the extracted pixel value to binary
         */
        private int takeLsbOfBinPixel(int n) {

            obj.eqtBin = Integer.parseInt(Integer.toBinaryString(n));

            //to return the lsb if the binary pixel
            return (obj.eqtBin % 2);
        }

        /**length is 15 bit binary
         * convert 15 bit binary to its decimal value*/
        private int convertPixelsToDecimal(int[] bin15Bitlength) {

            int len = 0, y = 0;
            for (int j = 14; j >= 0; j--) {
                // character
                len = (len) + (int) (bin15Bitlength[j] * Math.pow(2, y));
                y++;
            }
            return len;
        }

        //to check if info is available
        public void checkData() {
            int pix;
            //script2[] is the data encrytped in green pix of the image
            //if this info is available in the image, the image contains the info
            //if there z no such encryption in image, there is no info available
            int script2[] = {78, 69, 65, 76};
            for (int i = 0; i < 4; i++) {
                //extracts the green pix value of image
                pix = Color.green(obj.bmp.getPixel(0, i));

                //check if the extracted green pix value is same as the encrypted value
                //if it is not same, there is no info available
                //cancel the operation
                //execute onCancelled()
                if (pix != script2[i])
                    cancel(true);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.reveal_button) {

            //on clicking reveal ,async task is called and executed
            MyTask myTask = new MyTask();
            myTask.execute();
        }

        if (v.getId() == R.id.choose_encoded_image) {


            myImage.setAlpha(getResources().getDrawable(R.drawable.ic_launcher).getAlpha());
            myText = "";
            myTextView.setVisibility(View.GONE);
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 10);
        }
    }


    //to select image from gallery and set it in the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("enter the onActivityREsult", "hello");

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10 || requestCode == 20)
                && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePath = {MediaColumns.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            try {
                object.stream = getContentResolver().openInputStream(
                        data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (requestCode == 10) {
                obj.bmp = (BitmapFactory.decodeStream(object.stream));
                Log.i("path of image from gallery......******************.........",
                        picturePath + "");
                myImage.setImageBitmap(decodeSampledBitmapFromResource(
                        getResources(), picturePath, obj.xDim, obj.yDim));

            }
        }
    }

    private Bitmap decodeSampledBitmapFromResource(Resources resources,
                                                   String picturePath, int xDim2, int yDim2) {
        /** First decode with inJustDecodeBounds=true to check dimensions */
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        /** Calculate inSampleSize */
        options.inSampleSize = calculateInSampleSize(options, xDim2, yDim2);
        /** Decode bitmap with inSampleSize set */
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    private int calculateInSampleSize(Options options, int xDim2, int yDim2) {
        int inSampleSize = 1; // Default subsampling size
        // See if image raw height and width is bigger than that of required
        // view
        if (options.outHeight > xDim2 || options.outWidth > yDim2) {
            // bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > xDim2
                    && (halfWidth / inSampleSize) > yDim2) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    private void init() {

        mChooseImageBtn = (Button) findViewById(R.id.choose_encoded_image);
        mRevealBtn = (Button) findViewById(R.id.reveal_button);
        myImage = (ImageView) findViewById(R.id.revealation_image);
        myTextView = (TextView) findViewById(R.id.textReveal);
        myTextView.setVisibility(View.INVISIBLE);



        obj.toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(obj.toolbar);


        // Prepare the Interstitial Ad
        obj.interstitial = new InterstitialAd(RevealInfo.this);
// Insert the Ad Unit ID
        obj.interstitial.setAdUnitId("ca-app-pub-71720509/214813");
        //Locate the Banner Ad in activity_main.xml
        obj.adView = (AdView) this.findViewById(R.id.adView);

    }

    private void setUpButtonListeners() {
        mRevealBtn.setOnClickListener(this);
        mChooseImageBtn.setOnClickListener(this);
    }
}
