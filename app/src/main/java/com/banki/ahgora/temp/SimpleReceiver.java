package com.banki.ahgora.temp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.banki.ahgora.ContadorActivity;

public class SimpleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent it) {
        String valor = it.getStringExtra("valor");

        Intent intent = new Intent(ctx, ContadorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("valor", valor);
        ctx.startActivity(intent);
    }
}
