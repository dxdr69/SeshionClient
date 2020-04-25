package com.seshion.seshionclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;



public class LocationBroadcastReceiver extends BroadcastReceiver {
    UserAccount loggedInUser;

    public LocationBroadcastReceiver(UserAccount loggedInUser) {
        super();
        this.loggedInUser = loggedInUser;


    }

    @Override
    public void onReceive(Context context, Intent intent) {



    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
