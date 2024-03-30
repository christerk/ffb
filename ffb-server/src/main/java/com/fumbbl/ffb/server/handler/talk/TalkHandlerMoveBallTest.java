package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerMoveBallTest extends TalkHandlerMoveBall {

	public TalkHandlerMoveBallTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
