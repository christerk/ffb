package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.AbstractBlockLogicModule;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.Set;

public class BlockLogicModule extends AbstractBlockLogicModule {

	protected final BlockLogicExtension extension;

	public BlockLogicModule(FantasyFootballClient client) {
		super(client);
		extension = new BlockLogicExtension(client);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			if (isSufferingBloodLust(actingPlayer)) {
				return InteractionResult.selectAction(actionContext(actingPlayer));
			} else if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
				client.getCommunication().sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.BLITZ_MOVE,
					actingPlayer.isJumping());
				return InteractionResult.handled();
			} else {
				ActionContext actionContext = actionContext(actingPlayer);
				if (actionContext.getActions().isEmpty()) {
					deselectActingPlayer();
					return InteractionResult.handled();
				} else {
					return InteractionResult.selectAction(actionContext);
				}
			}
		} else {
			return block(player, actingPlayer);
		}
	}

	protected InteractionResult block(Player<?> player, ActingPlayer actingPlayer) {
		if (extension.isBlockable(client.getGame(), player)) {
			PlayerAction action = actingPlayer.getPlayerAction();
			client.getCommunication()
				.sendBlock(actingPlayer.getPlayerId(), player, action == PlayerAction.STAB, action == PlayerAction.CHAINSAW,
					action == PlayerAction.PROJECTILE_VOMIT, action == PlayerAction.BREATHE_FIRE);
		}
		return InteractionResult.ignore();
	}


	@Override
	public InteractionResult playerPeek(Player<?> player) {
		if (extension.isBlockable(client.getGame(), player)) {
			return InteractionResult.perform();
		}

		return InteractionResult.reset();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			add(ClientAction.BLOCK);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.BLACK_INK);
			add(ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext().merge(extension.actionContext(actingPlayer));
		if (isSufferingBloodLust(actingPlayer)) {
			actionContext.add(ClientAction.MOVE);
		}
		if (!actionContext.getActions().isEmpty() || actingPlayer.hasActed()) {
			actionContext.add(ClientAction.END_MOVE);
			if (actingPlayer.hasActed()) {
				actionContext.add(Influences.HAS_ACTED);
			}
		}
		return actionContext;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();

		switch (action) {
			case END_MOVE:
				communication.sendActingPlayer(null, null, false);
				break;
			case MOVE:
				communication.sendActingPlayer(player, PlayerAction.MOVE, client.getGame().getActingPlayer().isJumping());
				break;
			case BLOCK:
				block(player, actingPlayer);
				break;
			case TREACHEROUS:
				Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
				communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
				break;
			case WISDOM:
				communication.sendUseWisdom();
				break;
			case RAIDING_PARTY:
				Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
				communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
				break;
			case LOOK_INTO_MY_EYES:
				UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
					.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
				break;
			case BALEFUL_HEX:
				Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
				communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
				break;
			case BLACK_INK:
				Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
				communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
				break;
			case THEN_I_STARTED_BLASTIN:
				if (isThenIStartedBlastinAvailable(actingPlayer)) {
					Skill blastinSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
					communication.sendUseSkill(blastinSkill, true, actingPlayer.getPlayerId());
				}
				break;
			default:
				break;
		}
	}
}
