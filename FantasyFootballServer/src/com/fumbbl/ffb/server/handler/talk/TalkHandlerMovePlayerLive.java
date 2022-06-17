package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerMovePlayerLive extends TalkHandlerMovePlayer {

	public TalkHandlerMovePlayerLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
