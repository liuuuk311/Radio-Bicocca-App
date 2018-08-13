package com.radiobicocca.android.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.radiobicocca.android.R;

import java.net.InetAddress;

/**
 * Created by lucap on 2/27/2018.
 */

public class ConnectionDetector {

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static void showNoConnectionError(Context context){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.MyDialog)
                .setMessage(R.string.err_no_connection)
                .setTitle(R.string.err_no_connection_title)
                .setPositiveButton(R.string.pos_dialog_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertBuilder.show();
    }
}
