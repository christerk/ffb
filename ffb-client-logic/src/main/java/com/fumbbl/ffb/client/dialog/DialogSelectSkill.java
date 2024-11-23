package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DialogSelectSkill extends Dialog implements ActionListener {

	private final SkillCheckList fList;
	private final JButton fButtonSelect;
	private final JButton fButtonCancel;
	private final int minSelects;
	private Skill[] selectedSkills;

	public DialogSelectSkill(FantasyFootballClient client, String header, List<Skill> skills,
	                         int minSelects, int maxSelects, boolean preSelected) {

		super(client, "Skill Choice", false);
		this.minSelects = minSelects;

		fButtonSelect = new JButton(dimensionProvider(), "Select", RenderContext.ON_PITCH);
		fButtonSelect.setToolTipText("Select the checked skill(s)");
		fButtonSelect.addActionListener(this);
		fButtonSelect.setMnemonic((int) 'S');
		fButtonSelect.setEnabled((skills.size() == 1) || preSelected);

		fButtonCancel = new JButton(dimensionProvider(), "Cancel", RenderContext.ON_PITCH);
		fButtonCancel.setToolTipText("Do not select any skill");
		fButtonCancel.addActionListener(this);
		fButtonCancel.setMnemonic((int) 'C');

		fList = new SkillCheckList(dimensionProvider(), skills, minSelects, maxSelects, preSelected, fButtonSelect);
		fList.setVisibleRowCount(Math.min(skills.size(), 5));

		JScrollPane listScroller = new JScrollPane(fList);
		// listScroller.setPreferredSize(new Dimension(200, 100));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		JLabel headerLabel = new JLabel(dimensionProvider(), header, RenderContext.ON_PITCH);
		headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, headerLabel.getFont().getSize()));
		headerPanel.add(headerLabel);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.add(listScroller);
		listPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButtonSelect);
		if (minSelects == 0) {
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(fButtonCancel);
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
		centerPane.add(headerPanel);
		centerPane.add(listPanel);
		centerPane.add(buttonPanel);

		getContentPane().add(centerPane, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

		addMouseListener(this);

		fList.setSelectedIndex(0);
	}

	public DialogId getId() {
		return DialogId.PLAYER_CHOICE;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonCancel) {
			selectedSkills = new Skill[0];
			closeDialog();
		}
		if (pActionEvent.getSource() == fButtonSelect) {
			selectedSkills = fList.getSelectedSkills();
			if (selectedSkills.length >= minSelects) {
				closeDialog();
			}
		}
	}

	private void closeDialog() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public Skill[] getSelectedSkills() {
		return selectedSkills;
	}

}
