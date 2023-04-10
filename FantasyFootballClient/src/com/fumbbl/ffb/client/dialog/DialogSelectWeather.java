package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kalimar
 */
public class DialogSelectWeather extends Dialog {

	private int modifier;
	private String weatherName;

	public DialogSelectWeather(FantasyFootballClient pClient, Map<String, Integer> weatherOptions) {

		super(pClient, "Select Weather", false);

		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		JLabel title = new JLabel(dimensionProvider(), "<html><b>Select roll modifier</b></html>");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 10, 0);

		panel.add(title, constraints);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		AtomicInteger row = new AtomicInteger(1);
		constraints.gridwidth = 1;
		Insets buttonInsets = new Insets(0, 0, 0, 5);


		weatherOptions.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
			Integer value = entry.getValue();
			String formattedValue = value >= 0 ? "+" + value : value.toString();
			JLabel label = new JLabel(dimensionProvider());
			label.setText(Weather.valueOf(entry.getKey()).getName());
			label.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			label.setHorizontalAlignment(SwingConstants.CENTER);

			JButton button = new JButton(dimensionProvider(), formattedValue);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

			constraints.gridx = 0;
			constraints.gridy = row.getAndIncrement();
			constraints.insets = buttonInsets;
			panel.add(button, constraints);
			constraints.gridx = 1;
			constraints.insets = new Insets(0, 0, 0, 0);
			panel.add(label, constraints);

			button.addActionListener(e -> {
				modifier = value;
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
					modifier = value;
					weatherName = entry.getKey();
					if (getCloseListener() != null) {
						getCloseListener().dialogClosed(DialogSelectWeather.this);
					}
				}
			});
		});

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(panel);
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
