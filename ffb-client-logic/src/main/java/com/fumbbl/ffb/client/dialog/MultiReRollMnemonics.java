package com.fumbbl.ffb.client.dialog;

import java.util.ArrayList;
import java.util.List;

public interface MultiReRollMnemonics {

	default List<Mnemonics> mnemonics() {
		return new ArrayList<Mnemonics>() {{
			add(new Mnemonics(new ArrayList<Character>() {{
				add('T');
				add('K');
				add('G');
			}}, 'N', 'B', 'H',
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
			add(new Mnemonics(new ArrayList<Character>() {{
				add('e');
				add('Y');
				add('Z');
			}}, 'l', 'r', 'h',
				new ArrayList<Character>() {{
					add('r');
					add('y');
					add('z');
				}},
				new ArrayList<Character>() {{
					add('a');
					add('f');
					add('v');
				}}, 'b',
				new ArrayList<Character>() {{
					add('k');
					add('l');
					add('w');
				}}));
		}};
	}

	class Mnemonics {
		private final char brawler, hatred, none, anyBlockDice;
		private final List<Character> team, pro, anyDie, singleBlockDie;

		public Mnemonics(List<Character> team, char none, char brawler, char hatred, List<Character> pro,
			List<Character> anyDie, char anyBlockDice, List<Character> singleBlockDie) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.hatred = hatred;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
			this.singleBlockDie = singleBlockDie;
		}

		/**
		 * @return one mnemonic for each team re-roll option, in the order the options are offered
		 */
		public List<Character> getTeam() {
			return team;
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
