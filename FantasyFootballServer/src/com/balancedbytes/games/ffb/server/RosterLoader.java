package com.balancedbytes.games.ffb.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.balancedbytes.games.ffb.model.Game;
import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.util.FileIterator;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class RosterLoader {

	private File rootDir;

	public RosterLoader(File rootDir) {
		this.rootDir = rootDir;
	}

	public Roster getRosterById(String pRosterId, Game game) throws IOException {
		FileIterator fileIterator = new FileIterator(rootDir, false,
				pathname -> pathname.getName().endsWith(".xml"));
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			try (BufferedReader xmlIn = new BufferedReader(new FileReader(file))) {
				InputSource xmlSource = new InputSource(xmlIn);
				Roster roster = new Roster();
				XmlHandler.parse(game, xmlSource, roster);
				if (roster.getId().equals(pRosterId)) {
					return roster;
				}
			} catch (FantasyFootballException pFfe) {
				throw new FantasyFootballException("Error initializing roster " + file.getAbsolutePath(), pFfe);
			}
		}

		throw new FantasyFootballException("Could not find roster for id '" + pRosterId + "'");
	}

}
