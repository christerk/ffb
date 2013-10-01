package com.balancedbytes.games.ffb;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public final class PasswordChallenge {
  
  //  1. Get the challenge string for the coach:
  //     http://fumbbl.com/xml:auth?op=challenge&coach=kalimar
  //     Convert the string into binary and store in the variable CHL.
  //     It's simply a hex encoding.
  //
  //  2. Calculate PWD = MD5(password). This is the MD5 of the clear-text
  //     password provided by the coach. Store it in binary form.
  //
  //  3. Calculate OPAD = PWD XOR 0x5c5c...5c.
  //
  //  4. Calculate IPAD = PWD XOR 0x3636...36.
  //
  //  5. Calculate R = MD5( OPAD + MD5 ( IPAD + CHL ) )
  //
  //  6. Hex encode R
  //
  //  7. Send the hex encoded result to
  //     http://fumbbl.com/xml:auth?op=response&coach=kalimar&response=4b26ae85bb...12ff
  //
  //  8. Check the response. Will return "OK" or "FAIL" as appropriate.
  
  public static byte[] fromHexString(String pHexString) {
    int len = pHexString.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(pHexString.charAt(i), 16) << 4)
                             + Character.digit(pHexString.charAt(i+1), 16));
    }
    return data;
  }
  
  public static String toHexString(byte[] pBytes) {
    StringBuilder hexString = new StringBuilder();
    for (int i = 0; i < pBytes.length; i++) {
      hexString.append(Integer.toString((pBytes[i] & 0xff) + 0x100, 16).substring(1));
    }
    return hexString.toString();
  }
  
  public static byte[] concat(byte[] pBytes1, byte[] pBytes2) {
    int size1 = (pBytes1 != null) ? pBytes1.length : 0;
    int size2 = (pBytes2 != null) ? pBytes2.length : 0;
    byte[] result = new byte[size1 + size2];
    for (int i = 0; i < size1; i++) {
      result[i] = pBytes1[i];
    }
    for (int i = 0; i < size2; i++) {
      result[i + size1] = pBytes2[i];
    }
    return result;
  }
  
  public static byte[] xor(byte[] pBytes, byte pMask) {
    byte[] result = null;
    if (ArrayTool.isProvided(pBytes)) {
      result = new byte[pBytes.length];
      for (int i = 0; i < result.length; i++) {
        result[i] = (byte) (pBytes[i] ^ pMask);
      }
    } else {
      result = new byte[0];
    }
    return result;
  }
  
  public static byte[] md5Encode(byte[] pBytes) throws NoSuchAlgorithmException {
    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
    return md5Digest.digest(pBytes);
  }
      
  public static String createResponse(String pChallenge, byte[] pMd5EncodedPassword) throws IOException, NoSuchAlgorithmException {
    if (StringTool.isProvided(pChallenge)) {
      byte[] challenge = fromHexString(pChallenge);
      byte[] opad = xor(pMd5EncodedPassword, (byte) 0x5c);
      byte[] ipad = xor(pMd5EncodedPassword, (byte) 0x36);
      return toHexString(md5Encode(concat(opad, md5Encode(concat(ipad, challenge)))));
    } else {
      return toHexString(pMd5EncodedPassword);
    }
  }
  
  public static void main(String[] args) {
    try {
      System.out.println(toHexString(md5Encode(args[0].getBytes())));
    } catch (Exception pAll) {
      pAll.printStackTrace();
    }
  }

}