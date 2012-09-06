package com.blackjack;

import java.io.*;
import java.net.*;
import android.os.Handler;
import android.util.Log;

public class ConnThread extends Thread{
	private static final int PORT = 2200;
	private static final String TAG = "ConnThread.java-->";
	private Socket socket;
	private OutputStream out;
	private PrintWriter output;
	private BufferedReader input;
	private Handler handler;
	
	public ConnThread(Handler h){
		handler = h;
	}

	public void run(){
		try{
			 socket = new Socket("128.62.99.204", PORT);
			 InetAddress addr2 = InetAddress.getLocalHost();
			 
			 Log.d(TAG, "ip address = " + addr2.getHostAddress() + " 128.62.99.204");
			 
			 String hostName = InetAddress.getLocalHost().getHostName();
			 InetAddress addrs[] = InetAddress.getAllByName(hostName);
			 String myIp = "UNKNOWN";
			 for (InetAddress addr: addrs) {
				 if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
					 myIp = addr.getHostAddress();
				 }
			 }
			 Log.d(TAG, "ip address = " + myIp + " 128.62.99.204");
			 out = socket.getOutputStream();
	      	 output = new PrintWriter(out);
	      	 Log.i(TAG, "connThread is starting");
	      	 input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	      	 while(true){
	      		 
	      		 Log.i(TAG, "connThread is waiting for a readline");
	      		 String str = input.readLine();
	      		 Log.i(TAG, "connThread read the line");
	      		 Log.i(TAG, str);
	      		 handler.obtainMessage(BlackJack.READ_MSG, -1, -1, str).sendToTarget();
	      	 }
		}catch(IOException ioe){
			Log.e(TAG, ioe.toString());
			//ioe.printStackTrace();
		}
	}
	
	public void write(String str){
		try{
			Log.i(TAG, str);
			//Sends to the android screen
			handler.obtainMessage(BlackJack.WRITE_MSG, -1, -1, str).sendToTarget();
			
			output.println(str);
			output.flush();	
		}catch(Exception e){
			Log.e(TAG, e.toString());
			//e.printStackTrace();
		}
	}
	
	public void cancel(){
		try{
			socket.close();
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
}
