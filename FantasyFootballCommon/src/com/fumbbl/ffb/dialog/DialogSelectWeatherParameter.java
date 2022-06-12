package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;

public class DialogSelectWeatherParameter implements IDialogParameter {

	private int roll;
	private Weather


	@Override

	public DialogId getId() {
		return DialogId.SELECT_WEATHER;
	}

	@Override
	public IDialogParameter transform() {
		return null;
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		return null;
	}

	@Override
	public JsonObject toJsonValue() {
		return null;
	}
}
