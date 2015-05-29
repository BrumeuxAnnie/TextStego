package com.annie.brumeuxannie.textstego;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.annie.brumeuxannie.textstego.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {


    L objM = new L();

    /**
     * Button for Hide Message Activity
     */
    private Button mHideBtn;

    /**
     * Button for Reveal Data Activity
     */
    private Button mRevealBtn;

    Toolbar toolbar;
    String picturePath;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setUpButtonListeners();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {

            //to open gallery to select image to share
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 10);
        }

        // Return true to display menu
        return super.onOptionsItemSelected(item);
    }
    //toolbar settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //to select image from gallery and set it in the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("enter the onActivityREsult", "hello");

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10 || requestCode == 20)
                && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.MediaColumns.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();
            try {
                objM.stream = getContentResolver().openInputStream(
                        data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        share("gmail", picturePath, "your comment");

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

    private void init() {

        mHideBtn = (Button) findViewById(R.id.btext);
        mRevealBtn = (Button) findViewById(R.id.button_reveal_msg);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

    }

    private void setUpButtonListeners() {

        mRevealBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openClass(RevealInfo.class);
            }
        });
        mHideBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                openClass(TextHide.class);
            }
        });
    }

    private void openClass(Class nextClass) {
        Intent i = new Intent(getApplicationContext(), nextClass);
        startActivity(i);
    }
}