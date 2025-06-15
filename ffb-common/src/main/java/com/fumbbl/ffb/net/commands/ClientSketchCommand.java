package com.fumbbl.ffb.net.commands;

public abstract class ClientSketchCommand extends ClientCommand {

	public boolean requiresControl() {
		return false;
	}
}
