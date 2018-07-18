package sample.sample_wifidirect;

import android.net.wifi.p2p.WifiP2pInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Socket_Send extends Thread {

    private Socket mSocket;
    private WifiP2pInfo mInfo;

    public Socket_Send(WifiP2pInfo info)
    {
        mInfo = info;
    }

    public void run()
    {
        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(mInfo.groupOwnerAddress, 5000));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

            // データを送信
            writer.write(mInfo.groupOwnerAddress.getHostName());

            writer.close();
            mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(String message)
    {
//        try {
//            mSocket = new Socket();
//            mSocket.connect(new InetSocketAddress(mInfo.groupOwnerAddress, 5000));
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
//
//            // データを送信
//            writer.write(message);
//
//            writer.close();
//            mSocket.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
