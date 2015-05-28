package com.example.brumeuxannie.textstego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TextHide extends ActionBarActivity implements OnClickListener {


    private ShareActionProvider mShareActionProvider;

    private Toolbar toolbar;

    /**
     * to keep track of pixel to be accessed
     */
    int m = 0;

    int width = 0, height = 0;
    int row, column;

    int xDim, yDim;

    File file;
    File myDir;

    public L object = new L();

    int decimalPixel[] = new int[5];

    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texthide);

        widgetsEventHandling();
        object.bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.untitled);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        /** to set changes in bitmap, change it to mutable */
        object.bmp = object.bmp.copy(Bitmap.Config.ARGB_8888, true);

    }


    @Override
    /**Get the size of the Image view after the Activity has completely loaded*/
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        xDim = object.myImage.getWidth();
        yDim = object.myImage.getHeight();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            pd = new ProgressDialog(TextHide.this);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            checkScript();

            try {
                // fething the string and taking its length
                object.enteredString = object.ethideText.getText().toString();
                object.len = object.enteredString.length();

                object.extractCount = 15 + (object.len * 8);

                Log.i("length", Integer.toString(object.len));

                object.messageData = new int[object.len];

                object.binary8Dig = new int[object.len][8];

                /** to store ascii values of each character in the array */
                for (int i = 0; i < object.len; i += 1)
                    object.messageData[i] = object.enteredString.charAt(i);

                width = object.bmp.getWidth();
                height = object.bmp.getHeight();

                // to count the no of rows and columns of the image to be accessed
                if (object.extractCount < width) {
                    //if the no of bits<width of image, no of columns to access =no of bits
                    //it is over in the 1st row itself
                    column = object.extractCount;
                    row = 1;
                } else {
                    //if bits>width, whole column is to b accessed
                    //no of rows =
                    column = width;
                    row = ( object.extractCount / width);
                }

                /** arrays to store the values of pixels of image */
                int[][] pixVal = new int[row][width];

                /** to convert length to binary n */
                covertTo15Digit(object.len);

                for (int y = 0; y < object.len; y++) {
                    /**
                     * to extract elements from the string array to store the 8 bits
                     */

                    /** store character's 8 digit binary eqt */

                    // to convert the binary eqt to 8 digit
                    convertTo8Digit(object.messageData[y], y);
                }

                for (int j = 0; j < row; j++) {
                    /**
                     * to continue to decode as many pixels only as the length of
                     * the string and 1 for length
                     */

                    /**
                     * Controls movement through the image vertically
                     */
                    for (int k = 0; k < column; k++) {

                        /**
                         * Controls movement through the image horizontally
                         */
                        // extract rgb values of d image

                        pixVal[j][k] = Color.red(object.bmp.getPixel(j, k));

                        // to convert each red value to its eqt binary bits
                        convertPixelTo8Digit(pixVal[j][k], m);
                        m++;
                    }

                }
                // end of for loop for pixel extraction

                //to compare and modify the pixels in array
                compareChangeAndHide();

                //to set the change in image by settting the pixel as the modified pixel array
                changeImagePixel(decimalPixel);

                //to save the mdified image
                saveToStorage();

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {


            if (pd != null)
                pd.dismiss();
            object.myImage.setImageBitmap(object.bmp);


            Log.i("Hello", "image set");
            Toast.makeText(TextHide.this, "string hidden and saved", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    public void checkScript() {
        //to encript 4 charcters each of 7 bit binary ascii value
        int pix;
        int script1[] = {78, 69, 65, 76};
        for (int i = 0; i < 4; i++) {
            pix = script1[i];
            object.bmp.setPixel(0, i, Color.argb(Color.alpha(object.bmp.getPixel(0, i)),
                    Color.red(object.bmp.getPixel(0, i)), pix,
                    Color.blue(object.bmp.getPixel(0, i))));
            // bin = Integer.parseInt(Integer.toBinaryString(pix));
        }

    }


    /**
     * @param n - pixel value
     * @param y - the position of pixel
     */
    public void convertPixelTo8Digit(int n, int y) {

        // conversion to binary
        object.eqtBin = Integer.parseInt(Integer.toBinaryString(n));

        // conversion to 8 bit for storing in array
        for (int l = 7; (l >= 0); l--) {
            object.bin8BitRedPixel[y][l] = object.eqtBin % 10;
            object.eqtBin = object.eqtBin / 10;
        }
    }

    /**
     * @param n -eqt binary of character at y position
     * @param y -character index of the string
     */
    public void convertTo8Digit(int n, int y) {

        //converting to binary
        object.eqtBin = Integer.parseInt(Integer.toBinaryString(n));


        //converting to 8 bit
        for (int l = 7; (l >= 0); l--) {
            object.binary8Dig[y][l] = object.eqtBin % 10;
            object.eqtBin = object.eqtBin / 10;
        }
    }

    /**
     * to convert length to 15 bit binary value
     *
     * @param n -length of the entered string
     */
    private void covertTo15Digit(int n) {

        //coverting to binary
        object.eqtBin = Integer.parseInt(Integer.toBinaryString(n));
        //converting to 15 bits
        Log.i("length", Integer.toString(object.eqtBin));
        for (int x = 14; (x >= 0); x--) {
            object.binary15DigLength[x] = object.eqtBin % 10;// storing 0 in
            // array
            object.eqtBin = object.eqtBin / 10;
        }
    }

    /**
     * to convert modified pixel bit array to decimal
     *
     * @param bin8BitRedPixel -to fetch the array
     *                        - 15 for length 8 for characters
     * @return eqt binary
     */
    private int convertPixelsToDecimal(int[] bin8BitRedPixel) {

        int eqtPixelDec = 0;
        int y = 0;
        for (int j = 7; j >= 0; j--) {
            // character
            eqtPixelDec = (eqtPixelDec)
                    + (int) (bin8BitRedPixel[j] * Math.pow(2, y));
            y++;
        }

        return eqtPixelDec;
    }

    private void compareChangeAndHide() {

        /** to access the rows */
        int m = 0;

        /**loop variable*/
        int i;

        //size of array= no of bits hidden
        decimalPixel = new int[object.extractCount];

        /** to access the lsb of binary pixel value */
        int p = 0;

        // hide the length of 15 bit length
        // lsb bit z the bit at index no 7
        for (i = 0; i <= 14; i++) {
            p = object.bin8BitRedPixel[m][7];

            //to compare and modify the lsb, comparing with data bit
            if (object.binary15DigLength[i] != object.bin8BitRedPixel[m][7]) {
                if ((p / 1) == 1)
                    object.bin8BitRedPixel[m][7] = 0;
                else
                    object.bin8BitRedPixel[m][7] = 1;
            }//lsb modified

            //to convert modified binary pixel to decimal value
            //1 pixel = 8 bit binary
            int[] col = new int[8];
            for (int k = 7; k >= 0; k--) {
                col[k] = object.bin8BitRedPixel[m][k];
            }

            /**decimal value of modified pixel*/
            decimalPixel[m] = convertPixelsToDecimal(col);

            m++;// to move to next pixel
        }//15 bit length hidden in 1st 15 pixels by changing the lsb
        //1 pixel for 1 bit



        // hide the string in the consecutive rows
        for (int j = 0; j < object.len; j++) {// loop to access each character

            for (i = 0; i <= 7; i++) {// loop to hide 8 bit of 1 character
                p = object.bin8BitRedPixel[m][7];// to access the lsb of binary
                // pixel value

                //compare and change lsb of pixel binary array
                if (object.binary8Dig[j][i] != object.bin8BitRedPixel[m][7]) {
                    if ((p / 1) == 1)
                        object.bin8BitRedPixel[m][7] = 0;
                    else
                        object.bin8BitRedPixel[m][7] = 1;
                }

                /** to display the modified pixel value,convert to 1d array */
                //to convert modified binary pixel to decimal value
                //1 pixel = 8 bit binary
                int[] col = new int[8];
                for (int k = 7; k >= 0; k--) {
                    col[k] = object.bin8BitRedPixel[m][k];
                }

                decimalPixel[m] = convertPixelsToDecimal(col);
                m++;// to move to next pixel
            }

        }
    }

    /**
     * to modify the image in accordance with the value of changed pixel
     * here the change in set in the image
     */
    private void changeImagePixel(int[] decimalPixel) {

        int m = 0;

        for (int j = 0; j < row; j++) {
            /**
             * Controls movement through the image vertically
             */
            for (int k = 0; k < column; k++) {

                /**
                 * Controls movement through the image horizontally
                 */
                // extract rgb values of d image

                int pixelValue = object.bmp.getPixel(j, k);

                //change the image pixel
                //only red changes, others remaining the same
                object.bmp.setPixel(j, k, Color.argb(Color.alpha(pixelValue),
                        decimalPixel[m], Color.green(pixelValue),
                        Color.blue(pixelValue)));
                m++;
            }
        }
        Log.i("congratulations", "image successfully modified");
    }

    private void saveToStorage() {
        Log.i("Hello", "image compression");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();

        myDir = new File(root + "/stego");

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        file = new File(myDir, imageFileName + ".png");

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);

            object.bmp.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });

        Log.i("congratulations", "Saved to your folder");
    }

    private void widgetsEventHandling() {

        object.hideTxt = (Button) findViewById(R.id.bhidetext);
        object.picLibChoose = (Button) findViewById(R.id.b_img_lib_text);
        object.picTakeNew = (Button) findViewById(R.id.b_img_camera_text);
        object.ethideText = (EditText) findViewById(R.id.etText);
        object.myImage = (ImageView) findViewById(R.id.display_image);

        object.hideTxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyTask myTask = new MyTask();
                myTask.execute();
            }
        });

        object.picTakeNew.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                object.i = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(object.i, 5);
            }
        });
        object.picLibChoose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 10);
            }
        });
    }

    // code to take the image ater selecting he image source
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("enter the onActivityREsult", "hello");

        super.onActivityResult(requestCode, resultCode, data);

        // if camera selected
        if (requestCode == 5 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            object.bmp = (Bitmap) extras.get("data");
            object.myImage.setImageBitmap(object.bmp);
        }

        // if gallery selected
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
                object.bmp = (BitmapFactory.decodeStream(object.stream));
                Log.i("path of image from gallery......******************.........",
                        picturePath + "");
                object.myImage.setImageBitmap(decodeSampledBitmapFromResource(
                        getResources(), picturePath, xDim, yDim));

            }

        }

        /** to set changes in bitmap, change it to mutable */
        object.bmp = object.bmp.copy(Bitmap.Config.ARGB_8888, true);
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

    @Override
    public void onClick(View v) {
    }


    //toolbar settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {

            File filePath = new File(String.valueOf(file));
            share("gmail", filePath.toString(), "your comment");

        }
        if (id == R.id.action_settings) {
            //codes for DialogFragment
            DialogFragment dialog = new MyDialogFragment();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }


        // Return true to display menu
        return super.onOptionsItemSelected(item);
    }

    public void share(String nameApp, String imagePath, String message) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("message/rfc822");
            share.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{""});

            share.putExtra(Intent.EXTRA_SUBJECT, "subject");
            share.putExtra(Intent.EXTRA_TEXT, "compose mail");
            List<ResolveInfo> resInfo = getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(
                            android.content.Intent.ACTION_SEND);
                    targetedShare.setType("message/rfc822"); // put here your mime
                    // type
                    if (info.activityInfo.packageName.toLowerCase().contains(
                            nameApp)
                            || info.activityInfo.name.toLowerCase().contains(
                            nameApp)) {
                        share.putExtra(Intent.EXTRA_SUBJECT, "subject");
                        share.putExtra(Intent.EXTRA_TEXT, "compose mail");
                        targetedShare.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(new File(imagePath)));
                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(
                        targetedShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }
        } catch (Exception e) {
            Log.v("VM",
                    "Exception while sending image on" + nameApp + " "
                            + e.getMessage());
        }
    }


}
