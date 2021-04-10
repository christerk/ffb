package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObject;

/**
 *
 * @author Kalimar
 */
public enum StepId implements INamedObject {

	ALWAYS_HUNGRY("alwaysHungry"), // action.ttm
	ANIMOSITY("animosity"), // action.pass
	APOTHECARY("apothecary"), // action.common
	APOTHECARY_MULTIPLE("apothecaryMultiple"), // action.multiBlock 2020
	APPLY_KICKOFF_RESULT("applyKickoffResult"), // phase.kickoff
	BLITZ_TURN("blitzTurn"), // phase.kickoff
	BLOCK_BALL_AND_CHAIN("blockBallAndChain"), // action.block
	BLOCK_CHAINSAW("blockChainsaw"), // action.block
	BLOCK_CHAINSAW_MULTIPLE("blockChainsawMultiple"), // action.multiBlock 2020
	BLOCK_CHOICE("blockChoice"), // action.block
	BLOCK_DODGE("blockDodge"), // action.block
	BLOCK_ROLL("blockRoll"), // action.block
	BLOCK_ROLL_MULTIPLE("blockRollMultiple"), // action.multiBlock 2020
	BLOCK_STATISTICS("blockStatistics"), // action.block
	BLOOD_LUST("bloodLust"), // action.common
	BOMBARDIER("bombardier"), // action.pass
	BONE_HEAD("boneHead"), // action.common
	BOTH_DOWN("bothDown"), // action.block
	BRIBES("bribes"), // action.foul
	BUY_CARDS("buyCards"), // game.start
	BUY_CARDS_AND_INDUCEMENTS("buyCardsAndInducements"), // game.start
	BUY_INDUCEMENTS("buyInducements"), // game.start
	CATCH_SCATTER_THROW_IN("catchScatterThrowIn"), // action.common
	COIN_CHOICE("coinChoice"), // game.start
	CLOUD_BURSTER("cloudBurster"), // action.pass
	DAUNTLESS("dauntless"), // action.block
	DAUNTLESS_MULTIPLE("dauntlessMultiple"), // action.multiBlock 2020
	DISPATCH_DUMP_OFF("dispatchDumpOff"),
	DISPATCH_PASSING("dispatchPassing"), // action.pass
	DIVING_TACKLE("divingTackle"), // action.move
	DROP_DIVING_TACKLER("dropDivingTackler"), // action.move
	DROP_FALLING_PLAYERS("dropFallingPlayers"), // action.block
	DUMP_OFF("dumpOff"), // action.block
	EAT_TEAM_MATE("eatTeamMate"), // action.ttm
	EJECT_PLAYER("ejectPlayer"), // action.foul
	END_BLOCKING("endBlocking"), // action.block
	END_BOMB("endBomb"), // phase.special
	END_FEEDING("endFeeding"), // action.end
	END_FOULING("endFouling"), // action.foul
	END_GAME("endGame"), // game.end
	END_INDUCEMENT("endInducement"), // phase.inducement
	END_KICKOFF("endKickoff"), // phase.kickoff
	END_MOVING("endMoving"), // action.move
	END_PASSING("endPassing"), // action.pass
	END_SCATTER_PLAYER("endScatterPlayer"), // action.ttm
	END_SELECTING("endSelecting"), // action.select
	END_THROW_TEAM_MATE("endThrowTeamMate"), // action.ttm
	END_KICK_TEAM_MATE("endKickTeamMate"), // action.kickTeamMate
	END_TURN("endTurn"), // action.common
	FALL_DOWN("fallDown"), // action.common
	FAN_FACTOR("fanFactor"), // game.end
	FOLLOWUP("followup"), // action.block
	FOUL("foul"), // action.foul
	FOUL_APPEARANCE("foulAppearance"), // action.block
	FOUL_APPEARANCE_MULTIPLE("foulAppearanceMultiple"), // action.multiBlock 2020
	FOUL_CHAINSAW("foulChainsaw"), // action.foul
	FUMBLE_TTM_PASS("fumbleTtmPass"), // action.ttm
	GO_FOR_IT("goForIt"), // action.common
	GOTO_LABEL("gotoLabel"), //
	HAIL_MARY_PASS("hailMaryPass"), // action.pass
	HAND_OVER("handOver"), // action.pass
	HORNS("horns"), // action.block
	HYPNOTIC_GAZE("hypnoticGaze"), // action.move
	INIT_BLOCKING("initBlocking"), // action.block
	INIT_BOMB("initBomb"), // phase.special
	INIT_END_GAME("initEndGame"), // game.end
	INIT_FEEDING("initFeeding"), // action.end
	INIT_FOULING("initFouling"), // action.foul
	INIT_INDUCEMENT("initInducement"), // phase.inducement
	INIT_KICKOFF("initKickoff"), // phase.kickoff
	INIT_MOVING("initMoving"), // action.move
	INIT_PASSING("initPassing"), // action.pass
	INIT_SCATTER_PLAYER("initScatterPlayer"), // action.ttm
	INIT_SELECTING("initSelecting"), // action.select
	INIT_START_GAME("initStartGame"), // game.start
	INIT_THROW_TEAM_MATE("initThrowTeamMate"), // action.ttm
	INIT_KICK_TEAM_MATE("initKickTeamMate"), // action.kickTeamMate
	INTERCEPT("intercept"), // action.pass
	JUGGERNAUT("juggernaut"), // action.block
	JUMP_UP("jumpUp"), // action.select
	KICK_TEAM_MATE("kickTeamMate"), // action.kickTeamMate
	KICK_TM_DOUBLE_ROLLED("kickTeamMateDoubleRolled"), // action.kickTeamMate
	KICKOFF("kickoff"), // phase.kickoff
	KICKOFF_ANIMATION("kickoffAnimation"), // phase.kickoff
	KICKOFF_RESULT_ROLL("kickoffResultRoll"), // phase.kickoff
	KICKOFF_RETURN("kickoffReturn"), // phase.kickoff
	KICKOFF_SCATTER_ROLL("kickoffScatterRoll"), // phase.kickoff
	JUMP("leap"), // action.move
	MISSED_PASS("missedPass"), // action.pass
	MOVE("move"), // action.move
	MOVE_BALL_AND_CHAIN("moveBallAndChain"), // action.move
	MOVE_DODGE("moveDodge"), // action.move
	MULTI_BLOCK_FORK("multiBlockFork"), // action.multiBlock 2020
	MVP("mvp"), // game.end
	NEXT_STEP("nextStep"), //
	PASS("pass"), // action.pass
	PASS_BLOCK("passBlock"), // action.pass
	PENALTY_SHOOTOUT("penaltyShootout"), // game.end
	PETTY_CASH("pettyCash"), // game.start
	PICK_UP("pickUp"), // action.common
	PLAY_CARD("playCard"), // phase.inducement
	PLAYER_LOSS("playerLoss"), // game.end
	PUSHBACK("pushback"), // action.block
	REALLY_STUPID("reallyStupid"), // action.common
	RECEIVE_CHOICE("receiveChoice"), // game.start
	REFEREE("referee"), // action.foul
	REMOVE_BLITZ_STATE("removeBlitzState"), // action.blitz
	RESOLVE_PASS("resovlePass"),
	RIGHT_STUFF("rightStuff"), // action.ttm
	RIOTOUS_ROOKIES("riotousRookies"),
	SAFE_THROW("safeThrow"), // action.pass
	SELECT_BLITZ_TARGET("selectBlitzTarget"),
	SELECT_BLITZ_TARGET_END("selectBlitzTargetEnd"),
	SETUP("setup"), // phase.kickoff
	SET_DEFENDER("setDefender"), // action.MultiBlock 2020
	SHADOWING("shadowing"), // action.common
	SPECTATORS("spectators"), // game.start
	SPECIAL_EFFECT("specialEffect"), // phase.special
	STAB("stab"), // action.block
	STAND_UP("standUp"), // action.select
	SWARMING("swarming"), // phase.kickoff
	SWOOP("swoop"), // action.ttm
	TAKE_ROOT("takeRoot"), // action.common
	TENTACLES("tentacles"), // action.move
	TEST("test"), // test
	THROW_TEAM_MATE("throwTeamMate"), // action.ttm
	TOUCHBACK("touchback"), // phase.kickoff
	WEATHER("weather"), // game.start
	WILD_ANIMAL("wildAnimal"), // action.common
	WINNINGS("winnings"), // game.end
	WIZARD("wizard"), // phase.inducement
	WRESTLE("wrestle"); // action.block

	// obsolete = 57 (createGame)
	// maxId = 111

	private String fName;

	private StepId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
