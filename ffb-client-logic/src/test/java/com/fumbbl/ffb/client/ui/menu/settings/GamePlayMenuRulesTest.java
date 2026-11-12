package com.fumbbl.ffb.client.ui.menu.settings;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JMenu;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GamePlayMenuRulesTest {

	@Test
	void bb2025ShowsSkillSettingsWithoutSeparateBallAndChainMenu() {
		GamePlayMenu menu = menu(RulesCollection.Rules.BB2025);
		menu.init();

		assertTrue(hasItem(menu, "Skill settings"));
		assertFalse(hasItem(menu, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN.getValue()));
		assertEquals(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN.getValue(),
			menu.menuName(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN));
		assertEquals(CommonProperty.SETTING_DODGE_BLOCK.getValue(), menu.menuName(CommonProperty.SETTING_DODGE_BLOCK));
		assertTrue(menu.menuEntries(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)
			.containsKey(IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS));
		assertTrue(menu.menuEntries(CommonProperty.SETTING_DODGE_BLOCK)
			.containsKey(IClientPropertyValue.SETTING_DODGE_BLOCK_ALWAYS));
		assertEquals("Widezone & Chainpush",
			menu.menuEntries(CommonProperty.SETTING_DODGE_BLOCK)
				.get(IClientPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE));
		assertEquals("Sideline & Chainpush",
			menu.menuEntries(CommonProperty.SETTING_DODGE_BLOCK)
				.get(IClientPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL));
	}

	@Test
	void otherRulesetsShowAndSaveTheBallAndChainMenuOnly() {
		FantasyFootballClient client = client(RulesCollection.Rules.BB2020);
		GamePlayMenu menu = menu(client);
		menu.init();

		assertTrue(hasItem(menu, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN.getValue()));
		assertFalse(hasItem(menu, "Skill settings"));
		assertTrue(menu.menuEntries(CommonProperty.SETTING_DODGE_BLOCK).isEmpty());
		assertEquals(CommonProperty.SETTING_DODGE_BLOCK.getValue(), menu.menuName(CommonProperty.SETTING_DODGE_BLOCK));

		JMenu ballAndChainMenu = findMenu(menu, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN.getValue());
		javax.swing.JMenuItem neverItem = ballAndChainMenu.getItem(3);
		neverItem.doClick();

		verify(client).setProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN,
			IClientPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER);
		verify(client).saveUserSettings(false);
	}

	private GamePlayMenu menu(RulesCollection.Rules rules) {
		return menu(client(rules));
	}

	private GamePlayMenu menu(FantasyFootballClient client) {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		DimensionProvider dimensionProvider = new UiDimensionProvider(settings);
		return new GamePlayMenu(client, dimensionProvider, new StyleProvider(), settings);
	}

	private FantasyFootballClient client(RulesCollection.Rules rules) {
		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		Game game = mock(Game.class);
		GameOptions options = mock(GameOptions.class);
		when(client.getGame()).thenReturn(game);
		when(game.getOptions()).thenReturn(options);
		when(options.getRulesVersion()).thenReturn(rules);
		when(options.getOptionWithDefault(GameOptionId.ENABLE_TACKLEZONE_OVERLAYS))
			.thenReturn(mock(GameOptionBoolean.class));
		when(client.getMode()).thenReturn(ClientMode.PLAYER);
		return client;
	}

	private boolean hasItem(javax.swing.JMenu menu, String name) {
		return findMenuOrItem(menu, name) != null;
	}

	private Object findMenuOrItem(javax.swing.JMenu menu, String name) {
		for (int index = 0; index < menu.getItemCount(); index++) {
			javax.swing.JMenuItem item = menu.getItem(index);
			if (item != null && name.equals(item.getText())) {
				return item;
			}
			if (item instanceof javax.swing.JMenu) {
				Object found = findMenuOrItem((javax.swing.JMenu) item, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private JMenu findMenu(javax.swing.JMenu menu, String name) {
		for (int index = 0; index < menu.getItemCount(); index++) {
			javax.swing.JMenuItem item = menu.getItem(index);
			if (item instanceof JMenu && name.equals(item.getText())) {
				return (JMenu) item;
			}
		}
		return null;
	}
}
