package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnModeLive extends TalkHandlerTurnMode {

	public TalkHandlerTurnModeLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
