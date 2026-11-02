package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.BlockRollProperties;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.skill.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferedReRollSourceServiceTest {

	private final OfferedReRollSourceService service = new OfferedReRollSourceService();
	private Game game;
	private Skill brawler;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		GameRules rules = mock(GameRules.class);
		SkillFactory skillFactory = mock(SkillFactory.class);
		brawler = mock(Skill.class);
		when(brawler.getName()).thenReturn("Brawler");
		when(game.getRules()).thenReturn(rules);
		when(rules.getSkillFactory()).thenReturn(skillFactory);
		when(skillFactory.forName("Brawler")).thenReturn(brawler);
	}

	private DialogReRollPropertiesParameter reRollDialog(List<ReRollProperty> properties, Skill reRollSkill) {
		return new DialogReRollPropertiesParameter("playerId", ReRolledActions.DODGE, 3, properties, false, reRollSkill, null,
			null, null, Collections.emptyList());
	}

	private void showDialog(IDialogParameter dialogParameter) {
		when(game.getDialogParameter()).thenReturn(dialogParameter);
	}

	@Test
	void nothingIsOfferedWithoutReRollDialog() {
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, null, null));
		showDialog(new DialogSkillUseParameter("playerId", brawler, 0));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.TEAM_RE_ROLL, null));
	}

	@Test
	void propertiesDialogOffersMatchingActionOnly() {
		showDialog(reRollDialog(Collections.singletonList(ReRollProperty.TRR), null));

		assertTrue(service.isOffered(game, ReRolledActions.DODGE, null, null));
		assertTrue(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.TEAM_RE_ROLL, null));
		assertFalse(service.isOffered(game, ReRolledActions.GO_FOR_IT, ReRollSources.TEAM_RE_ROLL, null));
		assertFalse(service.isOffered(game, ReRolledActions.GO_FOR_IT, null, null));
	}

	@Test
	void propertiesDialogChecksEachPartOfComposedSources() {
		showDialog(reRollDialog(Arrays.asList(ReRollProperty.TRR, ReRollProperty.MASCOT), null));

		assertTrue(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.MASCOT_TRR, null));
		assertTrue(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.MASCOT, null));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.PRO_TRR, null));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.DRIVE_BONUS_RE_ROLL, null));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.BRAWLER, null));
	}

	@Test
	void driveBonusNeedsItsOwnProperty() {
		showDialog(reRollDialog(Arrays.asList(ReRollProperty.TRR, ReRollProperty.DRIVE_BONUS), null));

		assertTrue(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.DRIVE_BONUS_RE_ROLL, null));
	}

	@Test
	void propertiesDialogOffersItsReRollSkill() {
		showDialog(reRollDialog(Collections.emptyList(), brawler));

		assertTrue(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.BRAWLER, null));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.TEAM_RE_ROLL, null));
	}

	@Test
	void blockDialogOffersMappedSourcesAndProByName() {
		Map<String, String> actionToSource = new HashMap<>();
		actionToSource.put("singleBothDown", "Brawler");
		actionToSource.put("singleDiePerActivation", "Pro");
		showDialog(new DialogBlockRollPropertiesParameter("teamId", 2, new int[]{1, 2},
			Collections.emptyList(), actionToSource));

		assertTrue(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.BRAWLER, null));
		assertTrue(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.PRO, null));
		assertFalse(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.PRO_TRR, null));
		assertFalse(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.HATRED, null));
		assertFalse(service.isOffered(game, ReRolledActions.DODGE, ReRollSources.BRAWLER, null));
	}

	@Test
	void blockForTargetsDialogChecksTheTargetsRoll() {
		BlockRollProperties withReRoll = new BlockRollProperties("target1", new PlayerState(PlayerState.STANDING), 0);
		withReRoll.add(ReRollProperty.TRR);
		BlockRollProperties withoutReRoll = new BlockRollProperties("target2", new PlayerState(PlayerState.STANDING), 1);
		showDialog(new DialogReRollBlockForTargetsPropertiesParameter("playerId", Arrays.asList(withReRoll, withoutReRoll)));

		assertTrue(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.TEAM_RE_ROLL, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.TEAM_RE_ROLL, "target2"));
		assertFalse(service.isOffered(game, ReRolledActions.BLOCK, ReRollSources.TEAM_RE_ROLL, "unknown"));
		assertTrue(service.isOffered(game, ReRolledActions.BLOCK, null, null));
		assertFalse(service.isOffered(game, ReRolledActions.DAUNTLESS, null, null));
	}

	@Test
	void regenerationDialogChecksThePlayersOptions() {
		showDialog(new DialogReRollRegenerationMultipleParameter(Arrays.asList("player1", "player2"), Arrays.asList(
			new ReRollOptions(Collections.singletonList(ReRollProperty.TRR), null),
			new ReRollOptions(Collections.emptyList(), null))));

		assertTrue(service.isOffered(game, ReRolledActions.REGENERATION, ReRollSources.TEAM_RE_ROLL, "player1"));
		assertFalse(service.isOffered(game, ReRolledActions.REGENERATION, ReRollSources.TEAM_RE_ROLL, "player2"));
		assertFalse(service.isOffered(game, ReRolledActions.REGENERATION, ReRollSources.TEAM_RE_ROLL, "unknown"));
		assertTrue(service.isOffered(game, ReRolledActions.REGENERATION, null, null));
		assertFalse(service.isOffered(game, ReRolledActions.BLOCK, null, null));
	}

	@Test
	void forTargetsDialogChecksTargetAndOfferedSources() {
		showDialog(new DialogReRollForTargetsParameter("playerId", Arrays.asList("target1", "target2"),
			ReRolledActions.DAUNTLESS, new HashMap<>(), Collections.singletonList("target1"), false, true, brawler, null,
			false));

		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.TEAM_RE_ROLL, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.BRAWLER, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.TEAM_RE_ROLL, "target2"));
		assertFalse(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.PRO, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.LORD_OF_CHAOS, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.FOUL_APPEARANCE, ReRollSources.TEAM_RE_ROLL, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, null, null));
	}

	@Test
	void forTargetsDialogOffersSingleUseSourceAndProWhenAvailable() {
		showDialog(new DialogReRollForTargetsParameter("playerId", Collections.singletonList("target1"),
			ReRolledActions.FOUL_APPEARANCE, new HashMap<>(), Collections.singletonList("target1"), true, false, null,
			ReRollSources.LORD_OF_CHAOS, false));

		assertTrue(service.isOffered(game, ReRolledActions.FOUL_APPEARANCE, ReRollSources.PRO, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.FOUL_APPEARANCE, ReRollSources.LORD_OF_CHAOS, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.FOUL_APPEARANCE, ReRollSources.TEAM_RE_ROLL, "target1"));
	}

	@Test
	void explicitForTargetsDialogOffersComposedSourcesOnlyForAvailableTargets() {
		showDialog(new DialogReRollForTargetsParameter("playerId", Arrays.asList("target1", "target2"),
			ReRolledActions.DAUNTLESS, new HashMap<>(), Collections.singletonList("target1"), true, true, brawler,
			ReRollSources.LORD_OF_CHAOS, false,
			Arrays.asList(ReRollProperty.MASCOT, ReRollProperty.TRR, ReRollProperty.PRO)));

		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.MASCOT, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.PRO_TRR, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.PRO_MASCOT_TRR, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.BRAWLER, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.LORD_OF_CHAOS, "target1"));
		assertFalse(service.isOffered(game, ReRolledActions.DAUNTLESS, ReRollSources.PRO_TRR, "target2"));
		assertFalse(service.isOffered(game, ReRolledActions.FOUL_APPEARANCE, ReRollSources.PRO, "target1"));
		assertTrue(service.isOffered(game, ReRolledActions.DAUNTLESS, null, null));
	}
}
