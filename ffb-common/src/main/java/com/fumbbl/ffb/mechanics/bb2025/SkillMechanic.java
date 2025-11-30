package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SkillMechanic extends com.fumbbl.ffb.mechanics.SkillMechanic {

	private static final Set<TurnMode> modesAllowingPro = new HashSet<TurnMode>() {{
		add(TurnMode.REGULAR);
		add(TurnMode.BLITZ);
		add(TurnMode.BOMB_HOME);
		add(TurnMode.BOMB_AWAY);
	}};

	@Override
	public boolean eligibleForPro(Game game, Player<?> player, String originalBomberId) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return (!game.getActingPlayer().isStandingUp() || game.getActingPlayer().hasActedIgnoringNegativeTraits())
			&& !playerState.isProneOrStunned() && !playerState.isStunned()
			&& game.getActingPlayer().getPlayer() == player
			&& modesAllowingPro.contains(game.getTurnMode())
			&& (!game.getTurnMode().isBombTurn() || player.getId().equals(originalBomberId));
	}

	@Override
	public boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player) {
		if (fieldModel.getPlayerState(player).isEyeGouged()) {
			return false;
		}
		return !(usingMultiBlock && fieldModel.isMultiBlockTarget(player.getId()));
	}

	@Override
	public boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate) {
		return !(fieldModel.wasMultiBlockTargetSquare(coordinate));
	}

	@Override
	public boolean canPreventStripBall(PlayerState playerState) {
		return playerState.hasTacklezones();
	}

	@Override
	public boolean allowsCancellingGuard(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public String calculatePlayerLevel(Game game, Player<?> player) {
		int gainedSkills = (int) player.skillInfos().stream()
			.filter(info -> info.getCategory() == SkillDisplayInfo.Category.PLAYER
				&& info.getSkill().getCategory() != SkillCategory.STAT_DECREASE).count();

		switch (gainedSkills) {
			case 0:
				return "Rookie";
			case 1:
				return "Experienced";
			case 2:
				return "Veteran";
			case 3:
				return "Emerging";
			case 4:
				return "Star";
			case 5:
				return "Super Star";
			default:
				return "Legend";
		}
	}

	@Override
	public boolean canAlwaysAssistFoul(Game game, Player<?> assistant) {
		return assistant.hasSkillProperty(NamedProperties.canAlwaysAssistFouls);
	}

}
