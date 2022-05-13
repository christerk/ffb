package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSetPlayerLive extends TalkHandlerSetPlayer {

	public TalkHandlerSetPlayerLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
