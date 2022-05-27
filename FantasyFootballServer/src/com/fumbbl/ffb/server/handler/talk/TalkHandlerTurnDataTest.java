package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerTurnDataTest extends TalkHandlerTurnData {

	public TalkHandlerTurnDataTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
