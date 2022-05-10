package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnTest extends TalkHandlerTurn {

	public TalkHandlerTurnTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
