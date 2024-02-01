package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerWeatherLive extends TalkHandlerWeather {

	public TalkHandlerWeatherLive() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}
}
