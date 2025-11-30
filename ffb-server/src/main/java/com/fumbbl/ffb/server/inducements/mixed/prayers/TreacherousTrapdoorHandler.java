package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;
import java.util.Set;

public abstract class TreacherousTrapdoorHandler extends PrayerHandler {

	private final Set<FieldCoordinate> trapdoorCoordinates = new HashSet<FieldCoordinate>() {{
		add(new FieldCoordinate(6, 1));
		add(new FieldCoordinate(19, 13));
	}};

	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		FieldModel fieldModel = gameState.getGame().getFieldModel();
		trapdoorCoordinates.stream().map(TrapDoor::new).forEach(fieldModel::add);
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {
		gameState.getGame().getFieldModel().clearTrapdoors();
	}

	@Override
	public AnimationType animationType() {
		return AnimationType.PRAYER_TREACHEROUS_TRAPDOOR;
	}

}
