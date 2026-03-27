package com.fumbbl.ffb.client.dialog;

import java.util.ArrayList;
import java.util.List;

public interface MultiReRollMnemonics {

	default List<Mnemonics> mnemonics() {
		return new ArrayList<Mnemonics>() {{
			add(new Mnemonics('T', 'N', 'B', 'H',
				new ArrayList<Character>() {{
					add('P');
					add('o');
					add('x');
				}},
				new ArrayList<Character>() {{
					add('C');
					add('u');
					add('m');
				}}, 'S', 'f', 'p', 'n',
				new ArrayList<Character>() {{
					add('h');
					add('i');
					add('j');
				}}));
			add(new Mnemonics('e', 'l', 'r', 'h',
				new ArrayList<Character>() {{
					add('r');
					add('y');
					add('z');
				}},
				new ArrayList<Character>() {{
					add('a');
					add('f');
					add('v');
				}}, 'b', 'q', 's', 'u',
				new ArrayList<Character>() {{
					add('k');
					add('l');
					add('w');
				}}));
		}};
	}

	class Mnemonics {
		private final char team, brawler, hatred, none, anyBlockDice, trrFallback, proFallback, proTrrFallback;
		private final List<Character> pro, anyDie, singleBlockDie;

		public Mnemonics(char team, char none, char brawler, char hatred, List<Character> pro, List<Character> anyDie,
			char anyBlockDice, char trrFallback, char proFallback, char proTrrFallback, List<Character> singleBlockDie) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.hatred = hatred;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
			this.trrFallback = trrFallback;
			this.proFallback = proFallback;
			this.proTrrFallback = proTrrFallback;
			this.singleBlockDie = singleBlockDie;
		}

		public char getTeam() {
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

		public char getTrrFallback() {
			return trrFallback;
		}

		public char getProFallback() {
			return proFallback;
		}

		public char getProTrrFallback() {
			return proTrrFallback;
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
