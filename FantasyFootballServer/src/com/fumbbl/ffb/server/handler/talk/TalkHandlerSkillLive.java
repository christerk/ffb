package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerSkillLive extends TalkHandlerSkill {

	public TalkHandlerSkillLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
