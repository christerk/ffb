package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSources;

import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiReRollMnemonicsTest {

	@Test
	void teamSourceMnemonicsAreExplicitAndStableForEachTarget() {
		List<MultiReRollMnemonics.Mnemonics> mnemonics = new MultiReRollMnemonics() { }.mnemonics();

		assertEquals(KeyEvent.VK_T, mnemonics.get(0).forTeamReRollSource(ReRollSources.TEAM_RE_ROLL));
		assertEquals(KeyEvent.VK_L, mnemonics.get(0).forTeamReRollSource(ReRollSources.LEADER));
		assertEquals(KeyEvent.VK_M, mnemonics.get(0).forTeamReRollSource(ReRollSources.MASCOT));
		assertEquals(KeyEvent.VK_D, mnemonics.get(0).forTeamReRollSource(ReRollSources.DRIVE_BONUS_RE_ROLL));
		assertEquals(KeyEvent.VK_E, mnemonics.get(1).forTeamReRollSource(ReRollSources.TEAM_RE_ROLL));
		assertEquals(KeyEvent.VK_A, mnemonics.get(1).forTeamReRollSource(ReRollSources.LEADER));
		assertEquals(KeyEvent.VK_Q, mnemonics.get(1).forTeamReRollSource(ReRollSources.MASCOT));
		assertEquals(KeyEvent.VK_F, mnemonics.get(1).forTeamReRollSource(ReRollSources.DRIVE_BONUS_RE_ROLL));
	}

	@Test
	void skillAndActionMnemonicsUseKeyEventCodes() {
		List<MultiReRollMnemonics.Mnemonics> mnemonics = new MultiReRollMnemonics() { }.mnemonics();

		assertEquals(Arrays.asList(KeyEvent.VK_P, KeyEvent.VK_O, KeyEvent.VK_X), mnemonics.get(0).getPro());
		assertEquals(Arrays.asList(KeyEvent.VK_C, KeyEvent.VK_U, KeyEvent.VK_G), mnemonics.get(0).getAnyDie());
		assertEquals(KeyEvent.VK_S, mnemonics.get(0).getAnyBlockDice());
		assertEquals(KeyEvent.VK_N, mnemonics.get(0).getNone());
		assertEquals(KeyEvent.VK_B, mnemonics.get(0).getBrawler());
		assertEquals(KeyEvent.VK_H, mnemonics.get(0).getHatred());
		assertEquals(Arrays.asList(KeyEvent.VK_Y, KeyEvent.VK_I, KeyEvent.VK_J), mnemonics.get(0).getSingleBlockDie());
		assertEquals(KeyEvent.VK_4, mnemonics.get(1).getAnyBlockDice());
		assertEquals(Arrays.asList(KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3), mnemonics.get(1).getAnyDie());
		assertEquals(Arrays.asList(KeyEvent.VK_K, KeyEvent.VK_Z, KeyEvent.VK_V), mnemonics.get(1).getPro());
		assertEquals(KeyEvent.VK_0, mnemonics.get(1).getNone());
		assertEquals(KeyEvent.VK_W, mnemonics.get(1).getBrawler());
		assertEquals(KeyEvent.VK_R, mnemonics.get(1).getHatred());
		assertEquals(Arrays.asList(KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7), mnemonics.get(1).getSingleBlockDie());
	}
}
