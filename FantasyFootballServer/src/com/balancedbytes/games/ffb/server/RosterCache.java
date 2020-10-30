package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class RosterCache {

  private Map<String, Roster> fRosterById;
  
  public RosterCache() {
    fRosterById = new HashMap<>();
  }
  
  public void add(Roster pRoster) {
    if (pRoster != null) {
      fRosterById.put(pRoster.getId(), pRoster);
    }
  }
    
  public Roster getRosterById(String pRosterId) {
    return fRosterById.get(pRosterId);
  }

  public void clear() {
    fRosterById.clear();
  }
  
  public void init(File pRosterDirectory) throws IOException {
    FileIterator fileIterator = new FileIterator(
      pRosterDirectory,
      false,
      pathname -> pathname.getName().endsWith(".xml")
    );
    while (fileIterator.hasNext()) {
      File file = fileIterator.next();
      try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
        InputSource xmlSource = new InputSource(xmlIn);
        Roster roster = new Roster();
          XmlHandler.parse(xmlSource, roster);
          add(roster);
      } catch (FantasyFootballException pFfe) {
      throw new FantasyFootballException("Error initializing roster " + file.getAbsolutePath(), pFfe);
      }
    }
  }
  
}
