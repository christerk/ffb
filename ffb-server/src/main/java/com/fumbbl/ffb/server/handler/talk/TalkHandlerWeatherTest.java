package com.fumbbl.ffb.server.handler.talk;

public class TalkHandlerWeatherTest extends TalkHandlerWeather {

	public TalkHandlerWeatherTest() {
		super(new IdentityCommandAdapter(), TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}
}
