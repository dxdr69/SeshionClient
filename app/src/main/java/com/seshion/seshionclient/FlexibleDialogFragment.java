package com.seshion.seshionclient;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class FlexibleDialogFragment extends DialogFragment {
    private TextView messageTextView;
    private Button okayButton;

    /* empty constructor required */
    public FlexibleDialogFragment() {}


    public static FlexibleDialogFragment newInstance(String title, String message){
        FlexibleDialogFragment fdf = new FlexibleDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        fdf.setArguments(args);
        return fdf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seshion_checkin, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* text view*/
        messageTextView = (TextView) view.findViewById(R.id.loginmessagetextview);

        /* button */
        okayButton = (Button) view.findViewById(R.id.okaybutton);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "yoyoyo");
        getDialog().setTitle(title);

        String message = getArguments().getString("message", "yoyoyo");

        // Show soft keyboard automatically and request focus to field
        okayButton.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("You just checked into the sesh!!!")
//                .setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton("get me out of here...", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }

}
