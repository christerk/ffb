package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerActivatedTest extends TalkHandlerActivated {

	public TalkHandlerActivatedTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
