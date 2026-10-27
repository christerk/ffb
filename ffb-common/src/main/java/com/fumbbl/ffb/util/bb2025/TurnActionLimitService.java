package com.fumbbl.ffb.util.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;

/**
 * Validates client requested player actions against the once per turn limits tracked in {@link TurnData}.
 * <p>
 * Clients only offer actions that are still available, but the server must not rely on that.
 */
public class TurnActionLimitService {

	public boolean isActionAvailable(Game game, Player<?> player, PlayerAction playerAction) {
		LimitedAction requested = LimitedAction.of(playerAction);
		if (requested == null) {
			return true;
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((player != null) && player.getId().equals(actingPlayer.getPlayerId())
			&& (requested == LimitedAction.of(actingPlayer.getPlayerAction()))) {
			// the acting player continues the declared action, e.g. toggles jumping or changes PASS_MOVE to PASS
			return true;
		}

		TurnData turnData = game.getTurnData();
		switch (requested) {
			case BLITZ:
				return !turnData.isBlitzUsed();
			case FOUL:
				return !turnData.isFoulUsed()
					|| ((player != null) && player.hasSkillProperty(NamedProperties.allowsAdditionalFoul));
			case PASS:
				return !turnData.isPassUsed();
			case HAND_OVER:
				return !turnData.isHandOverUsed();
			case THROW_TEAM_MATE:
				return !turnData.isTtmUsed();
			case KICK_TEAM_MATE:
				return !turnData.isKtmUsed();
			case BOMB:
				return !turnData.isBombUsed();
			case SECURE_THE_BALL:
				return !turnData.isSecureTheBallUsed();
			case PUNT:
				return !turnData.isPuntUsed();
			default:
				return true;
		}
	}

	private enum LimitedAction {
		BLITZ, FOUL, PASS, HAND_OVER, THROW_TEAM_MATE, KICK_TEAM_MATE, BOMB, SECURE_THE_BALL, PUNT;

		private static LimitedAction of(PlayerAction playerAction) {
			if (playerAction == null) {
				return null;
			}
			switch (playerAction) {
				case BLITZ:
				case BLITZ_MOVE:
				case BLITZ_SELECT:
				case KICK_EM_BLITZ:
				case STAND_UP_BLITZ:
				case FURIOUS_OUTPBURST:
					return BLITZ;
				case FOUL:
				case FOUL_MOVE:
					return FOUL;
				case PASS:
				case PASS_MOVE:
				case HAIL_MARY_PASS:
					return PASS;
				case HAND_OVER:
				case HAND_OVER_MOVE:
					return HAND_OVER;
				case THROW_TEAM_MATE:
				case THROW_TEAM_MATE_MOVE:
					return THROW_TEAM_MATE;
				case KICK_TEAM_MATE:
				case KICK_TEAM_MATE_MOVE:
					return KICK_TEAM_MATE;
				case THROW_BOMB:
				case HAIL_MARY_BOMB:
				case ALL_YOU_CAN_EAT:
					return BOMB;
				case SECURE_THE_BALL:
					return SECURE_THE_BALL;
				case PUNT:
				case PUNT_MOVE:
					return PUNT;
				default:
					return null;
			}
		}
	}
}
