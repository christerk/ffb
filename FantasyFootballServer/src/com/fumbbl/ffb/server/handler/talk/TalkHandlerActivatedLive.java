package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerActivatedLive extends TalkHandlerActivated {

	public TalkHandlerActivatedLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
