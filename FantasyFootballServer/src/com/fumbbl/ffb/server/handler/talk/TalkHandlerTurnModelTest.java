package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnModelTest extends TalkHandlerTurnMode {

	public TalkHandlerTurnModelTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
