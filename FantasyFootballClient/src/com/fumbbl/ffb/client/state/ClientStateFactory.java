package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.bb2016.ClientStateKickTeamMate;
import com.fumbbl.ffb.client.state.bb2020.ClientStateGazeMove;
import com.fumbbl.ffb.client.state.bb2020.ClientStateKickTeamMateLikeThrow;
import com.fumbbl.ffb.client.state.bb2020.ClientStateRaidingParty;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSelectBlitzTarget;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSelectGazeTarget;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSynchronousMultiBlock;
import com.fumbbl.ffb.client.state.bb2020.ClientStateThrowKeg;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateFactory {

	private final FantasyFootballClient fClient;

	private final Map<ClientStateId, ClientState> fClientStateById;

	public ClientStateFactory(FantasyFootballClient pClient) {
		fClient = pClient;
		fClientStateById = new HashMap<>();
		register(new ClientStateLogin(pClient));
		register(new ClientStateStartGame(pClient));
		register(new ClientStateSpectate(pClient));
		register(new ClientStateSelect(pClient));
		register(new ClientStatePass(pClient));
		register(new ClientStateHandOver(pClient));
		register(new ClientStateMove(pClient));
		register(new ClientStateKickoff(pClient));
		register(new ClientStateBlock(pClient));
		register(new ClientStatePushback(pClient));
		register(new ClientStateInterception(pClient));
		register(new ClientStateBlitz(pClient));
		register(new ClientStateFoul(pClient));
		register(new ClientStateSetup(pClient));
		register(new ClientStateQuickSnap(pClient));
		register(new ClientStateHighKick(pClient));
		register(new ClientStateTouchback(pClient));
		register(new ClientStateWaitForOpponent(pClient));
		register(new ClientStateReplay(pClient));
		register(new ClientStateThrowTeamMate(pClient));
		register(new ClientStateKickTeamMateLikeThrow(pClient));
		register(new ClientStateKickTeamMate(pClient));
		register(new ClientStateSwoop(pClient));
		register(new ClientStateDumpOff(pClient));
		register(new ClientStateWaitForSetup(pClient));
		register(new ClientStateGaze(pClient));
		register(new ClientStateKickoffReturn(pClient));
		register(new ClientStateSwarming(pClient));
		register(new ClientStateWizard(pClient));
		register(new ClientStatePassBlock(pClient));
		register(new ClientStateBomb(pClient));
		register(new ClientStateIllegalSubstitution(pClient));
		register(new ClientStateSelectBlitzTarget(pClient));
		register(new ClientStateSynchronousMultiBlock(pClient));
		register(new ClientStatePlaceBall(pClient));
		register(new ClientStateSolidDefence(pClient));
		register(new ClientStateSelectGazeTarget(pClient));
		register(new ClientStateGazeMove(pClient));
		register(new ClientStateThrowKeg(pClient));
		register(new ClientStateRaidingParty(pClient));
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public ClientState getStateForId(ClientStateId pClientStateId) {
		return fClientStateById.get(pClientStateId);
	}

	private void register(ClientState pClientState) {
		fClientStateById.put(pClientState.getId(), pClientState);
	}

	public ClientState getStateForGame() {
		ClientStateId clientStateId = null;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((ClientMode.REPLAY == getClient().getMode()) || getClient().getReplayer().isReplaying()) {
			clientStateId = ClientStateId.REPLAY;
		} else if (!StringTool.isProvided(game.getTeamHome().getName())) {
			clientStateId = ClientStateId.LOGIN;
		} else if (ClientMode.SPECTATOR == getClient().getMode()) {
			clientStateId = ClientStateId.SPECTATE;
		} else if (game.getFinished() != null) {
			clientStateId = ClientStateId.SPECTATE;
		} else if (game.isHomePlaying() && game.isWaitingForOpponent()) {
			clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
//    } else if (game.getDefenderAction() != null) {
//      if (game.isHomePlaying()) {
//        if ((game.getDefenderAction() == PlayerAction.DUMP_OFF) && (game.getTurnMode() == TurnMode.INTERCEPTION)) {
//          clientStateId = ClientStateId.INTERCEPTION;
//        } else {
//          clientStateId = findPassiveState();
//        }
//      } else {
//        switch (game.getDefenderAction()) {
//          case DUMP_OFF:
//            clientStateId = ClientStateId.DUMP_OFF;
//            break;
//        }
//      }
		} else {
			switch (game.getTurnMode()) {
				case SELECT_BLITZ_TARGET:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.SELECT_BLITZ_TARGET;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case SELECT_GAZE_TARGET:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.SELECT_GAZE_TARGET;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case BLITZ:
				case REGULAR:
					if (game.isHomePlaying()) {
						if (actingPlayer.getPlayer() == null) {
							clientStateId = ClientStateId.SELECT_PLAYER;
						} else if (ArrayTool.isProvided(game.getFieldModel().getPushbackSquares())) {
							clientStateId = ClientStateId.PUSHBACK;
						} else {
							switch (actingPlayer.getPlayerAction()) {
								case MOVE:
								case STAND_UP:
								case STAND_UP_BLITZ:
									clientStateId = ClientStateId.MOVE;
									break;
								case BLITZ_MOVE:
									clientStateId = ClientStateId.BLITZ;
									break;
								case BLITZ:
								case BLOCK:
									clientStateId = ClientStateId.BLOCK;
									break;
								case MULTIPLE_BLOCK:
									clientStateId = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canBlockTwoAtOnce)
										? ClientStateId.SYNCHRONOUS_MULTI_BLOCK : ClientStateId.BLOCK;
									break;
								case FOUL:
								case FOUL_MOVE:
									clientStateId = ClientStateId.FOUL;
									break;
								case HAND_OVER:
								case HAND_OVER_MOVE:
									clientStateId = ClientStateId.HAND_OVER;
									break;
								case PASS:
								case PASS_MOVE:
								case HAIL_MARY_PASS:
									clientStateId = ClientStateId.PASS;
									break;
								case THROW_TEAM_MATE:
								case THROW_TEAM_MATE_MOVE:
									clientStateId = ClientStateId.THROW_TEAM_MATE;
									break;
								case KICK_TEAM_MATE:
								case KICK_TEAM_MATE_MOVE:
									TtmMechanic mechanic = (TtmMechanic) getClient().getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
									clientStateId = mechanic.handleKickLikeThrow() ? ClientStateId.KICK_TEAM_MATE_THROW : ClientStateId.KICK_TEAM_MATE;
									break;
								case SWOOP:
									clientStateId = ClientStateId.SWOOP;
									break;
								case GAZE:
									clientStateId = ClientStateId.GAZE;
									break;
								case THROW_BOMB:
								case HAIL_MARY_BOMB:
									clientStateId = ClientStateId.BOMB;
									break;
								case GAZE_MOVE:
									clientStateId = ClientStateId.GAZE_MOVE;
									break;
								case THROW_KEG:
									clientStateId = ClientStateId.THROW_KEG;
									break;
								default:
									break;
							}
						}
					} else {
						clientStateId = findPassiveState();
					}
					break;
				case KICKOFF:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.KICKOFF;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case KICKOFF_RETURN:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.KICKOFF_RETURN;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case SWARMING:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.SWARMING;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case PASS_BLOCK:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.PASS_BLOCK;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case START_GAME:
					clientStateId = ClientStateId.START_GAME;
					break;
				case SETUP:
				case PERFECT_DEFENCE:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.SETUP;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_SETUP;
					}
					break;
				case SOLID_DEFENCE:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.SOLID_DEFENCE;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_SETUP;
					}
					break;
				case HIGH_KICK:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.HIGH_KICK;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case QUICK_SNAP:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.QUICK_SNAP;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case ILLEGAL_SUBSTITUTION:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.ILLEGAL_SUBSTITUTION;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case TOUCHBACK:
					if (!game.isHomePlaying()) {
						clientStateId = ClientStateId.TOUCHBACK;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case INTERCEPTION:
//        if (!game.isHomePlaying() && !StringTool.isProvided(game.getDefenderId())) {
					if ((!game.isHomePlaying() && (game.getThrowerAction() != PlayerAction.DUMP_OFF))
						|| (game.isHomePlaying() && (game.getThrowerAction() == PlayerAction.DUMP_OFF))) {
						clientStateId = ClientStateId.INTERCEPTION;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case DUMP_OFF:
					if (!game.isHomePlaying()) {
						clientStateId = ClientStateId.DUMP_OFF;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case WIZARD:
					if (game.isHomePlaying() || getClient().getClientData().getWizardSpell() != null) {
						clientStateId = ClientStateId.WIZARD;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case BOMB_HOME:
				case BOMB_HOME_BLITZ:
				case BOMB_AWAY:
				case BOMB_AWAY_BLITZ:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.BOMB;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				case SAFE_PAIR_OF_HANDS:
					if (game.isHomePlaying()) {
						clientStateId = ClientStateId.PLACE_BALL;
					} else {
						clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
					}
					break;
				default:
					break;
			}
		}
		return getStateForId(clientStateId);
	}

	private ClientStateId findPassiveState() {
		ClientStateId clientStateId;
		Game game = getClient().getGame();
		if (ArrayTool.isProvided(game.getFieldModel().getPushbackSquares()) && game.isWaitingForOpponent()) {
			clientStateId = ClientStateId.PUSHBACK;
		} else {
			clientStateId = ClientStateId.WAIT_FOR_OPPONENT;
		}
		return clientStateId;
	}

}
