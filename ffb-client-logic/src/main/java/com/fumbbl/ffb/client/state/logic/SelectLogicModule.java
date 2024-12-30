package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class SelectLogicModule extends LogicModule {

	public SelectLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void postInit() {
		super.postInit();
		client.getGame().setDefenderId(null);
		client.getClientData().clearBlockDiceResult();
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if (game.getTeamHome().hasPlayer(player) && playerState.isActive()) {
			return InteractionResult.selectAction(new ActionContext(availableActions(player), influencingActions(player), findAlternateBlockActions(player)));
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.BLOCK);
			add(ClientAction.BLITZ);
			add(ClientAction.FRENZIED_RUSH);
			add(ClientAction.FOUL);
			add(ClientAction.MOVE);
			add(ClientAction.STAND_UP);
			add(ClientAction.STAND_UP_BLITZ);
			add(ClientAction.HAND_OVER);
			add(ClientAction.PASS);
			add(ClientAction.THROW_TEAM_MATE);
			add(ClientAction.KICK_TEAM_MATE);
			add(ClientAction.RECOVER);
			add(ClientAction.MULTIPLE_BLOCK);
			add(ClientAction.BOMB);
			add(ClientAction.GAZE);
			add(ClientAction.GAZE_ZOAT);
			add(ClientAction.SHOT_TO_NOTHING);
			add(ClientAction.SHOT_TO_NOTHING_BOMB);
			add(ClientAction.BEER_BARREL_BASH);
			add(ClientAction.ALL_YOU_CAN_EAT);
			add(ClientAction.KICK_EM_BLOCK);
			add(ClientAction.KICK_EM_BLITZ);
			add(ClientAction.THE_FLASHING_BLADE);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case BLOCK:
					communication.sendActingPlayer(player, PlayerAction.BLOCK, false);
					break;
				case BLITZ:
					communication.sendActingPlayer(player, PlayerAction.BLITZ_MOVE, false);
					break;
				case FRENZIED_RUSH:
					communication.sendActingPlayer(player, PlayerAction.BLITZ_MOVE, false);
					Skill skill = player.getSkillWithProperty(NamedProperties.canGainFrenzyForBlitz);
					communication.sendUseSkill(skill, true, player.getId());
					break;
				case FOUL:
					communication.sendActingPlayer(player, PlayerAction.FOUL_MOVE, false);
					break;
				case MOVE:
					communication.sendActingPlayer(player, PlayerAction.MOVE, false);
					break;
				case STAND_UP:
					communication.sendActingPlayer(player, PlayerAction.STAND_UP, false);
					break;
				case STAND_UP_BLITZ:
					communication.sendActingPlayer(player, PlayerAction.STAND_UP_BLITZ, false);
					break;
				case HAND_OVER:
					communication.sendActingPlayer(player, PlayerAction.HAND_OVER_MOVE, false);
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case PASS:
					communication.sendActingPlayer(player, PlayerAction.PASS_MOVE, false);
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case THROW_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.THROW_TEAM_MATE_MOVE, false);
					break;
				case KICK_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.KICK_TEAM_MATE_MOVE, false);
					break;
				case RECOVER:
					communication.sendActingPlayer(player, PlayerAction.REMOVE_CONFUSION, false);
					break;
				case MULTIPLE_BLOCK:
					communication.sendActingPlayer(player, PlayerAction.MULTIPLE_BLOCK, false);
					break;
				case BOMB:
					if (isThrowBombActionAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_BOMB, false);
					}
					break;
				case GAZE:
					communication.sendActingPlayer(player, PlayerAction.GAZE_MOVE, false);
					break;
				case GAZE_ZOAT:
					communication.sendActingPlayer(player, PlayerAction.GAZE_MOVE, false);
					Skill gazeSkill = player.getSkillWithProperty(NamedProperties.canGainGaze);
					communication.sendUseSkill(gazeSkill, true, player.getId());
					break;
				case SHOT_TO_NOTHING:
					communication.sendActingPlayer(player, PlayerAction.PASS_MOVE, false);
					Skill stnSkill = player.getSkillWithProperty(NamedProperties.canGainHailMary);
					communication.sendUseSkill(stnSkill, true, player.getId());
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case SHOT_TO_NOTHING_BOMB:
					if (isThrowBombActionAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_BOMB, false);
						Skill stnbSkill = player.getSkillWithProperty(NamedProperties.canGainHailMary);
						communication.sendUseSkill(stnbSkill, true, player.getId());
						if (isTreacherousAvailable(player)) {
							Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
							communication.sendUseSkill(treacherous, true, player.getId());
						}
					}
					break;
				case BEER_BARREL_BASH:
					if (isBeerBarrelBashAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_KEG, false);
					}
					break;
				case ALL_YOU_CAN_EAT:
					if (isAllYouCanEatAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.ALL_YOU_CAN_EAT, false);
					}
					break;
				case KICK_EM_BLOCK:
					if (isKickEmBlockAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.KICK_EM_BLOCK, false);
					}
					break;
				case KICK_EM_BLITZ:
					if (isKickEmBlitzAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.KICK_EM_BLITZ, false);
					}
					break;
				case THE_FLASHING_BLADE:
					if (isFlashingBladeAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THE_FLASHING_BLADE, false);
					}
					break;
				default:
					break;
			}
		}

	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

	private List<InfluencingAction> influencingActions(Player<?> player) {
		List<InfluencingAction> actions = new ArrayList<>();
		if (isTreacherousAvailable(player)) {
			actions.add(InfluencingAction.TREACHEROUS);
		}
		return actions;
	}

	private List<String> findAlternateBlockActions(Player<?> player) {
		return player.getSkillsIncludingTemporaryOnes().stream().filter(skill ->
				skill.hasSkillProperty(NamedProperties.providesBlockAlternative)
					&& SkillUsageType.REGULAR == skill.getSkillUsageType())
			.map(Skill::getName).collect(Collectors.toList());
	}

}
