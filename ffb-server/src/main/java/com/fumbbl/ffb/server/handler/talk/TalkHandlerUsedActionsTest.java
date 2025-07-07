package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerUsedActionsTest extends TalkHandlerUsedActions {

	public TalkHandlerUsedActionsTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
