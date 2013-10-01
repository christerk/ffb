package com.balancedbytes.games.ffb.server.step;

/**
 * 
 * @author Kalimar
 */
public enum StepId {
	
	ALWAYS_HUNGRY(91),           // action.ttm
	ANIMOSITY(47),               // action.pass
	APOTHECARY(20),              // action.common
	APPLY_KICKOFF_RESULT(68),    // phase.kickoff
	BLITZ_TURN(75),              // phase.kickoff
	BLOCK_BALL_AND_CHAIN(104),   // action.block
	BLOCK_CHAINSAW(5),           // action.block
	BLOCK_CHOICE(1),             // action.block
	BLOCK_DODGE(7),              // action.block
	BLOCK_ROLL(2),               // action.block
	BLOCK_STATISTICS(3),         // action.block
	BLOOD_LUST(26),              // action.common
	BOMBARDIER(105),             // action.pass
	BONE_HEAD(102),              // action.common
	BOTH_DOWN(4),                // action.block
	BRIBES(86),                  // action.foul
	BUY_CARDS(108),              // game.start
	BUY_INDUCEMENTS(60),         // game.start
	CATCH_SCATTER_THROW_IN(22),  // action.common
	COIN_CHOICE(62),             // game.start
	DAUNTLESS(6),                // action.block
	DISPATCH_PASSING(48),        // action.pass
	DIVING_TACKLE(33),           // action.move
	DROP_DIVING_TACKLER(35),     // action.move
	DROP_FALLING_PLAYERS(8),     // action.block
	DUMP_OFF(9),                 // action.block
	EAT_TEAM_MATE(92),           // action.ttm
	EJECT_PLAYER(87),            // action.foul
	END_BLOCKING(18),            // action.block
	END_BOMB(107),               // phase.special
	END_FEEDING(99),             // action.end
	END_FOULING(82),             // action.foul
	END_GAME(45),                // game.end
	END_INDUCEMENT(78),          // phase.inducement
	END_KICKOFF(80),             // phase.kickoff
	END_MOVING(38),              // action.move
	END_PASSING(55),             // action.pass
	END_SCATTER_PLAYER(96), 		 // action.ttm
	END_SELECTING(74),           // action.select
	END_THROW_TEAM_MATE(89),     // action.ttm
	END_TURN(24),                // action.common
	FALL_DOWN(29),               // action.common
	FAN_FACTOR(43),              // game.end
	FOLLOWUP(10),                // action.block
	FOUL(84),                    // action.foul
	FOUL_APPEARANCE(11),         // action.block
	FOUL_CHAINSAW(83),           // action.foul
	FUMBLE_TTM_PASS(97),         // action.ttm
	GO_FOR_IT(12),               // action.common
	GOTO_LABEL(28),              //
	HAIL_MARY_PASS(98),          // action.pass
	HAND_OVER(53),               // action.pass
	HORNS(13),                   // action.block
	HYPNOTIC_GAZE(30),           // action.move
	INIT_BLOCKING(19),           // action.block
	INIT_BOMB(106),              // phase.special
	INIT_CARD(109),              // phase.inducement
	INIT_END_GAME(39),           // game.end
	INIT_FEEDING(25),            // action.end
	INIT_FOULING(81),            // action.foul
	INIT_INDUCEMENT(79),         // phase.inducement
	INIT_KICKOFF(64),            // phase.kickoff
	INIT_MOVING(31),             // action.move
	INIT_PASSING(46),            // action.pass
	INIT_SCATTER_PLAYER(93), 		 // action.ttm
	INIT_SELECTING(71),          // action.select
	INIT_START_GAME(56),         // game.start
	INIT_THROW_TEAM_MATE(88),    // action.ttm
	INTERCEPT(49),               // action.pass
	JUGGERNAUT(14),              // action.block
	JUMP_UP(72),                 // action.select
	KICKOFF_ANIMATION(69),       // phase.kickoff
	KICKOFF_RESULT_ROLL(67),     // phase.kickoff
	KICKOFF_RETURN(66),          // phase.kickoff
	KICKOFF_SCATTER_ROLL(65),    // phase.kickoff
	LEAP(32),                    // action.move
	MISSED_PASS(52),             // action.pass
	MOVE(36),                    // action.move
	MOVE_BALL_AND_CHAIN(103),    // action.move
	MOVE_DODGE(34),              // action.move
	MVP(41),                     // game.end
	NEXT_STEP(101),              //
	PASS(51),                    // action.pass
	PASS_BLOCK(100),             // action.pass
	PENALTY_SHOOTOUT(40),        // game.end
	PETTY_CASH(59),              // game.start
	PICK_UP(21),                 // action.common
	PLAYER_LOSS(44),             // game.end
	PUSHBACK(15),                // action.block
	REALLY_STUPID(58),           // action.common
	RECEIVE_CHOICE(63),          // game.start
	REFEREE(85),                 // action.foul
	RIGHT_STUFF(94),             // action.ttm
	SAFE_THROW(50),              // action.pass
	SHADOWING(23),               // action.common
	SPECTATORS(90),              // game.start
	SPECIAL_EFFECT(77),          // phase.special
	STAB(16),                    // action.block
	STAND_UP(73),                // action.select
	TAKE_ROOT(27),               // action.common
	TENTACLES(37),               // action.move
	TEST(110),                   // test
	THROW_TEAM_MATE(95),         // action.ttm
	TOUCHBACK(70),               // phase.kickoff
	WEATHER(61),                 // game.start
	WILD_ANIMAL(54),             // action.common
	WINNINGS(42),                // game.end
	WIZARD(76),                  // phase.inducement
	WRESTLE(17);                 // action.block

	// obsolete = 57 (createGame)
	// maxId = 110

	private int fId;
	
	private StepId(int pId) {
		fId = pId;
	}
	
	public int getId() {
		return fId;
	}
	
	public static StepId fromId(int pId) {
		for (StepId stepLabel : values()) {
			if (stepLabel.getId() == pId) {
				return stepLabel;
			}
		}
		return null;
	}

}
