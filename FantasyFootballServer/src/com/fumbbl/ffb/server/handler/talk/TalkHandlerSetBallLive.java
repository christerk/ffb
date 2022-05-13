package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSetBallLive extends TalkHandlerSetBall {

	public TalkHandlerSetBallLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
