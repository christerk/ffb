package com.balancedbytes.games.ffb.server.step;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook.HookPoint;
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
import com.balancedbytes.games.ffb.server.step.action.ktm.StepEndKickTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ktm.StepInitKickTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ktm.StepKickTeamMate;
import com.balancedbytes.games.ffb.server.step.action.ktm.StepKickTeamMateDoubleRolled;
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
import com.balancedbytes.games.ffb.server.step.action.ttm.StepSwoop;
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
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepInitInducement;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepPlayCard;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepRiotousRookies;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepWizard;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepApplyKickoffResult;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepBlitzTurn;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepCoinChoice;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepEndKickoff;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepInitKickoff;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoff;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffAnimation;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffResultRoll;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffReturn;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepKickoffScatterRoll;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepReceiveChoice;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepSetup;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepSwarming;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepTouchback;
import com.balancedbytes.games.ffb.server.step.phase.special.StepEndBomb;
import com.balancedbytes.games.ffb.server.step.phase.special.StepInitBomb;
import com.balancedbytes.games.ffb.server.step.phase.special.StepSpecialEffect;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Generator class for steps.
 * 
 * @author Kalimar
 */
public class StepFactory {

	private GameState fGameState;
	private HashMap<StepId, Class<? extends IStep>> stepRegistry = new HashMap<>();
	private HashMap<HookPoint, List<StepId>> hooks = new HashMap<>();

	public StepFactory(GameState pGameState) {
		fGameState = pGameState;
		
		for (HookPoint p : HookPoint.values()) {
			hooks.put(p, new ArrayList<>());
		}
	}

	public IStep forStepId(StepId pStepId) {
		return create(pStepId, null, null);
	}

