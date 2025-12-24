package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.menu.GameMenuBar;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogReRollPropertiesTest {
	public static void main(String[] args)
		throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		UserInterface userInterface = mock(UserInterface.class, RETURNS_DEEP_STUBS);
		GameMenuBar menuBar = mock(GameMenuBar.class);
		Map<String, String> entries = new HashMap<String, String>() {{
			put(CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS, "Value1");
		}};
		when(menuBar.menuEntries(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)).thenReturn(entries);
		when(userInterface.getGameMenuBar()).thenReturn(menuBar);


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

		List<ReRollProperty> properties = new ArrayList<>();
		properties.add(ReRollProperty.TRR);
		properties.add(ReRollProperty.MASCOT);
		properties.add(ReRollProperty.PRO);
		properties.add(ReRollProperty.LONER);
//		properties.add(ReRollProperty.BRILLIANT_COACHING);

		DialogReRollPropertiesParameter param =
		/*	new DialogReRollPropertiesParameter("playerID", ReRolledActions.RUSH, 2, properties, false, new ThinkingMansTroll(), new StrongPassingGame(),
				CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, CommonPropertyValue.SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS, Arrays.asList("You need a:", "  • 6 to knock your opponent down",
				"  • " + 45 + "+ to place your opponent prone", "  • " + 67 + "+ to avoid a turnover"));*/
		new DialogReRollPropertiesParameter("playerID", ReRolledActions.RUSH, 2, properties, false, null, null,
			null, null,null);


		JPanel panelContent = new JPanel();

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.add(panelContent, -1);
		desktopPane.setPreferredSize(panelContent.getPreferredSize());

		JFrame frame = new JFrame();
		frame.getContentPane().add(desktopPane, BorderLayout.CENTER);

		frame.setVisible(true);


		DialogReRollProperties comp = new DialogReRollProperties(client, param);
		frame.getContentPane().add(comp);
		comp.setVisible(true);


		frame.pack();
	}
}