package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerMoveBallLive extends TalkHandlerMoveBall {

	public TalkHandlerMoveBallLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
