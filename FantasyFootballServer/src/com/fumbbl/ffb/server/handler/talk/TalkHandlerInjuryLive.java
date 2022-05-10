package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerInjuryLive extends TalkHandlerInjury {

	public TalkHandlerInjuryLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
