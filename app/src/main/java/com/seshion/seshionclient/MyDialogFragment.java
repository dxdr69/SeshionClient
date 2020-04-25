package com.seshion.seshionclient;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyDialogFragment extends DialogFragment {

    int styleNum;
    int themeNum;
    String messageText;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static MyDialogFragment newInstance(int styleNum, int themeNum, String message) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("styleNum", styleNum);
        args.putInt("themeNum", themeNum);
        args.putString("message", message);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeNum = getArguments().getInt("themeNum");
        styleNum = getArguments().getInt("styleNum");
        messageText = getArguments().getString("message");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch (styleNum) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch (themeNum) {
            case 1: theme = android.R.style.Theme_Holo; break;
            case 2: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 3: theme = android.R.style.Theme_Holo_Light; break;
            case 4: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 5: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        View tv = v.findViewById(R.id.text);

//            ((TextView)tv).setText("Dialog #" + mNum + ": using style "
//                    + getNameForNum(mNum));

        ((TextView)tv).setText(messageText);

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.okaybutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                //((FragmentDialog)getActivity()).showDialog();
                //((Activity)getActivity()).;
                dismiss();
            }
        });

        return v;
    }
}