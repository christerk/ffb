package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerStunLive extends TalkHandlerStun {

	public TalkHandlerStunLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
