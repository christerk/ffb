package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.TeamSetup;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class TeamSetupCache {
  
  private class TeamSetupKey {
    
    private String fTeamId;
    private String fName;
    
    public TeamSetupKey(String pTeamId, String pName) {
      fTeamId = pTeamId;
      fName = pName;
    }
    
    public TeamSetupKey(TeamSetup pSetup) {
      this(pSetup.getTeamId(), pSetup.getName());
    }

    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fName == null) ? 0 : fName.hashCode());
      result = prime * result + ((fTeamId == null) ? 0 : fTeamId.hashCode());
      return result;
    }

    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final TeamSetupKey other = (TeamSetupKey) obj;
      if (fName == null) {
        if (other.fName != null) {
          return false;
        }
      } else if (!fName.equals(other.fName)) {
        return false;
      }
      if (fTeamId == null) { 
        if (other.fTeamId != null) {
          return false;
        }
      } else if (!fTeamId.equals(other.fTeamId)) {
        return false;
      }
      return true;
    }
    
  }

  private Map<TeamSetupKey, TeamSetup> fSetupBySetupKey;
  
  public TeamSetupCache() {
    fSetupBySetupKey = new HashMap<TeamSetupKey, TeamSetup>();
  }
  
  public void add(TeamSetup pSetup) {
    fSetupBySetupKey.put(new TeamSetupKey(pSetup), pSetup);
  }
    
  public TeamSetup getTeamSetup(String pTeamId, String pName) {
    return fSetupBySetupKey.get(new TeamSetupKey(pTeamId, pName));
  }

  public TeamSetup[] getTeamSetups() {
    return fSetupBySetupKey.values().toArray(new TeamSetup[fSetupBySetupKey.size()]);
  }
  
  public void init(File pSetupDirectory) throws IOException {
    FileIterator fileIterator = new FileIterator(
      pSetupDirectory,
      false,
      new FileFilter() {
        public boolean accept(File pathname) { return pathname.getName().endsWith(".xml"); };
      }
    );
    while (fileIterator.hasNext()) {
      File file = fileIterator.next();
      BufferedReader xmlIn = new BufferedReader(new FileReader(file));
      InputSource xmlSource = new InputSource(xmlIn);
      TeamSetup setup = new TeamSetup();
      try {
        XmlHandler.parse(xmlSource, setup);
      } catch (FantasyFootballException pFfe) {
        throw new FantasyFootballException("Error on initializing team setup " + file.getAbsolutePath(), pFfe);
      }
      xmlIn.close();
      add(setup);
    }
  }
  
}
