package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockBallAndChain;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockChainsaw;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockChoice;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockDodge;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockRoll;
import com.balancedbytes.games.ffb.server.step.action.block.StepBlockStatistics;
import com.balancedbytes.games.ffb.server.step.action.block.StepBothDown;
import com.balancedbytes.games.ffb.server.step.action.block.StepDauntless;
import com.balancedbytes.games.ffb.server.step.action.block.StepDropFallingPlayers;
import com.balancedbytes.games.ffb.server.step.action.block.StepDumpOff;
import com.balancedbytes.games.ffb.server.step.action.block.StepEndBlocking;
import com.balancedbytes.games.ffb.server.step.action.block.StepFollowup;
import com.balancedbytes.games.ffb.server.step.action.block.StepFoulAppearance;
import com.balancedbytes.games.ffb.server.step.action.block.StepHorns;
import com.balancedbytes.games.ffb.server.step.action.block.StepInitBlocking;
import com.balancedbytes.games.ffb.server.step.action.block.StepJuggernaut;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.step.action.block.StepStab;
import com.balancedbytes.games.ffb.server.step.action.block.StepWrestle;
import com.balancedbytes.games.ffb.server.step.action.common.StepApothecary;
import com.balancedbytes.games.ffb.server.step.action.common.StepBloodLust;
import com.balancedbytes.games.ffb.server.step.action.common.StepBoneHead;
import com.balancedbytes.games.ffb.server.step.action.common.StepCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.step.action.common.StepEndTurn;
import com.balancedbytes.games.ffb.server.step.action.common.StepFallDown;
import com.balancedbytes.games.ffb.server.step.action.common.StepGoForIt;
import com.balancedbytes.games.ffb.server.step.action.common.StepPickUp;
import com.balancedbytes.games.ffb.server.step.action.common.StepReallyStupid;
import com.balancedbytes.games.ffb.server.step.action.common.StepShadowing;
import com.balancedbytes.games.ffb.server.step.action.common.StepTakeRoot;
import com.balancedbytes.games.ffb.server.step.action.common.StepWildAnimal;
import com.balancedbytes.games.ffb.server.step.action.end.StepEndFeeding;
import com.balancedbytes.games.ffb.server.step.action.end.StepInitFeeding;
import com.balancedbytes.games.ffb.server.step.action.foul.StepBribes;
import com.balancedbytes.games.ffb.server.step.action.foul.StepEjectPlayer;
import com.balancedbytes.games.ffb.server.step.action.foul.StepEndFouling;
import com.balancedbytes.games.ffb.server.step.action.foul.StepFoul;
import com.balancedbytes.games.ffb.server.step.action.foul.StepFoulChainsaw;
import com.balancedbytes.games.ffb.server.step.action.foul.StepInitFouling;
import com.balancedbytes.games.ffb.server.step.action.foul.StepReferee;
import com.balancedbytes.games.ffb.server.step.action.move.StepDivingTackle;
import com.balancedbytes.games.ffb.server.step.action.move.StepDropDivingTackler;
import com.balancedbytes.games.ffb.server.step.action.move.StepEndMoving;
import com.balancedbytes.games.ffb.server.step.action.move.StepHypnoticGaze;
import com.balancedbytes.games.ffb.server.step.action.move.StepInitMoving;
import com.balancedbytes.games.ffb.server.step.action.move.StepLeap;
import com.balancedbytes.games.ffb.server.step.action.move.StepMove;
import com.balancedbytes.games.ffb.server.step.action.move.StepMoveBallAndChain;
import com.balancedbytes.games.ffb.server.step.action.move.StepMoveDodge;
import com.balancedbytes.games.ffb.server.step.action.move.StepTentacles;
import com.balancedbytes.games.ffb.server.step.action.pass.StepAnimosity;
import com.balancedbytes.games.ffb.server.step.action.pass.StepBombardier;
import com.balancedbytes.games.ffb.server.step.action.pass.StepDispatchPassing;
import com.balancedbytes.games.ffb.server.step.action.pass.StepEndPassing;
import com.balancedbytes.games.ffb.server.step.action.pass.StepHailMaryPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepHandOver;
import com.balancedbytes.games.ffb.server.step.action.pass.StepInitPassing;
import com.balancedbytes.games.ffb.server.step.action.pass.StepIntercept;
import com.balancedbytes.games.ffb.server.step.action.pass.StepMissedPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPass;
import com.balancedbytes.games.ffb.server.step.action.pass.StepPassBlock;
import com.balancedbytes.games.ffb.server.step.action.pass.StepSafeThrow;
import com.balancedbytes.games.ffb.server.step.action.select.StepEndSelecting;
import com.balancedbytes.games.ffb.server.step.action.select.StepInitSelecting;
import com.balancedbytes.games.ffb.server.step.action.select.StepJumpUp;
import com.balancedbytes.games.ffb.server.step.action.select.StepStandUp;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepAlwaysHungry;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepEatTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepEndScatterPlayer;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepEndThrowTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepFumbleTtmPass;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepInitScatterPlayer;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepInitThrowTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepRightStuff;
import com.balancedbytes.games.ffb.server.step.action.ttm.StepThrowTeamMate;
import com.balancedbytes.games.ffb.server.step.game.end.StepEndGame;
import com.balancedbytes.games.ffb.server.step.game.end.StepFanFactor;
import com.balancedbytes.games.ffb.server.step.game.end.StepInitEndGame;
import com.balancedbytes.games.ffb.server.step.game.end.StepMvp;
import com.balancedbytes.games.ffb.server.step.game.end.StepPenaltyShootout;
import com.balancedbytes.games.ffb.server.step.game.end.StepPlayerLoss;
import com.balancedbytes.games.ffb.server.step.game.end.StepWinnings;
import com.balancedbytes.games.ffb.server.step.game.start.StepBuyCards;
import com.balancedbytes.games.ffb.server.step.game.start.StepBuyInducements;
import com.balancedbytes.games.ffb.server.step.game.start.StepInitStartGame;
import com.balancedbytes.games.ffb.server.step.game.start.StepPettyCash;
import com.balancedbytes.games.ffb.server.step.game.start.StepSpectators;
import com.balancedbytes.games.ffb.server.step.game.start.StepWeather;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepEndInducement;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepInitCard;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepInitInducement;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepWizard;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepApplyKickoffResult;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepBlitzTurn;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepCoinChoice;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepEndKickoff;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepInitKickoff;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffAnimation;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffResultRoll;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffReturn;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffScatterRoll;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepReceiveChoice;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepTouchback;
import com.balancedbytes.games.ffb.server.step.phase.special.StepEndBomb;
import com.balancedbytes.games.ffb.server.step.phase.special.StepInitBomb;
import com.balancedbytes.games.ffb.server.step.phase.special.StepSpecialEffect;

