package com.fumbbl.ffb.kickoff.bb2025;

import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.modifiers.TemporaryStatDecrementer;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;

import java.util.HashSet;

public enum KickoffResult implements com.fumbbl.ffb.kickoff.KickoffResult {

	GET_THE_REF("Get the Ref", "Each coach receives a free bribe."),
	TIME_OUT("Time-out", "Turn marker moves back by one if kicking team is on turn 6, 7 or 8 or forward otherwise."),
	SOLID_DEFENCE("Solid Defence", "The kicking team may setup D3+3 of its players again."),
	HIGH_KICK("High Kick", "A player on the receiving team may try to catch the ball directly."),
	CHEERING_FANS("Cheering Fans",
		"The team with the most enthusiastic fans gains an additonal offensive assist on their next block."),
	WEATHER_CHANGE("Weather Change", "The weather changes suddenly."),
	BRILLIANT_COACHING("Brilliant Coaching", "The team with the best coaching gains a re-roll.") {
		@Override
		public boolean isCoachReRoll() {
			return true;
		}
	},
	QUICK_SNAP("Quick Snap", "The offence may reposition D3+3 of their open players 1 square each."),
	CHARGE("Charge",
		"The kicking team can select D3+3 open players to perform Move, Blitz, TTM and KTM actions as it was a regular team turn."),
	DODGY_SNACK("Dodgy Snack", "A random player gets either -MA and -AV for the Drive or is sent to reserves.") {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withModifiers(new HashSet<TemporaryStatModifier>() {{
				add(new TemporaryStatDecrementer(PlayerStatKey.AV, mechanic));
				add(new TemporaryStatDecrementer(PlayerStatKey.MA, mechanic));
			}});
		}
	},
	PITCH_INVASION("Pitch Invasion", "Random players are being stunned by the crowd.");

	private final String fName;
	private final String fDescription;

	KickoffResult(String pName, String pDescription) {
		fName = pName;
		fDescription = pDescription;
	}

	public String getName() {
		return fName;
	}

	public String getDescription() {
		return fDescription;
	}

	public boolean isFanReRoll() {
		return false;
	}

	public boolean isCoachReRoll() {
		return false;
	}
}
