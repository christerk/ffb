package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerStandupLive extends TalkHandlerStandup {

	public TalkHandlerStandupLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
