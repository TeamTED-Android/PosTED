package com.example.posted.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.posted.interfaces.NetworkStateReceiverListener;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {

    private List<NetworkStateReceiverListener> mNetworkListeners;
    private Boolean mConnected;

    public NetworkStateReceiver() {
        this.mNetworkListeners = new ArrayList<>();
        this.mConnected = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            this.mConnected = true;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            this.mConnected = false;
        }

        notifyStateToAll();
    }

    public void addListener(NetworkStateReceiverListener networkListener){
        this.mNetworkListeners.add(networkListener);
        this.notifyState(networkListener);
    }

    public void removeListener(NetworkStateReceiverListener networkListener){
        this.mNetworkListeners.remove(networkListener);
    }

    private void notifyState(NetworkStateReceiverListener networkListener) {
        if(this.mConnected == null || networkListener == null) {
            return;
        }

        if(this.mConnected == true) {
            networkListener.networkAvailable();
        }
//        else
//            listener.networkUnavailable();
    }

    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : this.mNetworkListeners)
            notifyState(listener);
    }
}
