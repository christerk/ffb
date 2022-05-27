package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSetPlayerTest extends TalkHandlerSetPlayer {

	public TalkHandlerSetPlayerTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