/**
 * Generator class for steps.
 * 
 * @author Kalimar
 */
public class StepFactory {
	
	private static StepFactory _INSTANCE = new StepFactory();
	
	/**
	 * @return the only instance of this class.
	 */
	public static StepFactory getInstance() {
		return _INSTANCE;
	}
	
	private StepFactory() {
		super();
	}
	
	public IStep create(StepId pStepId, GameState pGameState, String pLabel, StepParameterSet pParameterSet) {
		
		IStep step = null;

		if (pStepId != null) {
			switch (pStepId) {
			
				case ALWAYS_HUNGRY:
					step = new StepAlwaysHungry(pGameState);
					break;
				case ANIMOSITY:
					step = new StepAnimosity(pGameState);
					break;
				case APOTHECARY:
					step = new StepApothecary(pGameState);
					break;
				case APPLY_KICKOFF_RESULT:
					step = new StepApplyKickoffResult(pGameState);
					break;
				case BLITZ_TURN:
					step = new StepBlitzTurn(pGameState);
					break;
				case BLOCK_BALL_AND_CHAIN:
					step = new StepBlockBallAndChain(pGameState);
					break;
				case BLOCK_CHAINSAW:
					step = new StepBlockChainsaw(pGameState);
					break;
				case BLOCK_CHOICE:
					step = new StepBlockChoice(pGameState);
					break;
				case BLOCK_DODGE:
					step = new StepBlockDodge(pGameState);
					break;
				case BLOCK_ROLL:
					step = new StepBlockRoll(pGameState);
					break;
				case BLOCK_STATISTICS:
					step = new StepBlockStatistics(pGameState);
					break;
				case BLOOD_LUST:
					step = new StepBloodLust(pGameState);
					break;
				case BOMBARDIER:
					step = new StepBombardier(pGameState);
					break;
				case BONE_HEAD:
					step = new StepBoneHead(pGameState);
					break;
				case BOTH_DOWN:
					step =  new StepBothDown(pGameState);
					break;
				case BRIBES:
					step = new StepBribes(pGameState);
					break;
				case BUY_CARDS:
					step = new StepBuyCards(pGameState);
					break;
				case BUY_INDUCEMENTS:
					step = new StepBuyInducements(pGameState);
					break;
				case CATCH_SCATTER_THROW_IN:
					step = new StepCatchScatterThrowIn(pGameState);
					break;
				case COIN_CHOICE:
					step = new StepCoinChoice(pGameState);
					break;
				case DAUNTLESS:
					step = new StepDauntless(pGameState);
					break;
				case DISPATCH_PASSING:
					step = new StepDispatchPassing(pGameState);
					break;
				case DIVING_TACKLE:
					step = new StepDivingTackle(pGameState);
					break;
				case DROP_DIVING_TACKLER:
					step = new StepDropDivingTackler(pGameState);
					break;
				case DROP_FALLING_PLAYERS:
					step = new StepDropFallingPlayers(pGameState);
					break;
				case DUMP_OFF:
					step = new StepDumpOff(pGameState);
					break;
				case EAT_TEAM_MATE:
					step = new StepEatTeamMate(pGameState);
					break;
				case EJECT_PLAYER:
					step = new StepEjectPlayer(pGameState);
					break;
				case END_BLOCKING:
					step = new StepEndBlocking(pGameState);
					break;
				case END_BOMB:
					step = new StepEndBomb(pGameState);
					break;
				case END_FEEDING:
					step = new StepEndFeeding(pGameState);
					break;
				case END_FOULING:
					step = new StepEndFouling(pGameState);
					break;
				case END_GAME:
					step = new StepEndGame(pGameState);
					break;
				case END_INDUCEMENT:
					step = new StepEndInducement(pGameState);
					break;
				case END_KICKOFF:
					step = new StepEndKickoff(pGameState);
					break;
				case END_MOVING:
					step = new StepEndMoving(pGameState);
					break;
				case END_PASSING:
					step = new StepEndPassing(pGameState);
					break;
				case END_SCATTER_PLAYER:
					step = new StepEndScatterPlayer(pGameState);
					break;
				case END_SELECTING:
					step = new StepEndSelecting(pGameState);
					break;
				case END_THROW_TEAM_MATE:
					step = new StepEndThrowTeamMate(pGameState);
					break;
				case END_TURN:
					step = new StepEndTurn(pGameState);
					break;
				case FALL_DOWN:
					step = new StepFallDown(pGameState);
					break;
				case FAN_FACTOR:
					step = new StepFanFactor(pGameState);
					break;
				case FOLLOWUP:
					step = new StepFollowup(pGameState);
					break;
				case FOUL:
					step = new StepFoul(pGameState);
					break;
				case FOUL_APPEARANCE:
					step = new StepFoulAppearance(pGameState);
					break;
				case FOUL_CHAINSAW:
					step = new StepFoulChainsaw(pGameState);
					break;
				case FUMBLE_TTM_PASS:
					step = new StepFumbleTtmPass(pGameState);
					break;
				case GO_FOR_IT:
					step = new StepGoForIt(pGameState);
					break;
				case GOTO_LABEL:
					step = new StepGotoLabel(pGameState);
					break;
				case HAIL_MARY_PASS:
					step = new StepHailMaryPass(pGameState);
					break;
				case HAND_OVER:
					step = new StepHandOver(pGameState);
					break;
				case HORNS:
					step = new StepHorns(pGameState);
					break;
				case HYPNOTIC_GAZE:
					step = new StepHypnoticGaze(pGameState);
					break;
				case INIT_BLOCKING:
					step = new StepInitBlocking(pGameState);
					break;
				case INIT_BOMB:
					step = new StepInitBomb(pGameState);
					break;
				case INIT_CARD:
					step = new StepInitCard(pGameState);
					break;
				case INIT_END_GAME:
					step = new StepInitEndGame(pGameState);
					break;
				case INIT_FEEDING:
					step = new StepInitFeeding(pGameState);
					break;
				case INIT_FOULING:
					step = new StepInitFouling(pGameState);
					break;
				case INIT_INDUCEMENT:
					step = new StepInitInducement(pGameState);
					break;
				case INIT_KICKOFF:
					step = new StepInitKickoff(pGameState);
					break;
				case INIT_MOVING:
					step = new StepInitMoving(pGameState);
					break;
				case INIT_PASSING:
					step = new StepInitPassing(pGameState);
					break;
				case INIT_SCATTER_PLAYER:
					step = new StepInitScatterPlayer(pGameState);
					break;
				case INIT_SELECTING:
					step = new StepInitSelecting(pGameState);
					break;
				case INIT_START_GAME:
					step = new StepInitStartGame(pGameState);
					break;
				case INIT_THROW_TEAM_MATE:
					step = new StepInitThrowTeamMate(pGameState);
					break;
				case INTERCEPT:
					step = new StepIntercept(pGameState);
					break;
				case JUGGERNAUT:
					step = new StepJuggernaut(pGameState);
					break;
				case JUMP_UP:
					step = new StepJumpUp(pGameState);
					break;
				case KICKOFF_ANIMATION:
					step = new StepKickoffAnimation(pGameState);
					break;
				case KICKOFF_RESULT_ROLL:
					step = new StepKickoffResultRoll(pGameState);
					break;
				case KICKOFF_RETURN:
					step = new StepKickoffReturn(pGameState);
					break;
				case KICKOFF_SCATTER_ROLL:
					step = new StepKickoffScatterRoll(pGameState);
					break;
				case LEAP:
					step = new StepLeap(pGameState);
					break;
				case MISSED_PASS:
					step = new StepMissedPass(pGameState);
					break;
				case MOVE:
					step = new StepMove(pGameState);
					break;
				case MOVE_BALL_AND_CHAIN:
					step = new StepMoveBallAndChain(pGameState);
					break;
				case MOVE_DODGE:
					step = new StepMoveDodge(pGameState);
					break;
				case MVP:
					step = new StepMvp(pGameState);
					break;
				case NEXT_STEP:
					step = new StepNextStep(pGameState);
					break;
				case PASS:
					step = new StepPass(pGameState);
					break;
				case PASS_BLOCK:
					step = new StepPassBlock(pGameState);
					break;
				case PENALTY_SHOOTOUT:
					step = new StepPenaltyShootout(pGameState);
					break;
				case PETTY_CASH:
					step = new StepPettyCash(pGameState);
					break;
				case PICK_UP:
					step = new StepPickUp(pGameState);
					break;
				case PLAYER_LOSS:
					step = new StepPlayerLoss(pGameState);
					break;
				case PUSHBACK:
					step = new StepPushback(pGameState);
					break;
				case REALLY_STUPID:
					step = new StepReallyStupid(pGameState);
					break;
				case RECEIVE_CHOICE:
					step = new StepReceiveChoice(pGameState);
					break;
				case REFEREE:
					step = new StepReferee(pGameState);
					break;
				case RIGHT_STUFF:
					step = new StepRightStuff(pGameState);
					break;
				case SAFE_THROW:
					step = new StepSafeThrow(pGameState);
					break;
				case SHADOWING:
					step = new StepShadowing(pGameState);
					break;
				case SPECTATORS:
					step = new StepSpectators(pGameState);
					break;
				case SPECIAL_EFFECT:
					step = new StepSpecialEffect(pGameState);
					break;
				case STAB:
					step = new StepStab(pGameState);
					break;
				case STAND_UP:
					step = new StepStandUp(pGameState);
					break;
				case TAKE_ROOT:
					step = new StepTakeRoot(pGameState);
					break;
				case TENTACLES:
					step = new StepTentacles(pGameState);
					break;
				case THROW_TEAM_MATE:
					step = new StepThrowTeamMate(pGameState);
					break;
				case TOUCHBACK:
					step = new StepTouchback(pGameState);
					break;
				case WEATHER:
					step = new StepWeather(pGameState);
					break;
				case WILD_ANIMAL:
					step = new StepWildAnimal(pGameState);
					break;
				case WINNINGS:
					step = new StepWinnings(pGameState);
					break;
				case WIZARD:
					step = new StepWizard(pGameState);
					break;
				case WRESTLE:
					step = new StepWrestle(pGameState);
					break;
				default:
					throw new StepException("Unhandled StepId " + pStepId);
			}
		}
		
		if (step != null) {
			if (pLabel != null) {
				step.setLabel(pLabel);
			}
			if (pParameterSet != null) {
				step.init(pParameterSet);
			}
		}
		
		return step;
		
	}

}
