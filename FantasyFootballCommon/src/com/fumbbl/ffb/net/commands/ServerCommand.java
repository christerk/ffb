package com.fumbbl.ffb.net.commands;

import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.net.NetCommand;

/**
 * 
 * @author Kalimar
 */
public abstract class ServerCommand extends NetCommand {

	private int fCommandNr;

	public int getCommandNr() {
		return fCommandNr;
	}

	public void setCommandNr(int pCommandNr) {
		fCommandNr = pCommandNr;
	}

	public boolean isReplayable() {
		return true;
	}

	public FactoryContext getContext() {
		return FactoryContext.GAME;
	}
}
