package com.github.sureshcs_50.musicplayerapplication.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by adminaccount on 20/12/17.
 */

public class NetworkManager {

    // check network connection available or not.
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            SupplicantState wifiSupplicantState = getWifiSupplicantState(context);
            if (wifiNetwork != null && wifiNetwork.isConnectedOrConnecting())
                if (wifiSupplicantState.equals(SupplicantState.COMPLETED)) { // connected to WiFi..
                    return true;
                } else {
                    return false;
                }

            NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnected()) {
                return true;
            }

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }

        return false;
    }

    private static SupplicantState getWifiSupplicantState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSupplicantState();
        } else {
            return SupplicantState.INACTIVE;
        }
    }
}
