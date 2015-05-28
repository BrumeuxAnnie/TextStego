package com.example.brumeuxannie.textstego;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Brumeux Annie on 25-05-2015.
 */
public class MyDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("About");
        builder.setMessage("This is the app to share secret messages by encrypting it into an image." +
                "When you share an image, be careful which app you chose" +
                "Apps like whatsapp compress your image and hence, your data will be lost" +
                "So use gmail, bluetooth or other sharing app in which your image doesn't get altered");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just want it dismissed when clicked
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
