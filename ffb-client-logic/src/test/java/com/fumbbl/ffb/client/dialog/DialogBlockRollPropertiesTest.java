package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.menu.GameMenuBar;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogBlockRollPropertiesTest {
	public static void main(String[] args)
		throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		String teamId = "teamId";

		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		UserInterface userInterface = mock(UserInterface.class, RETURNS_DEEP_STUBS);
		GameMenuBar menuBar = mock(GameMenuBar.class);

		Map<String, String> entries = new HashMap<String, String>() {{
			put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS, "Value1");
		}};
		when(menuBar.menuEntries(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)).thenReturn(entries);
		when(userInterface.getGameMenuBar()).thenReturn(menuBar);
		when(client.getGame().getTeamHome().getId()).thenReturn(teamId);

		when(client.getUserInterface()).thenReturn(userInterface);
		when(userInterface.getPitchDimensionProvider()).thenReturn(new PitchDimensionProvider(new LayoutSettings(
			ClientLayout.LANDSCAPE, 1.0)));
		when(client.getGame().getRules().getSkillFactory()).thenReturn(mock(SkillFactory.class));
		InducementType inducementType = mock(
			InducementType.class);
		when(client.getGame().getActingTurnData().getInducementSet().getInducementTypes()).thenReturn(Collections.singleton(
			inducementType));
		when(inducementType.hasUsage(Usage.CONDITIONAL_REROLL)).thenReturn(true);
		when(inducementType.getDescription()).thenReturn("Team Mascot");
		when(userInterface.getIconCache().getIconByProperty(any(), any())).thenReturn(new BufferedImage(30, 30, 1));
		when(userInterface.getIconCache().getDiceIcon(anyInt(), any())).thenReturn(new BufferedImage(35, 35, 1));

		List<ReRollProperty> properties = new ArrayList<>();
		properties.add(ReRollProperty.TRR);
		properties.add(ReRollProperty.MASCOT);
		properties.add(ReRollProperty.PRO);
	//	properties.add(ReRollProperty.BRAWLER);
	//	properties.add(ReRollProperty.ANY_DIE_RE_ROLL);
		//properties.add(ReRollProperty.BRILLIANT_COACHING);

		Map<ReRolledAction, ReRollSource> actionMap = new HashMap<>();
		actionMap.put(ReRolledActions.SINGLE_DIE, ReRollSources.THINKING_MANS_TROLL);
	//	actionMap.put(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.UNSTOPPABLE_MOMENTUM);
	//	actionMap.put(ReRolledActions.MULTI_BLOCK_DICE, ReRollSources.SAVAGE_BLOW);

//		DialogBlockRollPropertiesParameter param = new DialogBlockRollPropertiesParameter(teamId, 3, new int[]{1, 2, 3}, properties, Collections.emptyMap());
		DialogBlockRollPropertiesParameter param =
			new DialogBlockRollPropertiesParameter(teamId, 1, new int[]{1}, properties, Collections.emptyMap());

		JPanel panelContent = new JPanel();

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.add(panelContent, -1);
		desktopPane.setPreferredSize(panelContent.getPreferredSize());

		JFrame frame = new JFrame();
		frame.getContentPane().add(desktopPane, BorderLayout.CENTER);

		frame.setVisible(true);


		DialogBlockRollProperties comp = new DialogBlockRollProperties(client, param, actionMap);
		frame.getContentPane().add(comp);
		comp.setVisible(true);


		frame.pack();
	}
}