package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerStunTest extends TalkHandlerStun {

	public TalkHandlerStunTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
