package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerProneLive extends TalkHandlerProne {

	public TalkHandlerProneLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
