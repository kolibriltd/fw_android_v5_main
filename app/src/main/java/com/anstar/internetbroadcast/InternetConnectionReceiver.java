package com.anstar.internetbroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.anstar.common.Const;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;

public class InternetConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// boolean notConnected = intent.getBooleanExtra(
		// ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		// String reason =
		// intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		// boolean isFailover = intent.getBooleanExtra(
		// ConnectivityManager.EXTRA_IS_FAILOVER, false);
		//
		// NetworkInfo currentNetworkInfo = (NetworkInfo) intent
		// .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		// NetworkInfo otherNetworkInfo = (NetworkInfo) intent
		// .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
		// Utils.LogInfo("CONNECTIVITY CHANGED - " +
		// String.valueOf(notConnected));
		//
		// if (!notConnected) {
		// // Connection eshtablished
		// if (Const.dosync) {
		// SyncHelper.Instance().startSyncing();
		// Const.dosync = false;
		// }
		// } else {
		// Const.dosync = true;
		// Utils.LogInfo("InternetConnectionReceiver : onReceive() -->: mNetworkInfo="
		// + currentNetworkInfo
		// + " mOtherNetworkInfo = "
		// + (otherNetworkInfo == null ? "[none]" : otherNetworkInfo
		// + " noConn=" + notConnected));
		// }
		if (NetworkConnectivity.isConnected()) {
			if (Const.dosync) {
				SyncHelper.Instance().startSyncing();
				Const.dosync = false;
			}
		} else {
			Const.dosync = true;
		}

	}

}
