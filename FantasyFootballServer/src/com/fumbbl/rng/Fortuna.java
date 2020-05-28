package com.fumbbl.rng;

import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * @author Christer Kaivo-oja
 */
public class Fortuna {
  
	private static int NUMBER_OF_POOLS = 32;
	private static long MAX_REKEY_DELAY_MS = 1000;
	
	private EntropyPool[] pools;
	private int currentPool;
	private int poolSelector;
	private SecretKeySpec sKeySpec;
	private Cipher cipher;
	private byte[] nonce;
	private byte[] randomData;
	private int byteOffset;
	private long lastRekeying;
	
	private long numberOfRekeyings;
	private long numberOfBytes;
	
	public Fortuna() {
		numberOfRekeyings=0;
		numberOfBytes=0;
		pools = new EntropyPool[NUMBER_OF_POOLS];
		for (int i=0; i<NUMBER_OF_POOLS; i++) {
			pools[i] = new EntropyPool();
		}
		currentPool = NUMBER_OF_POOLS - 1;
		poolSelector = 1;
		try {
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		nonce = new byte[] {
				(byte)0x4E,(byte)0xC1,(byte)0x37,(byte)0xA4,
				(byte)0x26,(byte)0xDA,(byte)0xBF,(byte)0x8A,
				(byte)0xA0,(byte)0xBE,(byte)0xB8,(byte)0xBC,
				(byte)0x0C,(byte)0x2B,(byte)0x89,(byte)0xD6
		};
		byte[] key = new byte[] {
				(byte)0x95,(byte)0xA8,(byte)0xEE,(byte)0x8E,(byte)0x89,(byte)0x97,(byte)0x9B,(byte)0x9E,
				(byte)0xFD,(byte)0xCB,(byte)0xC6,(byte)0xEB,(byte)0x97,(byte)0x97,(byte)0x52,(byte)0x8D,
				(byte)0x43,(byte)0x2D,(byte)0xC2,(byte)0x60,(byte)0x61,(byte)0x55,(byte)0x38,(byte)0x18,
				(byte)0xEA,(byte)0x63,(byte)0x5E,(byte)0xC5,(byte)0xD5,(byte)0xA7,(byte)0x72,(byte)0x7E
		};

		for (int i=0; i<key.length; i++) {
			long l = System.currentTimeMillis();
			byte b = (byte) (l & 0xff);
			key[i] ^= b ^ ((byte) (Thread.currentThread().getId() & 0xff));
			try {
				// Do stuff that takes sort of random amounts time
				Thread.sleep(10);
				InetAddress.getLocalHost().isReachable(100);
			} catch (Exception e) { }
		}
		
		rekeyGenerator(key);
		generateRandomData();
		rekeyGenerator(randomData);
		generateRandomData();
	}
	
	public void displayStats() {
		System.out.println("Rekeyings:         "+numberOfRekeyings);
		System.out.println("Bytes fetched:     "+numberOfBytes);
		System.out.println("Bits per rekeying: "+(8*numberOfBytes) / numberOfRekeyings);
	}
	
	public long getRekeyings() {
	  return numberOfRekeyings;
	}
	
	public long getNumberOfBytes() {
	  return numberOfBytes;
	}
	
	public int getByte() {
		numberOfBytes++;
		int result = randomData[byteOffset];
		byteOffset++;
		if (byteOffset >= 16)
			generateRandomData();
		return result & 0xff;
	}
	
	public int getDieRoll(int sides) {
		int result;
		assert sides < 256 && sides > 0;
		do {
			result = getByte();
		} while (result >= 256 - (256 % sides) );
		return 1 + (result % sides);
	}
	
	public synchronized void rekeyGenerator(byte[] newKey) {
		lastRekeying = System.currentTimeMillis();
		numberOfRekeyings++;
		sKeySpec = new SecretKeySpec(newKey, "AES");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void generateRandomData() {
		try {
			randomData = cipher.doFinal(nonce);
			for (int i=nonce.length-1; i>=0; i--) {
				nonce[i]++;
				if (nonce[i] != 0)
					break;
			}
			byteOffset = 0;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void addEntropy(byte data) {
		pools[currentPool].addEntropy(data);
		currentPool--;
		if (currentPool < 0) {
			currentPool += NUMBER_OF_POOLS;
			if (pools[0].hasEnoughEntropy() && System.currentTimeMillis() - lastRekeying > MAX_REKEY_DELAY_MS) {
				byte[] newKey = pools[0].getEntropy();
				for (int i=1; i<NUMBER_OF_POOLS; i++) {
					if ((poolSelector & (1<<i)) != 0) {
						byte[] entropy = pools[i].getEntropy();
						for (int j=0; j<entropy.length; j++)
							newKey[j] ^= entropy[j];
					}
				}
				rekeyGenerator(newKey);
				poolSelector++;
			}
		}
	}
}
