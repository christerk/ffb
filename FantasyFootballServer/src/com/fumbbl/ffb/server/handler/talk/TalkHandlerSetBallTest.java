package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSetBallTest extends TalkHandlerSetBall {

	public TalkHandlerSetBallTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
