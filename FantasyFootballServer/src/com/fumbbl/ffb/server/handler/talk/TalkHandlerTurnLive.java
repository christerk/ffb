package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnLive extends TalkHandlerTurn {

	public TalkHandlerTurnLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
