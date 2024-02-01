package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSkillTest extends TalkHandlerSkill {

	public TalkHandlerSkillTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
