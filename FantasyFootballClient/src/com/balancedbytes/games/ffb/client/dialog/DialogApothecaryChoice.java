package com.balancedbytes.games.ffb.client.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogApothecaryChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogApothecaryChoice extends Dialog implements ActionListener {

  private JButton fButtonInjuryOld;
  private JButton fButtonInjuryNew;
  
  private boolean fChoiceNewInjury;
  
  public DialogApothecaryChoice(FantasyFootballClient pClient, DialogApothecaryChoiceParameter pDialogParameter) {
    
  	super(pClient, "Choose Apothecary Result", false);
    
    JPanel panelMain = new JPanel();
    panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
    
    JPanel panelInfo1 = new JPanel();
    panelInfo1.setLayout(new BoxLayout(panelInfo1, BoxLayout.X_AXIS));
    panelInfo1.add(new JLabel("The apothecary gives you a choice."));
    panelInfo1.add(Box.createHorizontalGlue());
    
    panelMain.add(panelInfo1);
    
    JPanel panelInfo2 = new JPanel();
    panelInfo2.setLayout(new BoxLayout(panelInfo2, BoxLayout.X_AXIS));
    panelInfo2.add(new JLabel("Which injury result do you want to keep?"));
    panelInfo2.add(Box.createHorizontalGlue());

    panelMain.add(Box.createVerticalStrut(5));
    panelMain.add(panelInfo2);
    
    JPanel panelButtonOld = new JPanel();
    panelButtonOld.setLayout(new BoxLayout(panelButtonOld, BoxLayout.X_AXIS));
    if (pDialogParameter.getSeriousInjuryOld() != null) {
      fButtonInjuryOld = new JButton(pDialogParameter.getSeriousInjuryOld().getButtonText());
    } else {
      fButtonInjuryOld = new JButton(pDialogParameter.getPlayerStateOld().getButtonText());
    }
    fButtonInjuryOld.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    fButtonInjuryOld.addActionListener(this);
    panelButtonOld.add(fButtonInjuryOld);
    
    panelMain.add(Box.createVerticalStrut(10));
    panelMain.add(panelButtonOld);
    
    JPanel panelButtonNew = new JPanel();
    panelButtonNew.setLayout(new BoxLayout(panelButtonNew, BoxLayout.X_AXIS));
    if (pDialogParameter.getSeriousInjuryNew() != null) {
      fButtonInjuryNew = new JButton(pDialogParameter.getSeriousInjuryNew().getButtonText());
    } else {
      fButtonInjuryNew = new JButton(pDialogParameter.getPlayerStateNew().getButtonText());
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
