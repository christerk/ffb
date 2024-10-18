package com.fumbbl.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fumbbl.ffb.model.Team;
import org.xml.sax.InputSource;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterSkeleton;
import com.fumbbl.ffb.util.FileIterator;
import com.fumbbl.ffb.xml.XmlHandler;

/**
 * Roster Cache used when the server is in STANDALONE mode.
 *
 * For the cache to work, two files have to exist:
 *
 * 1. A team xml in `/ffb-server/teams`.
 * 2. A roster xml in `/ffb-server/roster`
 *
 * The format of these has evolved over time and is not fixed. E.g., in older formats, the `<rosterId>` in the
 * team XML is used to find the matching roster, while in newer formats, the team id is used directly.
 *
 * The easiest way to add new teams to the cache is by copying the output from the FUMBBL live server. This is done
 * by copying the result of these two API calls (replace team id with the wanted team):
 *
 * 1. Team information: https://fumbbl.com/xml:team?id=284314
 * 2. Roster information: https://fumbbl.com/xml:roster?team=284314
 *
 * @author Kalimar
 */
public class RosterCache {

	private Map<String, File> rosterFileByRosterId;
	private Map<String, File> rosterFileByTeamId;

	public RosterCache() {
		rosterFileByRosterId = new HashMap<>();
		rosterFileByTeamId = new HashMap<>();
	}

	public Roster getRosterForTeam(Team team, Game game) {
		// In newer versions of the XML format, the `<rosterId>` is not used (but is still present).
		// So we first check for the presence of a roster matching the team id, and only if no roster
		// is found, do we fall back to looking up the roster using the original rosterId.
		File rosterFile = rosterFileByTeamId.get(team.getId());
		if (rosterFile == null) {
			rosterFile = rosterFileByRosterId.get(team.getRosterId());
		}
		if (rosterFile == null) {
			throw new IllegalStateException("No roster found for neither rosterId (" + team.getRosterId() + ") nor teamId (" + team.getId() + ")");
		}
		try (BufferedReader xmlIn = new BufferedReader(new FileReader(rosterFile))) {
			InputSource xmlSource = new InputSource(xmlIn);
			Roster roster = new Roster();
			XmlHandler.parse(game, xmlSource, roster);

			return roster;
		} catch (FantasyFootballException | IOException pFfe) {
			throw new FantasyFootballException("Error deserializing roster file: " + rosterFile.getAbsolutePath(), pFfe);
		}
	}

	public void init(File pRosterDirectory) throws IOException {
		FileIterator fileIterator = new FileIterator(pRosterDirectory, false,
				pathname -> pathname.getName().endsWith(".xml"));
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
				InputSource xmlSource = new InputSource(xmlIn);
				RosterSkeleton roster = new RosterSkeleton();
				XmlHandler.parse(null, xmlSource, roster);

				if (roster.getTeamId() != null) {
					rosterFileByTeamId.put(roster.getTeamId(), file);
				} else if (roster.getId() != null) {
					rosterFileByRosterId.put(roster.getId(), file);
				} else {
					throw new IllegalStateException("Roster are missing either an 'id' or 'team' attribute.");
				}
			} catch (FantasyFootballException pFfe) {
				throw new FantasyFootballException("Error populating roster cache for file" + file.getAbsolutePath(), pFfe);
			}
		}
	}

}
