package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerUsedActionsLive extends TalkHandlerUsedActions {

	public TalkHandlerUsedActionsLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
