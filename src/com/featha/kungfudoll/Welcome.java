package com.featha.kungfudoll;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import com.phonegap.*;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;

public class Welcome extends DroidGap {
	
	private ServerSocket serverSocket;
    private Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Thread st = new Thread(new ServerThread());
		st.start();
        
        super.loadUrl("file:///android_asset/www/index.html");
        
        WebSettings settings = appView.getSettings(); 
      	settings.setPluginState(PluginState.ON);
      	
        Log.d("welcome", "appView: " + appView);
        Log.d("socket", "local address" + getLocalIpAddress());
    }
    
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

    
	public class ServerThread implements Runnable {
		private String line = null;

		public void run() {
			try {
				serverSocket = new ServerSocket(8080);
				while (true) {
					Socket client = serverSocket.accept();

					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        while ((line = in.readLine()) != null) {
        					handler.post(new Runnable() {
        						public void run() {
        							String str[] = line.split(" ");
        							Log.d("socket", str[0] + " - " + str[1]);
        							appView.loadUrl("javascript:$('#score').text('" + str[0] + "');");
        							appView.loadUrl("javascript:$('#speed').text('" + str[1] + "');");
        						}
        					});                        	
                        }
					} catch (Exception e) {
						Log.e("socket", e.getMessage());
					}
					client.close();
				}
			} catch (Exception e) {
				Log.e("socket", "exception: " + e.getMessage());
			}
		}

	}

}
