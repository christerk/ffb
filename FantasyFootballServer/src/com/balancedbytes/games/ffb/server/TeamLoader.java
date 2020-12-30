package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.balancedbytes.games.ffb.model.Game;
import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class TeamLoader {
	private File rootDir;

	public TeamLoader(File rootDir) {
		this.rootDir = rootDir;
	}

	public Team getTeamById(String pTeamId, Game game) {
		return loadTeams(game, team -> team.getId().equals(pTeamId)).get(0);
	}

	public Team[] getTeamsForCoach(String pCoach, Game game) {
		List<Team> teamList = loadTeams(game, team -> team.getCoach().equals(pCoach));
		teamList.sort(Team.comparatorByName());
		return teamList.toArray(new Team[0]);
	}

	public List<Team> loadTeams(Game game, Predicate<Team> predicate) {
		FileIterator fileIterator = new FileIterator(rootDir, false,
				pathname -> pathname.getName().endsWith(".xml"));
		List<Team> teams = new ArrayList<>();
		try {
			while (fileIterator.hasNext()) {
				File file = fileIterator.next();
				try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
					InputSource xmlSource = new InputSource(xmlIn);
					Team team = new Team();
					XmlHandler.parse(game, xmlSource, team);
					for (Player<?> player : team.getPlayers()) {
						player.setTeam(team);
					}
					if (predicate.test(team)) {
						teams.add(team);
					}
				} catch (FantasyFootballException pFfe) {
					throw new FantasyFootballException("Error initializing team " + file.getAbsolutePath(), pFfe);
				}
			}
		} catch (IOException ex) {
			throw new FantasyFootballException("Error loading teams from '" + rootDir.getAbsolutePath() + "'");
		}
		return teams;
	}

}
