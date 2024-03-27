package com.example.asteroides;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReceptorSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AcercaDeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(context, "SMS RECIBIDO", Toast.LENGTH_LONG).show();
        context.startActivity(i);
    }
}
