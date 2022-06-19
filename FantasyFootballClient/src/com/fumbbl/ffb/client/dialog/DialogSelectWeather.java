package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
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

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel textPanel = new JPanel();
		JLabel title = new JLabel("Choose new weather");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		textPanel.setAlignmentX(CENTER_ALIGNMENT);
		textPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		textPanel.add(title);

		panel.add(textPanel);
		panel.add(Box.createVerticalStrut(5));

		panel.setAlignmentX(CENTER_ALIGNMENT);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		weatherOptions.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
			Integer value = entry.getValue();
			String formattedValue = value >= 0 ? "+" + value : value.toString();
			JLabel label = new JLabel();
			label.setText(formattedValue);
			label.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			label.setHorizontalAlignment(SwingConstants.CENTER);

			JButton button = new JButton(Weather.valueOf(entry.getKey()).getName());
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

			JPanel entryPanel = new JPanel();
			entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
			entryPanel.add(label);
			entryPanel.add(Box.createHorizontalStrut(3));
			entryPanel.add(button);
			panel.add(entryPanel);

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
