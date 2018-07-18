// 参考URL
// https://developer.android.com/guide/topics/connectivity/wifip2p?hl=ja
// https://translate.google.co.jp/translate?sl=en&tl=ja&js=y&prev=_t&hl=ja&ie=UTF-8&u=https%3A%2F%2Fdeveloper.android.com%2Fguide%2Ftopics%2Fconnectivity%2Fwifip2p%3Fhl%3Dja&edit-text=&act=url
// https://techbooster.org/android/device/9960/
// https://techbooster.org/android/device/9981/

package sample.sample_wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, WiFiDirectBroadcastReceiver.SelfDataListener {

    IntentFilter mIntentFilter;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    WifiP2pConfig mConfig;
    private WifiP2pInfo mInfo;
    WifiP2pDevice mMyDevice;

    // 接続デバイスのリスト
    private List<WifiP2pDevice> mDeviceList = new ArrayList<WifiP2pDevice>();

    ListView mLstDevice;
    ArrayAdapter mArrayAdapter;
    Button mBtnDeviceSearch;
    TextView mTxtDeviceInfo;
    TextView mTxtSend;
    Button mBtnSend;
    Button mBtnDisConnect;
    TextView mTxtSelfData;
    TextView mTxtConnectDeviceData;

    Socket_Send mSocket_send;
    Socket_Receive mSocket_receive;

    boolean mIsWifiEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ブロードキャストを受信するためにインテントを登録
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        // アプリケーションをWifiフレームワークに登録
        mChannel = mManager.initialize(this, getMainLooper(), null);

        // クラスを作成
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mTxtSelfData = findViewById(R.id.textView3);

        mBtnDeviceSearch = findViewById(R.id.button);
        mBtnDeviceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "デバイスの検出プロセスが成功しました。",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "デバイスの検出プロセスが失敗しました。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        mLstDevice = (ListView) findViewById(R.id.ListView);
        mLstDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mConfig = new WifiP2pConfig();
                mConfig.deviceAddress = mDeviceList.get(position).deviceAddress;
                mManager.connect(mChannel, mConfig, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "デバイスの接続プロセスが成功しました。",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "デバイスの接続プロセスが失敗しました。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        mTxtDeviceInfo = findViewById(R.id.textView);

        mTxtSend = findViewById(R.id.textView2);

        mBtnSend = findViewById(R.id.button2);

        mBtnDisConnect = findViewById(R.id.button3);
        mBtnDisConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "デバイスの切断プロセスが成功しました。",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "デバイスの切断プロセスが失敗しました。",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        mTxtConnectDeviceData = findViewById(R.id.textView4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    // Wifiの有効/無効を判断
    public void SetWifiEnable(boolean a_isEnable)
    {
        String text;
        if (a_isEnable)
        {
            text = "WifiDirectが有効です。";
        }
        else
        {
            text = "WifiDirectが無効です。";
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        mIsWifiEnable = a_isEnable;
        SetMyDataAll();
    }

    // 新しいデバイスを検索したときに、状態に変更が発生したときに呼ばれる処理
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        mDeviceList.clear();
        mDeviceList.addAll(peerList.getDeviceList());
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        // リストに表示する
        if (mDeviceList.size() > 0) {
            for (byte ii = 0; ii < mDeviceList.size(); ii++) {
                // Deviceの情報を格納
                mArrayAdapter.add(mDeviceList.get(ii).deviceName + ", " + mDeviceList.get(ii).deviceAddress + ", " + ((mDeviceList.get(ii).isGroupOwner() == true) ? "Yes" : "No"));
            }
        }
        mLstDevice.setAdapter(mArrayAdapter);
    }

    // 接続しているデバイスとの通信状態が変化したときに呼ばれる処理
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        mInfo = info;

        if (mInfo.isGroupOwner)
        {
            mTxtDeviceInfo.setText("Group Owner IP" + mInfo.groupOwnerAddress.getHostAddress());
            /* // サーバースレッド起動
            mSocket_receive = new Socket_Receive(this);
            mSocket_receive.start();
            mBtnSend.setVisibility(View.VISIBLE);
            */
            Toast.makeText(this, "オーナーとして接続しました。", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mTxtDeviceInfo.setText("Group Owner IP" + mInfo.groupOwnerAddress.getHostAddress());
            /* // クライアントスレッド起動
            mSocket_send = new Socket_Send(mInfo);
            mSocket_send.start();
            */

            Toast.makeText(this, "子として接続しました。", Toast.LENGTH_SHORT).show();
        }

        SetMyDataAll();
    }

    @Override
    public void SetSelfData(WifiP2pDevice device) {
        mMyDevice = device;
        SetMyDataAll();
    }

    public void SetMyDataAll()
    {
        boolean l_is_Owner = false;

        if (mInfo != null)
        {
            l_is_Owner = mInfo.isGroupOwner;
        }

        if (mMyDevice != null){
            mTxtSelfData.setText("Wifi Direct 有効/無効 = " + ((mIsWifiEnable) ? "有効" : "無効") + "\n" +
                                    "デバイス名 = " + mMyDevice.deviceName + "\n" +
                                    "アドレス = " + mMyDevice.deviceAddress + "\n" +
                                    "デバイスの状態 = " +
                                    ((mMyDevice.status == 0) ? "CONNECTED" :
                                     (mMyDevice.status == 1) ? "INVITED" :
                                     (mMyDevice.status == 2) ? "FAILED" :
                                     (mMyDevice.status == 3) ? "AVAILABLE" : "UNAVAILABLE") + "\n" +
                                    "オーナー = " + ((l_is_Owner == true) ? "Yes" : "No"));
        }
    }
}