	public IStep create(StepId pStepId, String pLabel, StepParameterSet pParameterSet) {

		IStep step = null;

		if (pStepId != null) {
			switch (pStepId) {

			case ALWAYS_HUNGRY:
				step = new StepAlwaysHungry(fGameState);
				break;
			case ANIMOSITY:
				step = new StepAnimosity(fGameState);
				break;
			case APOTHECARY:
				step = new StepApothecary(fGameState);
				break;
			case APPLY_KICKOFF_RESULT:
				step = new StepApplyKickoffResult(fGameState);
				break;
			case BLITZ_TURN:
				step = new StepBlitzTurn(fGameState);
				break;
			case BLOCK_BALL_AND_CHAIN:
				step = new StepBlockBallAndChain(fGameState);
				break;
			case BLOCK_CHAINSAW:
				step = new StepBlockChainsaw(fGameState);
				break;
			case BLOCK_CHOICE:
				step = new StepBlockChoice(fGameState);
				break;
			case BLOCK_DODGE:
				step = new StepBlockDodge(fGameState);
				break;
			case BLOCK_ROLL:
				step = new StepBlockRoll(fGameState);
				break;
			case BLOCK_STATISTICS:
				step = new StepBlockStatistics(fGameState);
				break;
			case BLOOD_LUST:
				step = new StepBloodLust(fGameState);
				break;
			case BOMBARDIER:
				step = new StepBombardier(fGameState);
				break;
			case BONE_HEAD:
				step = new StepBoneHead(fGameState);
				break;
			case BOTH_DOWN:
				step = new StepBothDown(fGameState);
				break;
			case BRIBES:
				step = new StepBribes(fGameState);
				break;
			case BUY_CARDS:
				step = new StepBuyCards(fGameState);
				break;
			case BUY_INDUCEMENTS:
				step = new StepBuyInducements(fGameState);
				break;
			case CATCH_SCATTER_THROW_IN:
				step = new StepCatchScatterThrowIn(fGameState);
				break;
			case COIN_CHOICE:
				step = new StepCoinChoice(fGameState);
				break;
			case DAUNTLESS:
				step = new StepDauntless(fGameState);
				break;
			case DISPATCH_PASSING:
				step = new StepDispatchPassing(fGameState);
				break;
			case DIVING_TACKLE:
				step = new StepDivingTackle(fGameState);
				break;
			case DROP_DIVING_TACKLER:
				step = new StepDropDivingTackler(fGameState);
				break;
			case DROP_FALLING_PLAYERS:
				step = new StepDropFallingPlayers(fGameState);
				break;
			case DUMP_OFF:
				step = new StepDumpOff(fGameState);
				break;
			case EAT_TEAM_MATE:
				step = new StepEatTeamMate(fGameState);
				break;
			case EJECT_PLAYER:
				step = new StepEjectPlayer(fGameState);
				break;
			case END_BLOCKING:
				step = new StepEndBlocking(fGameState);
				break;
			case END_BOMB:
				step = new StepEndBomb(fGameState);
				break;
			case END_FEEDING:
				step = new StepEndFeeding(fGameState);
				break;
			case END_FOULING:
				step = new StepEndFouling(fGameState);
				break;
			case END_GAME:
				step = new StepEndGame(fGameState);
				break;
			case END_INDUCEMENT:
				step = new StepEndInducement(fGameState);
				break;
			case END_KICK_TEAM_MATE:
				step = new StepEndKickTeamMate(fGameState);
				break;
			case END_KICKOFF:
				step = new StepEndKickoff(fGameState);
				break;
			case END_MOVING:
				step = new StepEndMoving(fGameState);
				break;
			case END_PASSING:
				step = new StepEndPassing(fGameState);
				break;
			case END_SCATTER_PLAYER:
				step = new StepEndScatterPlayer(fGameState);
				break;
			case END_SELECTING:
				step = new StepEndSelecting(fGameState);
				break;
			case END_THROW_TEAM_MATE:
				step = new StepEndThrowTeamMate(fGameState);
				break;
			case END_TURN:
				step = new StepEndTurn(fGameState);
				break;
			case FALL_DOWN:
				step = new StepFallDown(fGameState);
				break;
			case FAN_FACTOR:
				step = new StepFanFactor(fGameState);
				break;
			case FOLLOWUP:
				step = new StepFollowup(fGameState);
				break;
			case FOUL:
				step = new StepFoul(fGameState);
				break;
			case FOUL_APPEARANCE:
				step = new StepFoulAppearance(fGameState);
				break;
			case FOUL_CHAINSAW:
				step = new StepFoulChainsaw(fGameState);
				break;
			case FUMBLE_TTM_PASS:
				step = new StepFumbleTtmPass(fGameState);
				break;
			case GO_FOR_IT:
				step = new StepGoForIt(fGameState);
				break;
			case GOTO_LABEL:
				step = new StepGotoLabel(fGameState);
				break;
			case HAIL_MARY_PASS:
				step = new StepHailMaryPass(fGameState);
				break;
			case HAND_OVER:
				step = new StepHandOver(fGameState);
				break;
			case HORNS:
				step = new StepHorns(fGameState);
				break;
			case HYPNOTIC_GAZE:
				step = new StepHypnoticGaze(fGameState);
				break;
			case INIT_BLOCKING:
				step = new StepInitBlocking(fGameState);
				break;
			case INIT_BOMB:
				step = new StepInitBomb(fGameState);
				break;
			case PLAY_CARD:
				step = new StepPlayCard(fGameState);
				break;
			case INIT_END_GAME:
				step = new StepInitEndGame(fGameState);
				break;
			case INIT_FEEDING:
				step = new StepInitFeeding(fGameState);
				break;
			case INIT_FOULING:
				step = new StepInitFouling(fGameState);
				break;
			case INIT_INDUCEMENT:
				step = new StepInitInducement(fGameState);
				break;
			case INIT_KICK_TEAM_MATE:
				step = new StepInitKickTeamMate(fGameState);
				break;
			case INIT_KICKOFF:
				step = new StepInitKickoff(fGameState);
				break;
			case INIT_MOVING:
				step = new StepInitMoving(fGameState);
				break;
			case INIT_PASSING:
				step = new StepInitPassing(fGameState);
				break;
			case INIT_SCATTER_PLAYER:
				step = new StepInitScatterPlayer(fGameState);
				break;
			case INIT_SELECTING:
				step = new StepInitSelecting(fGameState);
				break;
			case INIT_START_GAME:
				step = new StepInitStartGame(fGameState);
				break;
			case INIT_THROW_TEAM_MATE:
				step = new StepInitThrowTeamMate(fGameState);
				break;
			case INTERCEPT:
				step = new StepIntercept(fGameState);
				break;
			case JUGGERNAUT:
				step = new StepJuggernaut(fGameState);
				break;
			case JUMP_UP:
				step = new StepJumpUp(fGameState);
				break;
			case KICK_TEAM_MATE:
				step = new StepKickTeamMate(fGameState);
				break;
			case KICK_TM_DOUBLE_ROLLED:
				step = new StepKickTeamMateDoubleRolled(fGameState);
				break;
			case KICKOFF:
				step = new StepKickoff(fGameState);
				break;
			case KICKOFF_ANIMATION:
				step = new StepKickoffAnimation(fGameState);
				break;
			case KICKOFF_RESULT_ROLL:
				step = new StepKickoffResultRoll(fGameState);
				break;
			case KICKOFF_RETURN:
				step = new StepKickoffReturn(fGameState);
				break;
			case KICKOFF_SCATTER_ROLL:
				step = new StepKickoffScatterRoll(fGameState);
				break;
			case LEAP:
				step = new StepLeap(fGameState);
				break;
			case MISSED_PASS:
				step = new StepMissedPass(fGameState);
				break;
			case MOVE:
				step = new StepMove(fGameState);
				break;
			case MOVE_BALL_AND_CHAIN:
				step = new StepMoveBallAndChain(fGameState);
				break;
			case MOVE_DODGE:
				step = new StepMoveDodge(fGameState);
				break;
			case MVP:
				step = new StepMvp(fGameState);
				break;
			case NEXT_STEP:
				step = new StepNextStep(fGameState);
				break;
			case PASS:
				step = new StepPass(fGameState);
				break;
			case PASS_BLOCK:
				step = new StepPassBlock(fGameState);
				break;
			case PENALTY_SHOOTOUT:
				step = new StepPenaltyShootout(fGameState);
				break;
			case PETTY_CASH:
				step = new StepPettyCash(fGameState);
				break;
			case PICK_UP:
				step = new StepPickUp(fGameState);
				break;
			case PLAYER_LOSS:
				step = new StepPlayerLoss(fGameState);
				break;
			case PUSHBACK:
				step = new StepPushback(fGameState);
				break;
			case REALLY_STUPID:
				step = new StepReallyStupid(fGameState);
				break;
			case RECEIVE_CHOICE:
				step = new StepReceiveChoice(fGameState);
				break;
			case REFEREE:
				step = new StepReferee(fGameState);
				break;
			case RIGHT_STUFF:
				step = new StepRightStuff(fGameState);
				break;
			case RIOTOUS_ROOKIES:
				step = new StepRiotousRookies(fGameState);
				break;
			case SAFE_THROW:
				step = new StepSafeThrow(fGameState);
				break;
			case SETUP:
				step = new StepSetup(fGameState);
				break;
			case SHADOWING:
				step = new StepShadowing(fGameState);
				break;
			case SPECTATORS:
				step = new StepSpectators(fGameState);
				break;
			case SPECIAL_EFFECT:
				step = new StepSpecialEffect(fGameState);
				break;
			case STAB:
				step = new StepStab(fGameState);
				break;
			case STAND_UP:
				step = new StepStandUp(fGameState);
				break;
			case SWARMING:
				step = new StepSwarming(fGameState);
				break;
			case SWOOP:
				step = new StepSwoop(fGameState);
				break;
			case TAKE_ROOT:
				step = new StepTakeRoot(fGameState);
				break;
			case TENTACLES:
				step = new StepTentacles(fGameState);
				break;
			case THROW_TEAM_MATE:
				step = new StepThrowTeamMate(fGameState);
				break;
			case TOUCHBACK:
				step = new StepTouchback(fGameState);
				break;
			case WEATHER:
				step = new StepWeather(fGameState);
				break;
			case WILD_ANIMAL:
				step = new StepWildAnimal(fGameState);
				break;
			case WINNINGS:
				step = new StepWinnings(fGameState);
				break;
			case WIZARD:
				step = new StepWizard(fGameState);
				break;
			case WRESTLE:
				step = new StepWrestle(fGameState);
				break;
			default:
				if (stepRegistry.containsKey(pStepId)) {
					Class<? extends IStep> stepClass = stepRegistry.get(pStepId);
					Constructor ctr;
					try {
						ctr = stepClass.getConstructor(GameState.class);
						step = (IStep) ctr.newInstance(fGameState);
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new StepException("Error constructing Step " + pStepId, e);
					}
				} else {
					throw new StepException("Unhandled StepId " + pStepId);
				}
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

	// JSON serialization

	public IStep forJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		IStep step = null;
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		StepId stepId = (StepId) IServerJsonOption.STEP_ID.getFrom(source, jsonObject);
		if (stepId != null) {
			step = forStepId(stepId);
			if (step != null) {
				step.initFrom(source, pJsonValue);
			}
		}
		return step;
	}

	public void initialize() {
		SkillFactory skillFactory = fGameState.getGame().<SkillFactory>getFactory(Factory.SKILL);
		
		Collection<ISkillBehaviour<? extends Skill>> behaviours = skillFactory.getBehaviours();
		
		for (ISkillBehaviour<? extends Skill> behaviour : behaviours) {
			Map<StepId, Class<? extends IStep>> steps = ((SkillBehaviour<? extends Skill>)behaviour).getSteps();
			steps.forEach((stepId, step) -> {
				StepHook hook = step.getAnnotation(StepHook.class);
				if (hook != null) {
					HookPoint hookPoint = hook.value();
					hooks.get(hookPoint).add(stepId);
				}
			});
			
			stepRegistry.putAll(((SkillBehaviour<? extends Skill>)behaviour).getSteps());
		}
	}

	public List<StepId> getSteps(HookPoint hookPoint) {
		return hooks.get(hookPoint);
	}

}
