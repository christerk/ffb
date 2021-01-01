package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TeamSkeleton;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Kalimar
 */
public class TeamCache {

  private Map<TeamSkeleton, File> teamFiles = new HashMap<>();

  public Team getTeamById(String teamId, Game game) {
    return teamFiles.entrySet().stream().filter(entry -> entry.getKey().getId().equals(teamId)).findFirst().map(entry -> {
      try {
        return mapToTeam(entry.getValue(), game);
      } catch (IOException e) {
        throw new FantasyFootballException("Error populating team cache for team " + entry.getKey().getId(), e);
      }
    }).get();
  }

  public TeamSkeleton getSkeleton(String teamId) {
    //noinspection OptionalGetWithoutIsPresent
    return teamFiles.keySet().stream().filter(key -> key.getId().equals(teamId)).findFirst().get();
  }

  public Team[] getTeams(Game game) {
    return mapToTeams(teamFiles.values(), game).toArray(new Team[0]);
  }

  public Team[] getTeamsForCoach(String coach, Game game) {
    List<File> files = teamFiles.entrySet().stream().filter(entry -> entry.getKey().getCoach().equals(coach)).map(Map.Entry::getValue).collect(Collectors.toList());
    List<Team> teamList = mapToTeams(files, game);

    teamList.sort(Team.comparatorByName());
    return teamList.toArray(new Team[0]);
  }

  private List<Team> mapToTeams(Collection<File> files, Game game) {
    return files.stream().map(file -> {
      try {
        return mapToTeam(file, game);
      } catch (IOException e) {
        throw new FantasyFootballException("Error populating team cache for file " + file.getAbsolutePath(), e);
      }
    }).collect(Collectors.toList());
  }

  private Team mapToTeam(File file, Game game) throws IOException {
    try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
      InputSource xmlSource = new InputSource(xmlIn);
      Team team = new Team();
      XmlHandler.parse(game, xmlSource, team);
      return team;
    }
  }

  public void init(File pTeamDirectory) throws IOException {
    FileIterator fileIterator = new FileIterator(pTeamDirectory, false,
      pathname -> pathname.getName().endsWith(".xml"));
    while (fileIterator.hasNext()) {
      File file = fileIterator.next();
      try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
        InputSource xmlSource = new InputSource(xmlIn);
        TeamSkeleton team = new TeamSkeleton();
        XmlHandler.parse(null, xmlSource, team);
        teamFiles.put(team, file);
      } catch (FantasyFootballException pFfe) {
        throw new FantasyFootballException("Error populating team cache for file " + file.getAbsolutePath(), pFfe);
      }
    }
  }

}
