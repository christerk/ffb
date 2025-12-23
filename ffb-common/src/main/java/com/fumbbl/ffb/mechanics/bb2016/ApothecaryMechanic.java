package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2016)
public class ApothecaryMechanic extends com.fumbbl.ffb.mechanics.ApothecaryMechanic {

	@Override
	public List<ApothecaryType> apothecaryTypes(Game game, Player<?> defender, PlayerState playerState) {
		return new ArrayList<>();
	}

}
