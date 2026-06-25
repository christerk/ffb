package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSkillsResetUsedTest extends TalkHandlerSkillsResetUsed {

	public TalkHandlerSkillsResetUsedTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
