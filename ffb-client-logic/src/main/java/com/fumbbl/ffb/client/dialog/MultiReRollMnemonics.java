package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public interface MultiReRollMnemonics {

	default List<Mnemonics> mnemonics() {
		return new ArrayList<Mnemonics>() {{
			add(new Mnemonics(KeyEvent.VK_T, KeyEvent.VK_L, KeyEvent.VK_M, KeyEvent.VK_D, KeyEvent.VK_N,
				KeyEvent.VK_B, KeyEvent.VK_H,
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_P);
					add(KeyEvent.VK_O);
					add(KeyEvent.VK_X);
				}},
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_C);
					add(KeyEvent.VK_U);
					add(KeyEvent.VK_G);
				}}, KeyEvent.VK_S,
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_Y);
					add(KeyEvent.VK_I);
					add(KeyEvent.VK_J);
				}}));
			add(new Mnemonics(KeyEvent.VK_E, KeyEvent.VK_A, KeyEvent.VK_Q, KeyEvent.VK_F, KeyEvent.VK_0,
				KeyEvent.VK_W, KeyEvent.VK_R,
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_K);
					add(KeyEvent.VK_Z);
					add(KeyEvent.VK_V);
				}},
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_1);
					add(KeyEvent.VK_2);
					add(KeyEvent.VK_3);
				}}, KeyEvent.VK_4,
				new ArrayList<Integer>() {{
					add(KeyEvent.VK_5);
					add(KeyEvent.VK_6);
					add(KeyEvent.VK_7);
				}}));
		}};
	}

	class Mnemonics {
		private final int teamReRoll, leader, mascot, driveBonus;
		private final int brawler, hatred, none, anyBlockDice;
		private final List<Integer> pro, anyDie, singleBlockDie;

		public Mnemonics(int teamReRoll, int leader, int mascot, int driveBonus, int none, int brawler,
			int hatred, List<Integer> pro, List<Integer> anyDie, int anyBlockDice,
			List<Integer> singleBlockDie) {
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

		public int getTeamReRoll() {
			return teamReRoll;
		}

		public int getLeader() {
			return leader;
		}

		public int getMascot() {
			return mascot;
		}

		public int getDriveBonus() {
			return driveBonus;
		}

		public int forTeamReRollSource(ReRollSource source) {
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

		public int getBrawler() {
			return brawler;
		}

		public int getHatred() {
			return hatred;
		}

		public int getNone() {
			return none;
		}

		public int getAnyBlockDice() {
			return anyBlockDice;
		}

		public List<Integer> getPro() {
			return pro;
		}

		public List<Integer> getAnyDie() {
			return anyDie;
		}

		public List<Integer> getSingleBlockDie() {
			return singleBlockDie;
		}
	}
}
