package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerInjuryTest extends TalkHandlerInjury {

	public TalkHandlerInjuryTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
