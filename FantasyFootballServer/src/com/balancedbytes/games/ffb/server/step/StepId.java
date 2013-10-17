package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum StepId implements IEnumWithId, IEnumWithName {
	
	ALWAYS_HUNGRY(91, "alwaysHungry"),                  // action.ttm
	ANIMOSITY(47, "animosity"),                         // action.pass
	APOTHECARY(20, "apothecary"),                       // action.common
	APPLY_KICKOFF_RESULT(68, "applyKickoffResult"),     // phase.kickoff
	BLITZ_TURN(75, "blitzTurn"),                        // phase.kickoff
	BLOCK_BALL_AND_CHAIN(104, "blockBallAndChain"),     // action.block
	BLOCK_CHAINSAW(5, "blockChainsaw"),                 // action.block
	BLOCK_CHOICE(1, "blockChoice"),                     // action.block
	BLOCK_DODGE(7, "blockDodge"),                       // action.block
	BLOCK_ROLL(2, "blockRoll"),                         // action.block
	BLOCK_STATISTICS(3, "blockStatistics"),             // action.block
	BLOOD_LUST(26, "bloodLust"),                        // action.common
	BOMBARDIER(105, "bombardier"),                      // action.pass
	BONE_HEAD(102, "boneHead"),                         // action.common
	BOTH_DOWN(4, "bothDown"),                           // action.block
	BRIBES(86, "bribes"),                               // action.foul
	BUY_CARDS(108, "buyCards"),                         // game.start
	BUY_INDUCEMENTS(60, "buyInducements"),              // game.start
	CATCH_SCATTER_THROW_IN(22, "catchScatterThrowIn"),  // action.common
	COIN_CHOICE(62, "coinChoice"),                      // game.start
	DAUNTLESS(6, "dauntless"),                          // action.block
	DISPATCH_PASSING(48, "dispatchPassing"),            // action.pass
	DIVING_TACKLE(33, "divingTackle"),                  // action.move
	DROP_DIVING_TACKLER(35, "dropDivingTackler"),       // action.move
	DROP_FALLING_PLAYERS(8, "dropFallingPlayers"),      // action.block
	DUMP_OFF(9, "dumpOff"),                             // action.block
	EAT_TEAM_MATE(92, "eatTeamMate"),                   // action.ttm
	EJECT_PLAYER(87, "ejectPlayer"),                    // action.foul
	END_BLOCKING(18, "endBlocking"),                    // action.block
	END_BOMB(107, "endBomb"),                           // phase.special
	END_FEEDING(99, "endFeeding"),                      // action.end
	END_FOULING(82, "endFouling"),                      // action.foul
	END_GAME(45, "endGame"),                            // game.end
	END_INDUCEMENT(78, "endInducement"),                // phase.inducement
	END_KICKOFF(80, "endKickoff"),                      // phase.kickoff
	END_MOVING(38, "endMoving"),                        // action.move
	END_PASSING(55, "endPassing"),                      // action.pass
	END_SCATTER_PLAYER(96, "endScatterPlayer"), 		    // action.ttm
	END_SELECTING(74, "endSelecting"),                  // action.select
	END_THROW_TEAM_MATE(89, "endThrowTeamMate"),        // action.ttm
	END_TURN(24, "endTurn"),                            // action.common
	FALL_DOWN(29, "fallDown"),                          // action.common
	FAN_FACTOR(43, "fanFactor"),                        // game.end
	FOLLOWUP(10, "followup"),                           // action.block
	FOUL(84, "foul"),                                   // action.foul
	FOUL_APPEARANCE(11, "foulAppearance"),              // action.block
	FOUL_CHAINSAW(83, "foulChainsaw"),                  // action.foul
	FUMBLE_TTM_PASS(97, "fumbleTtmPass"),               // action.ttm
	GO_FOR_IT(12, "goForIt"),                           // action.common
	GOTO_LABEL(28, "gotoLabel"),                        //
	HAIL_MARY_PASS(98, "hailMaryPass"),                 // action.pass
	HAND_OVER(53, "handOver"),                          // action.pass
	HORNS(13, "horns"),                                 // action.block
	HYPNOTIC_GAZE(30, "hypnoticGaze"),                  // action.move
	INIT_BLOCKING(19, "initBlocking"),                  // action.block
	INIT_BOMB(106, "initBomb"),                         // phase.special
	INIT_CARD(109, "initCard"),                         // phase.inducement
	INIT_END_GAME(39, "initEndGame"),                   // game.end
	INIT_FEEDING(25, "initFeeding"),                    // action.end
	INIT_FOULING(81, "initFouling"),                    // action.foul
	INIT_INDUCEMENT(79, "initInducement"),              // phase.inducement
	INIT_KICKOFF(64, "initKickoff"),                    // phase.kickoff
	INIT_MOVING(31, "initMoving"),                      // action.move
	INIT_PASSING(46, "initPassing"),                    // action.pass
	INIT_SCATTER_PLAYER(93, "initScatterPlayer"), 		  // action.ttm
	INIT_SELECTING(71, "initSelecting"),                // action.select
	INIT_START_GAME(56, "initStartGame"),               // game.start
	INIT_THROW_TEAM_MATE(88, "initThrowTeamMate"),      // action.ttm
	INTERCEPT(49, "intercept"),                         // action.pass
	JUGGERNAUT(14, "juggernaut"),                       // action.block
	JUMP_UP(72, "jumpUp"),                              // action.select
	KICKOFF_ANIMATION(69, "kickoffAnimation"),          // phase.kickoff
	KICKOFF_RESULT_ROLL(67, "kickoffResultRoll"),       // phase.kickoff
	KICKOFF_RETURN(66, "kickoffReturn"),                // phase.kickoff
	KICKOFF_SCATTER_ROLL(65, "kickoffScatterRoll"),     // phase.kickoff
	LEAP(32, "leap"),                                   // action.move
	MISSED_PASS(52, "missedPass"),                      // action.pass
	MOVE(36, "move"),                                   // action.move
	MOVE_BALL_AND_CHAIN(103, "moveBallAndChain"),       // action.move
	MOVE_DODGE(34, "moveDodge"),                        // action.move
	MVP(41, "mvp"),                                     // game.end
	NEXT_STEP(101, "nextStep"),                         //
	PASS(51, "pass"),                                   // action.pass
	PASS_BLOCK(100, "passBlock"),                       // action.pass
	PENALTY_SHOOTOUT(40, "penaltyShootout"),            // game.end
	PETTY_CASH(59, "pettyCash"),                        // game.start
	PICK_UP(21, "pickUp"),                              // action.common
	PLAYER_LOSS(44, "playerLoss"),                      // game.end
	PUSHBACK(15, "pushback"),                           // action.block
	REALLY_STUPID(58, "reallyStupid"),                  // action.common
	RECEIVE_CHOICE(63, "receiveChoice"),                // game.start
	REFEREE(85, "referee"),                             // action.foul
	RIGHT_STUFF(94, "rightStuff"),                      // action.ttm
	SAFE_THROW(50, "safeThrow"),                        // action.pass
	SHADOWING(23, "shadowing"),                         // action.common
	SPECTATORS(90, "spectators"),                       // game.start
	SPECIAL_EFFECT(77, "specialEffect"),                // phase.special
	STAB(16, "stab"),                                   // action.block
	STAND_UP(73, "standUp"),                            // action.select
	TAKE_ROOT(27, "takeRoot"),                          // action.common
	TENTACLES(37, "tentacles"),                         // action.move
	TEST(110, "test"),                                  // test
	THROW_TEAM_MATE(95, "throwTeamMate"),               // action.ttm
	TOUCHBACK(70, "touchback"),                         // phase.kickoff
	WEATHER(61, "weather"),                             // game.start
	WILD_ANIMAL(54, "wildAnimal"),                      // action.common
	WINNINGS(42, "winnings"),                           // game.end
	WIZARD(76, "wizard"),                               // phase.inducement
	WRESTLE(17, "wrestle");                             // action.block

	// obsolete = 57 (createGame)
	// maxId = 110

	private int fId;
	private String fName;
	
	private StepId(int pId, String pName) {
		fId = pId;
		fName = pName;
	}
	
	public int getId() {
		return fId;
	}
	
	public String getName() {
	  return fName;
	}

}
