package com.balancedbytes.games.ffb.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class UtilFile {
  
  public static void sortPropertyFile(File pFile) throws IOException {
    
    List<String> header = new ArrayList<String>();
    List<String> body = new ArrayList<String>();
    
    try (BufferedReader in = new BufferedReader(new FileReader(pFile))) {
      boolean insideHeader = true;
      String lineIn;
      while ((lineIn = in.readLine()) != null) {
        if (insideHeader) {
          if (lineIn.startsWith("#")) {
            header.add(lineIn);
            continue;
          } else {
            insideHeader = false;
          }
        }
        body.add(lineIn);
      }
    }
    
    try (BufferedWriter out = new BufferedWriter(new FileWriter(pFile))) {
      for (String lineOut : header) {
        out.write(lineOut);
        out.newLine();
      }
      Collections.sort(body);
      for (String lineOut : body) {
        out.write(lineOut);
        out.newLine();
      }
    }
    
  }

}
