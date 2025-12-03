package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.model.AnimationType;

public abstract class KnuckleDustersHandler extends SelectPlayerPrayerHandler {

	@Override
	protected PlayerChoiceMode choiceMode() {
		return PlayerChoiceMode.KNUCKLE_DUSTERS;
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_KNUCKLE_DUSTERS;
	}
}
