package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerStandupTest extends TalkHandlerStandup {

	public TalkHandlerStandupTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
