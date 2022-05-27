package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerStatTest extends TalkHandlerStat {

	public TalkHandlerStatTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
