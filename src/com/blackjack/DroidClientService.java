package com.blackjack;

import android.os.Handler;

public class DroidClientService{
	private final Handler handler;
	private ConnThread connThread;
	private BlackJack context;
	
	public DroidClientService(BlackJack c, Handler h){
		context = c;
		handler = h;
	}
	
	public synchronized void start(){
		connThread = new ConnThread(handler);
		connThread.start();
	}
	
	public synchronized void stop(){
		if(connThread != null){
			connThread.cancel();
			connThread = null;
		}
	}
	
	public void write(String out){
		connThread.write(out);
	}
}
