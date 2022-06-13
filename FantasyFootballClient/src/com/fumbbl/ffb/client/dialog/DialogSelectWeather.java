package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * @author Kalimar
 */
public class DialogSelectWeather extends Dialog {

	private int modifier;
	private String weatherName;

	public DialogSelectWeather(FantasyFootballClient pClient, Map<String, Integer> weatherOptions) {

		super(pClient, "Select Weather", false);

		JPanel panelText = new JPanel();
		panelText.setLayout(new BoxLayout(panelText, BoxLayout.X_AXIS));
		panelText.add(new JLabel("Choose new weather"));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

		panelButtons.add(Box.createHorizontalGlue());
		GameMechanic mechanic = (GameMechanic) getClient().getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		weatherOptions.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
			JButton button = new JButton(entry.getValue() + ": " + mechanic.weatherDescription(Weather.valueOf(entry.getKey())));
			panelButtons.add(button);
			button.addActionListener(e -> {
				modifier = entry.getValue();
				weatherName = entry.getKey();
				if (getCloseListener() != null) {
					getCloseListener().dialogClosed(this);
				}
			});
			button.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
					modifier = entry.getValue();
					weatherName = entry.getKey();
					if (getCloseListener() != null) {
						getCloseListener().dialogClosed(DialogSelectWeather.this);
					}
				}
			});
		});

		panelButtons.add(Box.createHorizontalGlue());

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(Box.createVerticalStrut(5));
		getContentPane().add(panelText);
		getContentPane().add(Box.createVerticalStrut(5));
		getContentPane().add(panelButtons);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.SELECT_WEATHER;
	}

	public int getModifier() {
		return modifier;
	}

	public String getWeatherName() {
		return weatherName;
	}
}
