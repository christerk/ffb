package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.sun.istack.internal.Nullable;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class TreacherousTrapdoorHandler extends PrayerHandler {

	private final Set<FieldCoordinate> trapdoorCoordinates = new HashSet<FieldCoordinate>() {{
		add(new FieldCoordinate(5, 1));
		add(new FieldCoordinate(20, 13));
	}};

	@Override
	Prayer handledPrayer() {
		return Prayer.TREACHEROUS_TRAPDOOR;
	}

	@Override
	public void add(@Nullable IStep step, GameState gameState, Team prayingTeam) {
		FieldModel fieldModel = gameState.getGame().getFieldModel();
		trapdoorCoordinates.stream().map(TrapDoor::new).forEach(fieldModel::add);
		if (step != null) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	@Override
	public void removeEffect(GameState gameState) {
		gameState.getGame().getFieldModel().clearTrapdoors();
	}
}
