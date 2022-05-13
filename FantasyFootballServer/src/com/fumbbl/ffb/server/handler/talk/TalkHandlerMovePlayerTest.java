package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerMovePlayerTest extends TalkHandlerMovePlayer {

	public TalkHandlerMovePlayerTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
