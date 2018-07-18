package sample.sample_wifidirect;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Socket_Receive extends Thread{

    ServerSocket serverSocket = null;
    BufferedReader reader = null;
    Handler mHandler = new Handler();
    String mIpAddress;
    static Socket mSocketSend;

    Context mContext;

    Socket_Receive(Context context)
    {
        mContext = context;
    }

    public void run() {
        String string = "NG";
        try {
            serverSocket = new ServerSocket(5000);

            //while (true) {
                // ここでデータを受信するまで待機
                Socket socket = serverSocket.accept();

                // 受信したデータを格納
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mIpAddress = reader.readLine();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, "")
                    }
                });

                mSocketSend = socket;
                //socket.close();
                reader.close();
            //}

            //serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
