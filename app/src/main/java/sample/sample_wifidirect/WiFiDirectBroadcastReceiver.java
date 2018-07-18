package sample.sample_wifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    private SelfDataListener mSelfDataListener;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity)
    {
        mManager = manager;
        mChannel = channel;
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // WifiDirectの有効/無効の状態の変化を検知した場合
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                mActivity.SetWifiEnable(true);
            }
            else
            {
                mActivity.SetWifiEnable(false);
            }
        }

        // WifiDirect通信ができるデバイスの変更があったときに呼ばれる
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null)
            {
                mManager.requestPeers(mChannel, (WifiP2pManager.PeerListListener) mActivity);
            }
        }

        // 接続状態の変更を検知した場合
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null){
                return;
            }

            // ネットワークの状態を確認する
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel, (WifiP2pManager.ConnectionInfoListener) mActivity);
            }
        }

        // 自身のWifi状態が変化した場合
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // OS5.0では、Wifiを無効にしたときはここに入らない。原因は不明。
            mSelfDataListener = mActivity;
            mSelfDataListener.SetSelfData((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }

    public interface SelfDataListener
    {
        void SetSelfData(WifiP2pDevice device);
    }
}
