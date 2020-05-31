package com.fumbbl.rng;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The entropy server listens on a set network port for entropy data. The idea is that a single client can send entropy
 * information which is buffered by the server. A server-side application can then pull the entropy data from the EntropyServer
 * and use it for whatever purpose it sees fit.
 * 
 * @author christer
 *
 */
public class EntropyServer implements Runnable, EntropySource {
	private boolean runControl;
	private int port;
	private byte[] buffer;
	private int bStart;
	private int bEnd;
	private int bLength;
	private boolean connected;

	private Object lockEmpty;
	private Object lockFull;

	/**
	 * Constructs the Entropy server.
	 * 
	 * @param port Port number to listen for client.
	 * @param bufferSize Buffer size for receiving entropy.
	 */
	public EntropyServer(int port, int bufferSize) {
		this.port = port;
		this.buffer = new byte[bufferSize];
		this.bStart = 0;
		this.bEnd = 0;
		this.bLength = bufferSize;
		lockEmpty = new Object();
		lockFull = new Object();
	}

	/**
	 * Starts the entropy server. Does not block.
	 */
	public void startServer() {
		Thread t = new Thread(this);
		this.runControl = true;
		t.start();
	}

	/**
	 * Gracefully stops the entropy server.
	 */
	public void stopServer() {
		runControl = false;

		// Wake up threads waiting for data.
		synchronized (lockEmpty) {
			lockEmpty.notifyAll();
		}
	}

	/**
	 * Check status of the entropy client.
	 * 
	 * @return true if the entropy client is connected
	 */
	public boolean isConnected() {
		return connected;
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
		}
	}

	private synchronized void put(byte[] buf, int length) {
		while (isFull()) {
			try {
				synchronized(lockFull) {
					lockFull.wait();
				}
			} catch (InterruptedException ie) {
				return;
			}
		}

		int availableSpace = availableSpace();
		
		if (length > availableSpace)
			length = availableSpace;
		
		for (int i=0; i<length; i++) {
			buffer[bEnd] = buf[i];
			bEnd = (bEnd + 1) % bLength;
		}
		
		synchronized (lockEmpty) {
			lockEmpty.notifyAll();
		}
	}

	private synchronized byte get() {
		while (bStart == bEnd) {
			try {
				synchronized (lockEmpty) {
					lockEmpty.wait();
				}
			} catch (InterruptedException ie) {
				return 0;
			}
		}

		byte b = buffer[bStart];
		bStart = (bStart + 1) % bLength;

		synchronized(lockFull) {
			lockFull.notify();
		}
		
		return b;
	}

	private synchronized boolean isFull() {
		return ((bEnd + 1) % bLength) == bStart;
	}

//	private synchronized boolean isEmpty() {
//		return bStart == bEnd;
//	}

	private synchronized int availableSpace() {
		if (bEnd == bStart)
			return bLength;
		
		if (bEnd > bStart)
			return bLength - (bEnd-bStart) - 1;

		
		return bStart - bEnd;
	}

	public void run() {

		byte[] entropyBuffer = new byte[this.buffer.length];

		while (runControl) {
			try (ServerSocket serverSocket = new ServerSocket(this.port)) {

				serverSocket.setSoTimeout(1000);
				while (runControl) {
					try (Socket socket = serverSocket.accept();) {

						socket.setSoTimeout(1000);
						System.out.println("Entropy client connected");

						try (InputStream socketInputStream = socket.getInputStream()) {
							while (runControl && socket.isConnected()) {
								connected = true;

								try {
									int bytes = socketInputStream.read(entropyBuffer);
									put(entropyBuffer, bytes);
								} catch (SocketTimeoutException ste) {
									// Socket read timed out, do nothing but wait for next batch of data.
								}
							}
						}
					} catch (SocketTimeoutException ste) {
						// serverSocket.accept() timed out. This is to be able
						// to stop the server properly.
						// We don't do anything here except go back to waiting
						// for a connection.
					}
				}
			} catch (IOException ioe) {
				System.out.println(ioe.toString());
				System.out.println("Entropy client disconnected");
				connected = false;
				// Some weirdness on the network. Wait a second and set up the
				// server again.
				sleep(1000);
			}
		}
		connected = false;
	}

	public synchronized boolean hasEnoughEntropy() {
		return bEnd != bStart;
	}

	public byte getEntropy() {
		return get();
	}
}
