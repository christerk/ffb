package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerBoxLive extends TalkHandlerBox {

	public TalkHandlerBoxLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
