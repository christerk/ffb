package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerReRollTest extends TalkHandlerReRoll {

	public TalkHandlerReRollTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
