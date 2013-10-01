package com.fumbbl.utils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.client.net.NioClient;
import com.balancedbytes.games.ffb.net.NetCommand;

/**
 * 
 * @author Christer
 */
public class DelayQueue extends Thread {
	
	private ConcurrentLinkedQueue<Packet> outBuffer;
	private Random r;
	private int minDelay;
	private int maxDelay;
	private NioClient nioClient;
	
	public DelayQueue(NioClient nioClient, int min, int max) {
		outBuffer = new ConcurrentLinkedQueue<Packet>();
		r = new Random();
		this.minDelay = min;
		this.maxDelay = max;
		this.nioClient = nioClient;
	}
	
	public void send(NetCommand command) {
    long cTime = System.currentTimeMillis();
	  outBuffer.add(new Packet(cTime + minDelay + r.nextInt() % (maxDelay-minDelay), command));
	}
	
	public void run() {
			
			while(true) {
				// Run every 10 ms.
				try { Thread.sleep(10); } catch (InterruptedException _) { }
				
				long cTime = System.currentTimeMillis();

				// Push out some packets if the delay timer has expired
				if (outBuffer.size() > 0) {
				  while (outBuffer.peek().timestamp <= cTime) {
				    try {
				      nioClient.sendDirect(outBuffer.remove().data);
				    }
				    catch (IOException ioe) {
				      throw new FantasyFootballException(ioe);
				    }
				  }
				}

			}
	}
	
	private class Packet {
		public Packet(long timestamp, NetCommand data) {
			this.timestamp = timestamp;
			this.data = data;
		}
		
		public long timestamp;
		public NetCommand data;
	}
}
