package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class TeamCache {

  private Map<String, Team> fTeamById;
  
  public TeamCache() {
    fTeamById = new HashMap<String, Team>();
  }
  
  public void add(Team pTeam) {
    fTeamById.put(pTeam.getId(), pTeam);
  }
    
  public Team getTeamById(String pTeamId) {
    return fTeamById.get(pTeamId);
  }

  public Team[] getTeams() {
    return fTeamById.values().toArray(new Team[fTeamById.size()]);
  }
  
  public Team[] getTeamsForCoach(String pCoach) {
    List<Team> teamList = new ArrayList<Team>();
    for (Team team : fTeamById.values()) {
      if (team.getCoach().equals(pCoach)) {
        teamList.add(team);
      }
    }
    Collections.sort(teamList, Team.comparatorByName());
    return teamList.toArray(new Team[teamList.size()]);
  }

  public void init(File pTeamDirectory) throws IOException {
    FileIterator fileIterator = new FileIterator(
      pTeamDirectory,
      false,
      new FileFilter() {
        public boolean accept(File pathname) { return pathname.getName().endsWith(".xml"); };
      }
    );
    while (fileIterator.hasNext()) {
      File file = fileIterator.next();
      BufferedReader xmlIn = new BufferedReader(new FileReader(file));
      InputSource xmlSource = new InputSource(xmlIn);
      Team team = new Team();
      try {
        XmlHandler.parse(xmlSource, team);
      } catch (FantasyFootballException pFfe) {
        throw new FantasyFootballException("Error initializing team " + file.getAbsolutePath(), pFfe);
      }
      xmlIn.close();
      for (Player player : team.getPlayers()) {
        player.setTeam(team);
      }
      add(team);
    }
  }
  
}
