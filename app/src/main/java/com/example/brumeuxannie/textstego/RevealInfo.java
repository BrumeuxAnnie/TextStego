package com.example.brumeuxannie.textstego;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class RevealInfo extends ActionBarActivity implements OnClickListener {

	Button reveal, chooseImage;
	/** to display selected image */
	ImageView myImage;

	Bitmap bmp;

	TextView myTextView;

	String myText = "you hvnt selected any image";

	/** variable to store eqt binary bit, used in diff places */
	int eqtBin = 0;

	/** to store decimal ascii of character */
	int[] decimal;

	/** to store the length of the text */
	int length = 0;

	public L object = new L();
	int xDim, yDim;
	/**
	 * to count the no of pixels to be extracted after finding length of the
	 * hidden text
	 */
	int extractCount = 0;

	/**
	 * to keep count of no of rows and columns to b accessed in d image for
	 * extraction
	 */
	int row, column;

	/** to keep count of pixels */
	int m = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reveal_page);


		bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		widgetsEventHandling();
	}

	private void widgetsEventHandling() {
		reveal = (Button) findViewById(R.id.reveal_button);
		chooseImage = (Button) findViewById(R.id.choose_encoded_image);
		myImage = (ImageView) findViewById(R.id.revealation_image);
		reveal.setOnClickListener(this);
		chooseImage.setOnClickListener(this);

		myTextView = (TextView) findViewById(R.id.textReveal);

		myTextView.setVisibility(View.INVISIBLE);
	}

	/** Get the size of the Image view after the Activity has completely loaded */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		xDim = myImage.getWidth();
		yDim = myImage.getHeight();
	}

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {

			int[] pixelVal = new int[15];
			int[] decodeLengthBit = new int[15];
			for (int i = 0; i <= 14; i++) {
				// storing pixel value of first 15 pixels to extract length
				pixelVal[i] = Color.red(bmp.getPixel(0, i));
				int n = pixelVal[i];
				eqtBin = 0;
				// converting to binary
				int mul = 1;
				while (n != 0) {
					eqtBin = ((n % 2) * mul) + eqtBin;
					n = n / 2;
					mul = mul * 10;
				}
				Log.i("pixel",
						Integer.toString(pixelVal[i]) + "="
								+ Integer.toString(eqtBin));
				/** storing lsb of 15 pixels to extract length */
				decodeLengthBit[i] = eqtBin % 2;

			}
			length = convertPixelsToDecimal(decodeLengthBit);

			Log.i("length",
					Integer.toString(length) + "="
							+ Integer.toString(decodeLengthBit[0])
							+ Integer.toString(decodeLengthBit[1])
							+ Integer.toString(decodeLengthBit[2])
							+ Integer.toString(decodeLengthBit[3])
							+ Integer.toString(decodeLengthBit[4])
							+ Integer.toString(decodeLengthBit[5])
							+ Integer.toString(decodeLengthBit[6])
							+ Integer.toString(decodeLengthBit[7])
							+ Integer.toString(decodeLengthBit[8])
							+ Integer.toString(decodeLengthBit[9])
							+ Integer.toString(decodeLengthBit[10])
							+ Integer.toString(decodeLengthBit[11])
							+ Integer.toString(decodeLengthBit[12])
							+ Integer.toString(decodeLengthBit[13])
							+ Integer.toString(decodeLengthBit[14]));

			extractCount = length * 8;

			int width = bmp.getWidth();

			/** to keep count of no of rows to access in the image */
			if (extractCount < width) {
				column = extractCount;
				row = 1;
			} else {
				column = width;
				row = (extractCount / width);
			}

			m = 15;// 14 pixel already accessed for length
			int[][] extractedPixel = new int[length][8];

			decimal = new int[length];
			int c = 0;// to access through columns of lsbBit=8
			int len = 0;// to move rows of lsbBit=length
			int lsbBit[][] = new int[length][8];

			for (int j = 0; j < row; j++) {
				for (int k = 0; k < column; k++) {
					extractedPixel[len][c] = Color.red(bmp.getPixel(j, m));
					lsbBit[len][c] = convertToBinary(extractedPixel[len][c]);
					m++;
					if (c == 7) {
						c = 0;
						len++;// to move to next row for next character
					} else {
						c++;// to move to next column
					}
				}

				convertToDecimal(lsbBit);

				Log.i("info", "converted to decimal");
				for (int l = 0; l < length; l++) {
					Log.i("pixel",
							Integer.toString(decimal[l]) + "="
									+ Integer.toString(lsbBit[l][0])
									+ Integer.toString(lsbBit[l][1])
									+ Integer.toString(lsbBit[l][2])
									+ Integer.toString(lsbBit[l][3])
									+ Integer.toString(lsbBit[l][4])
									+ Integer.toString(lsbBit[l][5])
									+ Integer.toString(lsbBit[l][6])
									+ Integer.toString(lsbBit[l][7]));

					myText = myText + (char) decimal[l];
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i("result", "String is" + myText);
			myTextView.setVisibility(View.VISIBLE);

				myTextView.setText(myText);
				myText = "";
		}

		private void convertToDecimal(int[][] lsbBit) {

			for (int s = 0; s < length; s++) {
				int eqtPixelDec = 0;
				int y = 0;
				for (int i = 7; i >= 0; i--) {
					eqtPixelDec = (eqtPixelDec)
							+ (int) (lsbBit[s][i] * Math.pow(2, y));
					y++;
				}
				Log.i("pixel", Integer.toString(eqtPixelDec));
				decimal[s] = eqtPixelDec;
			}
		}

		private int convertToBinary(int n) {

			int p = n;
			int i = 10;
			eqtBin = n % 2;
			n = n / 2;
			while (n != 0) {
				eqtBin = ((n % 2) * i) + eqtBin;
				n = n / 2;
				i = i * 10;
			}
			return (eqtBin % 2);
		}

		private int convertPixelsToDecimal(int[] bin15Bitlength) {

			int len = 0, y = 0;
			for (int j = 14; j >= 0; j--) {
				// character
				len = (len) + (int) (bin15Bitlength[j] * Math.pow(2, y));
				y++;
			}
			return len;
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.reveal_button) {

			MyTask myTask = new MyTask();
			myTask.execute();
		}

		if (v.getId() == R.id.choose_encoded_image) {
			
			myText="";
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 10);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("enter the onActivityREsult", "hello");

		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == 10 || requestCode == 20)
				&& resultCode == Activity.RESULT_OK) {

			Uri selectedImage = data.getData();
			String[] filePath = { MediaColumns.DATA };
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (requestCode == 10) {
				bmp = (BitmapFactory.decodeStream(object.stream));
				Log.i("path of image from gallery......******************.........",
						picturePath + "");
				myImage.setImageBitmap(decodeSampledBitmapFromResource(
						getResources(), picturePath, xDim, yDim));

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
}
