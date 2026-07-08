package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSkillsResetUsedLive extends TalkHandlerSkillsResetUsed {

	public TalkHandlerSkillsResetUsedLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
