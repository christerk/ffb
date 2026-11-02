package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;

import java.util.ArrayList;
import java.util.List;

public interface MultiReRollMnemonics {

	default List<Mnemonics> mnemonics() {
		return new ArrayList<Mnemonics>() {{
			add(new Mnemonics('T', 'L', 'K', 'G', 'N', 'B', 'H',
				new ArrayList<Character>() {{
					add('P');
					add('o');
					add('x');
				}},
				new ArrayList<Character>() {{
					add('C');
					add('u');
					add('m');
				}}, 'S',
				new ArrayList<Character>() {{
					add('h');
					add('i');
					add('j');
				}}));
			add(new Mnemonics('E', 'W', 'Q', 'F', '0', 'r', 'h',
				new ArrayList<Character>() {{
					add('r');
					add('y');
					add('z');
				}},
				new ArrayList<Character>() {{
					add('a');
					add('o');
					add('v');
				}}, 'b',
				new ArrayList<Character>() {{
					add('C');
					add('I');
					add('J');
				}}));
		}};
	}

	class Mnemonics {
		private final char teamReRoll, leader, mascot, driveBonus;
		private final char brawler, hatred, none, anyBlockDice;
		private final List<Character> pro, anyDie, singleBlockDie;

		public Mnemonics(char teamReRoll, char leader, char mascot, char driveBonus, char none, char brawler,
			char hatred, List<Character> pro, List<Character> anyDie, char anyBlockDice,
			List<Character> singleBlockDie) {
			this.teamReRoll = teamReRoll;
			this.leader = leader;
			this.mascot = mascot;
			this.driveBonus = driveBonus;
			this.none = none;
			this.brawler = brawler;
			this.hatred = hatred;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
			this.singleBlockDie = singleBlockDie;
		}

		public char getTeamReRoll() {
			return teamReRoll;
		}

		public char getLeader() {
			return leader;
		}

		public char getMascot() {
			return mascot;
		}

		public char getDriveBonus() {
			return driveBonus;
		}

		public char forTeamReRollSource(ReRollSource source) {
			if (source == ReRollSources.TEAM_RE_ROLL) {
				return getTeamReRoll();
			}
			if (source == ReRollSources.LEADER) {
				return getLeader();
			}
			if (source == ReRollSources.MASCOT) {
				return getMascot();
			}
			if (source == ReRollSources.DRIVE_BONUS_RE_ROLL) {
				return getDriveBonus();
			}
			throw new IllegalArgumentException("Unsupported team re-roll source: " + source);
		}

		public char getBrawler() {
			return brawler;
		}

		public char getHatred() {
			return hatred;
		}

		public char getNone() {
			return none;
		}

		public char getAnyBlockDice() {
			return anyBlockDice;
		}

		public List<Character> getPro() {
			return pro;
		}

		public List<Character> getAnyDie() {
			return anyDie;
		}

		public List<Character> getSingleBlockDie() {
			return singleBlockDie;
		}
	}
}
