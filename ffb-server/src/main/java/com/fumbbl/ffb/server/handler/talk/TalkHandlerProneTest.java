package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerProneTest extends TalkHandlerProne {

	public TalkHandlerProneTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
