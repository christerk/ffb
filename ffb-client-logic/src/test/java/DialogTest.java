import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogReRollProperties;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogTest {
	public static void main(String[] args) {

		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		UserInterface userInterface = mock(UserInterface.class, RETURNS_DEEP_STUBS);
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

		List<ReRollProperty> properties = new ArrayList<>();
		properties.add(ReRollProperty.TRR);
		properties.add(ReRollProperty.MASCOT);
	//	properties.add(ReRollProperty.BRILLIANT_COACHING);

		DialogReRollPropertiesParameter param =
			new DialogReRollPropertiesParameter("playerID", ReRolledActions.RUSH, 2, properties, false, null, null, null,
				null, null);


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