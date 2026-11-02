package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSources;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiReRollMnemonicsTest {

	@Test
	void teamSourceMnemonicsAreExplicitAndStableForEachTarget() {
		List<MultiReRollMnemonics.Mnemonics> mnemonics = new MultiReRollMnemonics() { }.mnemonics();

		assertEquals('T', mnemonics.get(0).forTeamReRollSource(ReRollSources.TEAM_RE_ROLL));
		assertEquals('L', mnemonics.get(0).forTeamReRollSource(ReRollSources.LEADER));
		assertEquals('M', mnemonics.get(0).forTeamReRollSource(ReRollSources.MASCOT));
		assertEquals('D', mnemonics.get(0).forTeamReRollSource(ReRollSources.DRIVE_BONUS_RE_ROLL));
		assertEquals('E', mnemonics.get(1).forTeamReRollSource(ReRollSources.TEAM_RE_ROLL));
		assertEquals('A', mnemonics.get(1).forTeamReRollSource(ReRollSources.LEADER));
		assertEquals('Q', mnemonics.get(1).forTeamReRollSource(ReRollSources.MASCOT));
		assertEquals('F', mnemonics.get(1).forTeamReRollSource(ReRollSources.DRIVE_BONUS_RE_ROLL));
	}
}
