package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerReRollLive extends TalkHandlerReRoll {

	public TalkHandlerReRollLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
