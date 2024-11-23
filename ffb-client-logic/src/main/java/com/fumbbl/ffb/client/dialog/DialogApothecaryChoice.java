package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Kalimar
 */
public class DialogApothecaryChoice extends Dialog implements ActionListener {

	private final JButton fButtonInjuryOld;
	private final JButton fButtonInjuryNew;

	private boolean fChoiceNewInjury;

	public DialogApothecaryChoice(FantasyFootballClient pClient, DialogApothecaryChoiceParameter pDialogParameter) {

		super(pClient, "Choose Apothecary Result", false);

		String playerName = pClient.getGame().getPlayerById(pDialogParameter.getPlayerId()).getName();

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		JPanel panelInfo1 = new JPanel();
		panelInfo1.setLayout(new BoxLayout(panelInfo1, BoxLayout.X_AXIS));
		panelInfo1.add(new JLabel(dimensionProvider(), "The apothecary gives you a choice.", RenderContext.ON_PITCH));
		panelInfo1.add(new JLabel(dimensionProvider(), "Injury for " + playerName, RenderContext.ON_PITCH));
		panelInfo1.add(Box.createHorizontalGlue());

		panelMain.add(panelInfo1);

		JPanel panelInfo2 = new JPanel();
		panelInfo2.setLayout(new BoxLayout(panelInfo2, BoxLayout.X_AXIS));
		panelInfo2.add(new JLabel(dimensionProvider(), "Which injury result do you want to keep?", RenderContext.ON_PITCH));
		panelInfo2.add(Box.createHorizontalGlue());

		panelMain.add(Box.createVerticalStrut(5));
		panelMain.add(panelInfo2);

		JPanel panelButtonOld = new JPanel();
		panelButtonOld.setLayout(new BoxLayout(panelButtonOld, BoxLayout.X_AXIS));
		if (pDialogParameter.getSeriousInjuryOld() != null) {
			fButtonInjuryOld = new JButton(dimensionProvider(), pDialogParameter.getSeriousInjuryOld().getButtonText(), RenderContext.ON_PITCH);
		} else {
			fButtonInjuryOld = new JButton(dimensionProvider(), pDialogParameter.getPlayerStateOld().getButtonText(), RenderContext.ON_PITCH);
		}
		fButtonInjuryOld.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		fButtonInjuryOld.addActionListener(this);
		panelButtonOld.add(fButtonInjuryOld);

		panelMain.add(Box.createVerticalStrut(10));
		panelMain.add(panelButtonOld);

		JPanel panelButtonNew = new JPanel();
		panelButtonNew.setLayout(new BoxLayout(panelButtonNew, BoxLayout.X_AXIS));
		if (pDialogParameter.getSeriousInjuryNew() != null) {
			fButtonInjuryNew = new JButton(dimensionProvider(), pDialogParameter.getSeriousInjuryNew().getButtonText(), RenderContext.ON_PITCH);
		} else {
			fButtonInjuryNew = new JButton(dimensionProvider(), pDialogParameter.getPlayerStateNew().getButtonText(), RenderContext.ON_PITCH);
		}
		fButtonInjuryNew.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		fButtonInjuryNew.addActionListener(this);
		panelButtonNew.add(fButtonInjuryNew);

		panelMain.add(Box.createVerticalStrut(5));
		panelMain.add(panelButtonNew);
		panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelMain, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.APOTHECARY_CHOICE;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonInjuryOld) {
			fChoiceNewInjury = false;
		}
		if (pActionEvent.getSource() == fButtonInjuryNew) {
			fChoiceNewInjury = true;
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public boolean isChoiceNewInjury() {
		return fChoiceNewInjury;
	}

}
