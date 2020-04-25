/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seshion.seshionclient;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentDialog extends Activity {
    int mStackLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dialog);

        View tv = findViewById(R.id.text);
        ((TextView) tv).setText("Example of displaying dialogs with a DialogFragment.  "
                + "Press the show button below to see the first dialog; pressing "
                + "successive show buttons will display other dialog styles as a "
                + "stack, with dismissing or back going to the previous dialog.");

        // Watch for button clicks.
        Button button = (Button) findViewById(R.id.okaybutton);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            }
        });

        if (savedInstanceState != null) {
            mStackLevel = savedInstanceState.getInt("level");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }


    void showSessionCheckinDialog() {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = MyDialogFragment.newInstance(4, 2, "You've been checked into the seshion!");
        newFragment.show(ft, "dialog");
    }


    static String getNameForNum(int num) {
        switch ((num - 1) % 6) {
            case 1:
                return "STYLE_NO_TITLE";
            case 2:
                return "STYLE_NO_FRAME";
            case 3:
                return "STYLE_NO_INPUT (this window can't receive input, so "
                        + "you will need to press the bottom show button)";
            case 4:
                return "STYLE_NORMAL with dark fullscreen theme";
            case 5:
                return "STYLE_NORMAL with light theme";
            case 6:
                return "STYLE_NO_TITLE with light theme";
            case 7:
                return "STYLE_NO_FRAME with light theme";
            case 8:
                return "STYLE_NORMAL with light fullscreen theme";
        }
        return "STYLE_NORMAL";
    }

}



