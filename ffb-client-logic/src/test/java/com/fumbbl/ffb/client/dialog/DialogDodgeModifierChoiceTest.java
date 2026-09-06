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
import com.fumbbl.ffb.dialog.DialogDodgeModifierChoiceParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.DodgeModifierOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.BreakTackle;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogDodgeModifierChoiceTest {
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
		InducementType inducementType = mock(InducementType.class);
		when(client.getGame().getTurnData().getInducementSet().getInducementTypes()).thenReturn(Collections.singleton(
			inducementType));
		when(inducementType.hasUsage(Usage.CONDITIONAL_REROLL)).thenReturn(true);
		when(inducementType.getDescription()).thenReturn("Team Mascot");
		when(userInterface.getIconCache().getIconByProperty(any(), any())).thenReturn(new BufferedImage(30, 30, 1));

		List<ReRollProperty> properties = new ArrayList<>();
		properties.add(ReRollProperty.TRR);
		properties.add(ReRollProperty.PRO);

		Skill breakTackle = new BreakTackle();
		breakTackle.postConstruct();
		Skill consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();

		List<DodgeModifierOption> options = Arrays.asList(
			new DodgeModifierOption(Collections.singletonList(breakTackle), -1, 4),
			new DodgeModifierOption(Collections.singletonList(consummateProfessional), -1, 4),
			new DodgeModifierOption(Arrays.asList(breakTackle, consummateProfessional), -2, 3));

		DialogDodgeModifierChoiceParameter param =
			new DialogDodgeModifierChoiceParameter("playerID", ReRolledActions.DODGE, 5, 4, options, properties, false,
				null, null, null, Collections.singletonList("You rolled a 4, you needed a 5+."));

		JPanel panelContent = new JPanel();

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.add(panelContent, -1);
		desktopPane.setPreferredSize(panelContent.getPreferredSize());

		JFrame frame = new JFrame();
		frame.getContentPane().add(desktopPane, BorderLayout.CENTER);

		frame.setVisible(true);

		DialogDodgeModifierChoice comp = new DialogDodgeModifierChoice(client, param);
		frame.getContentPane().add(comp);
		comp.setVisible(true);

		frame.pack();
	}
}
