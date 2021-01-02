package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.RosterSkeleton;
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

	private Map<String, File> rosterFileById;

	public RosterCache() {
		rosterFileById = new HashMap<>();
	}

	public Roster getRosterById(String rosterId, Game game) {
		try (BufferedReader xmlIn = new BufferedReader(new FileReader(rosterFileById.get(rosterId)))) {
			InputSource xmlSource = new InputSource(xmlIn);
			Roster roster = new Roster();
			XmlHandler.parse(game, xmlSource, roster);

			return roster;
		} catch (FantasyFootballException | IOException pFfe) {
			throw new FantasyFootballException("Error deserializing roster for id " + rosterId, pFfe);
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

				rosterFileById.put(roster.getId(), file);
			} catch (FantasyFootballException pFfe) {
				throw new FantasyFootballException("Error populating roster cache for file" + file.getAbsolutePath(), pFfe);
			}
		}
	}

}
