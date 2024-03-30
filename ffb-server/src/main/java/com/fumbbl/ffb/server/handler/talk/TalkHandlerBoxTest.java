package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerBoxTest extends TalkHandlerBox {

	public TalkHandlerBoxTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
