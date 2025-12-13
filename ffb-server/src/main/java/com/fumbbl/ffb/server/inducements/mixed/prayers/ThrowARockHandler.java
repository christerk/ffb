package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.AnimationType;

public abstract class ThrowARockHandler extends PrayerHandler {
	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_THROW_A_ROCK;
	}
}
