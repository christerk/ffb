package com.balancedbytes.games.ffb.client.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogReRoll extends Dialog implements ActionListener, KeyListener {
  
  private JButton fButtonTeamReRoll;
  private JButton fButtonProReRoll;
  private JButton fButtonNoReRoll;
  
  private DialogReRollParameter fDialogParameter;
  private ReRollSource fReRollSource;
  
  public DialogReRoll(FantasyFootballClient pClient, DialogReRollParameter pDialogParameter) {

    super(pClient, "Use a Re-roll", false);

    fDialogParameter = pDialogParameter;
    
    fButtonTeamReRoll = new JButton("Team Re-Roll");
    fButtonTeamReRoll.addActionListener(this);
    fButtonTeamReRoll.addKeyListener(this);
    fButtonTeamReRoll.setMnemonic((int) 'T'); 

    fButtonProReRoll = new JButton("Pro Re-Roll");
    fButtonProReRoll.addActionListener(this);
    fButtonProReRoll.addKeyListener(this);
    fButtonProReRoll.setMnemonic((int) 'P'); 

    fButtonNoReRoll = new JButton("No Re-Roll");
    fButtonNoReRoll.addActionListener(this);
    fButtonNoReRoll.addKeyListener(this);
    fButtonNoReRoll.setMnemonic((int) 'N'); 

    StringBuilder message1 = new StringBuilder();
    if (fDialogParameter.getMinimumRoll() > 0) {
      message1.append("Do you want to re-roll the failed ").append(fDialogParameter.getReRolledAction().getName()).append("?");
    } else {
      message1.append("Do you want to re-roll the ").append(fDialogParameter.getReRolledAction().getName()).append("?");
    }
    
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.add(new JLabel(message1.toString()));
    
    if (pDialogParameter.isFumble()) {
      messagePanel.add(Box.createVerticalStrut(5));
      StringBuilder message2 = new StringBuilder();
      message2.append("Current roll is a FUMBLE.");
      messagePanel.add(new JLabel(message2.toString()));
    }
    
    Game game = getClient().getGame();
    Player reRollingPlayer = game.getPlayerById(pDialogParameter.getPlayerId());
    if ((reRollingPlayer != null) && UtilCards.hasSkill(game, reRollingPlayer, Skill.LONER)) {
      messagePanel.add(Box.createVerticalStrut(5));
      StringBuilder message3 = new StringBuilder();
      message3.append("Player is a LONER - the Re-Roll is not guaranteed to help.");
      messagePanel.add(new JLabel(message3.toString()));
    }
    		
    if (fDialogParameter.getMinimumRoll() > 0) {
      messagePanel.add(Box.createVerticalStrut(5));
      StringBuilder message4 = new StringBuilder();
      message4.append("You will need a roll of ").append(fDialogParameter.getMinimumRoll()).append("+ to succeed.");
      messagePanel.add(new JLabel(message4.toString()));
    }
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE_SMALL);
    infoPanel.add(new JLabel(new ImageIcon(icon)));
    infoPanel.add(Box.createHorizontalStrut(5));
    infoPanel.add(messagePanel);
    infoPanel.add(Box.createHorizontalGlue());
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    if (fDialogParameter.isTeamReRollOption()) {
      buttonPanel.add(fButtonTeamReRoll);
      buttonPanel.add(Box.createHorizontalStrut(5));
    }
    if (fDialogParameter.isProReRollOption()) {
      buttonPanel.add(fButtonProReRoll);
      buttonPanel.add(Box.createHorizontalStrut(5));
    }
    buttonPanel.add(fButtonNoReRoll);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    
    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    getContentPane().add(infoPanel);
    getContentPane().add(buttonPanel);

    pack();
    setLocationToCenter();

  }
  
  public DialogId getId() {
    return DialogId.RE_ROLL;
  }
  
  public void actionPerformed(ActionEvent pActionEvent) {
    if (pActionEvent.getSource() == fButtonTeamReRoll) {
      fReRollSource = ReRollSource.TEAM_RE_ROLL;
    }
    if (pActionEvent.getSource() == fButtonProReRoll) {
      fReRollSource = ReRollSource.PRO;
    }
    if (pActionEvent.getSource() == fButtonNoReRoll) {
      fReRollSource = null;
    }
    if (getCloseListener() != null) {
      getCloseListener().dialogClosed(this);
    }
  }
  
  public ReRollSource getReRollSource() {
    return fReRollSource;
  }
  
  public ReRolledAction getReRolledAction() {
    return fDialogParameter.getReRolledAction();
  }
  
  public DialogReRollParameter getDialogParameter() {
    return fDialogParameter;
  }
  
  public void keyPressed(KeyEvent pKeyEvent) {
  }
  
  public void keyReleased(KeyEvent pKeyEvent) {
    boolean keyHandled = true;
    switch (pKeyEvent.getKeyCode()) {
      case KeyEvent.VK_T:
        if (getDialogParameter().isTeamReRollOption()) {
          fReRollSource = ReRollSource.TEAM_RE_ROLL;
        }
        break;
      case KeyEvent.VK_P:
        if (getDialogParameter().isProReRollOption()) {
          fReRollSource = ReRollSource.PRO;
        }
        break;
      case KeyEvent.VK_N:
        keyHandled = true; 
        break;
      default:
        keyHandled = false;
        break;
    }
    if (keyHandled) {
      if (getCloseListener() != null) {
        getCloseListener().dialogClosed(this);
      }
    }
  }
  
  public void keyTyped(KeyEvent pKeyEvent) {
  }

}
