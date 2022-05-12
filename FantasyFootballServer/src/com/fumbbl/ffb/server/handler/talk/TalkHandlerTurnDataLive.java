package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnDataLive extends TalkHandlerTurnData {

	public TalkHandlerTurnDataLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
