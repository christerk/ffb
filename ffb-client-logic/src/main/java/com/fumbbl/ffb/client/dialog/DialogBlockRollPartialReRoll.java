package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.dialog.DialogBlockRollPartialReRollParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogBlockRollPartialReRoll extends AbstractDialogBlock implements ActionListener, KeyListener {

  private final JButton[] fBlockDice;

  private JButton fButtonTeamReRoll;
  private JButton fButtonProReRoll;
  private JButton fButtonNoReRoll;
  private final ReRollSource singleUseReRollSource;
  private JButton brawlerButton, proButton1, proButton2, proButton3,
    consummateButton, consummateButton1, consummateButton2, consummateButton3,
    singleDieButton, singleDieButton1, singleDieButton2, singleDieButton3;

  private int fDiceIndex, proIndex;
  private ReRollSource fReRollSource, singleDieReRollSource, singleBlockDieReRollSource;
  private JButton buttonSingleUseReRoll;

  private final DialogBlockRollPartialReRollParameter fDialogParameter;

  public DialogBlockRollPartialReRoll(FantasyFootballClient pClient, DialogBlockRollPartialReRollParameter pDialogParameter) {

    super(pClient, "Block Roll", false);

    fDiceIndex = -1;
    fDialogParameter = pDialogParameter;
    singleUseReRollSource = pDialogParameter.getSingleUseReRollSource();

    Skill singleDieReRollSkill = getClient().getGame().getActingPlayer().getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
    if (singleDieReRollSkill != null) {
      singleDieReRollSource = singleDieReRollSkill.getRerollSource(ReRolledActions.SINGLE_DIE);
    }

    Skill singleBlockDieReRollSkill = getDialogParameter().getReRollSingleDieSkill();
    if (singleBlockDieReRollSkill != null) {
      singleBlockDieReRollSource = singleBlockDieReRollSkill.getRerollSource(ReRolledActions.SINGLE_BLOCK_DIE);
    }

    IconCache iconCache = getClient().getUserInterface().getIconCache();

    JPanel centerPanel = new BackgroundPanel((getDialogParameter().getNrOfDice() < 0) ? colorOpponentChoice : colorOwnChoice);
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

    JPanel blockRollPanel = blockRollPanel();

    int[] blockRoll = getDialogParameter().getBlockRoll();
    fBlockDice = new JButton[blockRoll.length];
    boolean ownChoice = ((fDialogParameter.getNrOfDice() > 0)
      || (!fDialogParameter.hasTeamReRollOption() && !fDialogParameter.hasProReRollOption() && !fDialogParameter.hasBrawlerOption()));
    for (int i = 0; i < fBlockDice.length; i++) {
      fBlockDice[i] = new JButton(dimensionProvider());
      fBlockDice[i].setOpaque(false);
      fBlockDice[i].setBounds(0, 0, 45, 45);
      fBlockDice[i].setFocusPainted(false);
      fBlockDice[i].setMargin(new Insets(5, 5, 5, 5));
      fBlockDice[i].setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll[i])));
      int finalI = i;
      if ((getDialogParameter().hasProReRollOption() || getDialogParameter().hasBrawlerOption()) && Arrays.stream(fDialogParameter.getReRolledDiceIndexes()).anyMatch(index -> index == finalI)) {
        fBlockDice[i].setBorder(BorderFactory.createLineBorder(Color.red, 3, true));
      }
      blockRollPanel.add(fBlockDice[i]);
      if (ownChoice) {
        fBlockDice[i].addActionListener(this);
        if (i == 0) {
          fBlockDice[i].setMnemonic(KeyEvent.VK_1);
        }
        if (i == 1) {
          fBlockDice[i].setMnemonic(KeyEvent.VK_2);
        }
        if (i == 2) {
          fBlockDice[i].setMnemonic(KeyEvent.VK_3);
        }
        fBlockDice[i].addKeyListener(this);
      }
    }

    centerPanel.add(blockRollPanel);

    if (!ownChoice) {
      centerPanel.add(opponentChoicePanel());
    }

    if (getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption() || getDialogParameter().hasConsummateOption()
      || pDialogParameter.hasBrawlerOption() || singleUseReRollSource != null || singleDieReRollSource != null) {

      JPanel reRollPanel = new JPanel();
      reRollPanel.setOpaque(false);
      reRollPanel.setLayout(new BoxLayout(reRollPanel, BoxLayout.X_AXIS));

      fButtonTeamReRoll = new JButton(dimensionProvider(), "Team Re-Roll", KeyEvent.VK_T);
      fButtonTeamReRoll.addActionListener(this);
      fButtonTeamReRoll.setMnemonic(KeyEvent.VK_T);
      fButtonTeamReRoll.addKeyListener(this);

      fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll", KeyEvent.VK_N);
      fButtonNoReRoll.addActionListener(this);
      fButtonNoReRoll.setMnemonic(KeyEvent.VK_N);
      fButtonNoReRoll.addKeyListener(this);

      Box.Filler verticalGlue1 = (Box.Filler) Box.createVerticalGlue();
      verticalGlue1.setOpaque(false);
      reRollPanel.add(verticalGlue1);

      if (getDialogParameter().hasTeamReRollOption()) {
        reRollPanel.add(fButtonTeamReRoll);
      }

      if (singleUseReRollSource != null) {
        buttonSingleUseReRoll = new JButton(dimensionProvider(), singleUseReRollSource.getName(getClient().getGame()), KeyEvent.VK_L);
        buttonSingleUseReRoll.addActionListener(this);
        buttonSingleUseReRoll.setMnemonic(KeyEvent.VK_L);
        buttonSingleUseReRoll.addKeyListener(this);
        reRollPanel.add(buttonSingleUseReRoll);
      }

      if (getDialogParameter().getNrOfDice() == 1) {
        if (getDialogParameter().hasProReRollOption()) {
          fButtonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll",  KeyEvent.VK_P);
          fButtonProReRoll.addActionListener(this);
          fButtonProReRoll.setMnemonic(KeyEvent.VK_P);
          fButtonProReRoll.addKeyListener(this);
          reRollPanel.add(fButtonProReRoll);
        }

        if (getDialogParameter().hasConsummateOption() && singleDieReRollSource != null) {
          consummateButton = new JButton(dimensionProvider(), singleDieReRollSource.getName(getClient().getGame()),KeyEvent.VK_C);
          consummateButton.addActionListener(this);
          consummateButton.setMnemonic(KeyEvent.VK_C);
          consummateButton.addKeyListener(this);
          reRollPanel.add(consummateButton);
        }

        if (singleBlockDieReRollSource != null) {
          singleDieButton = new JButton(dimensionProvider(), singleBlockDieReRollSource.getName(getClient().getGame()),KeyEvent.VK_U);
          singleDieButton.addActionListener(this);
          singleDieButton.setMnemonic(KeyEvent.VK_U);
          singleDieButton.addKeyListener(this);
          reRollPanel.add(singleDieButton);
        }
      }

      if (getDialogParameter().hasBrawlerOption()) {
        brawlerButton = new JButton(dimensionProvider(), "Brawler Re-Roll", KeyEvent.VK_B );
        brawlerButton.addActionListener(this);
        brawlerButton.setMnemonic(KeyEvent.VK_B);
        brawlerButton.addKeyListener(this);
        reRollPanel.add(brawlerButton);
      }

      if (getDialogParameter().getNrOfDice() < 0) {
        reRollPanel.add(fButtonNoReRoll);
      }

      Box.Filler verticalGlue2 = (Box.Filler) Box.createVerticalGlue();
      verticalGlue2.setOpaque(false);
      reRollPanel.add(verticalGlue2);

      centerPanel.add(Box.createVerticalStrut(10));
      centerPanel.add(reRollPanel);

      if (Math.abs(getDialogParameter().getNrOfDice()) > 1) {
        if (getDialogParameter().hasProReRollOption()) {
          centerPanel.add(proPanel(Math.abs(pDialogParameter.getNrOfDice())));
          centerPanel.add(Box.createVerticalStrut(3));
        }

        if (pDialogParameter.hasConsummateOption() && singleDieReRollSource != null) {
          centerPanel.add(consummatePanel(Math.abs(pDialogParameter.getNrOfDice())));
          centerPanel.add(Box.createVerticalStrut(3));
        }

        if (singleBlockDieReRollSource != null) {
          centerPanel.add(singleBlockDiePanel(Math.abs(pDialogParameter.getNrOfDice())));
          centerPanel.add(Box.createVerticalStrut(3));

        }
      }
    }

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(centerPanel, BorderLayout.CENTER);

    pack();
    setLocationToCenter();

  }

  public DialogId getId() {
    return DialogId.BLOCK_ROLL_PARTIAL_RE_ROLL;
  }

  private JPanel singleBlockDiePanel(int diceCount) {
    List<Integer> mnemonics = new ArrayList<>();
    mnemonics.add(KeyEvent.VK_U);
    mnemonics.add(KeyEvent.VK_S);
    mnemonics.add(KeyEvent.VK_A);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(textPanel(singleBlockDieReRollSource.getName(getClient().getGame())));
    panel.setOpaque(false);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
    for (int i = 1; i <= diceCount; i++) {
      int finalI = i;
      if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
        JButton button = singleDieButton(i, mnemonics.get(0));
        switch (i) {
          case 1:
            singleDieButton1 = button;
            break;
          case 2:
            singleDieButton2 = button;
            break;
          default:
            singleDieButton3 = button;
            break;
        }
        buttonPanel.add(button);
      }

      panel.add(buttonPanel);
    }
    mnemonics.remove(0);

    return panel;
  }

  private JPanel proPanel(int diceCount) {
    List<Integer> proMnemonics = new ArrayList<>();
    proMnemonics.add(KeyEvent.VK_P);
    proMnemonics.add(KeyEvent.VK_R);
    proMnemonics.add(KeyEvent.VK_E);
    JPanel proPanel = new JPanel();
    proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
    proPanel.setAlignmentX(CENTER_ALIGNMENT);
    proPanel.add(proTextPanel());
    proPanel.setOpaque(false);

    JPanel proButtonPanel = new JPanel();
    proButtonPanel.setLayout(new BoxLayout(proButtonPanel, BoxLayout.X_AXIS));
    proButtonPanel.setAlignmentX(CENTER_ALIGNMENT);
    for (int i = 1; i <= diceCount; i++) {
      int finalI = i;
      if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
        JButton button = singleDieButton(i, proMnemonics.get(0));
        switch (i) {
          case 1:
            proButton1 = button;
            break;
          case 2:
            proButton2 = button;
            break;
          default:
            proButton3 = button;
            break;
        }
        proButtonPanel.add(button);
      }

      proPanel.add(proButtonPanel);
    }
    proMnemonics.remove(0);

    return proPanel;
  }

  private JPanel consummatePanel(int diceCount) {
    List<Integer> consummateMnemonics = new ArrayList<>();
    consummateMnemonics.add(KeyEvent.VK_C);
    consummateMnemonics.add(KeyEvent.VK_O);
    consummateMnemonics.add(KeyEvent.VK_M);
    JPanel consummatePanel = new JPanel();
    consummatePanel.setLayout(new BoxLayout(consummatePanel, BoxLayout.Y_AXIS));
    consummatePanel.setAlignmentX(CENTER_ALIGNMENT);
    consummatePanel.add(textPanel(singleDieReRollSource.getName(getClient().getGame())));
    consummatePanel.setOpaque(false);

    JPanel consummateButtonPanel = new JPanel();
    consummateButtonPanel.setLayout(new BoxLayout(consummateButtonPanel, BoxLayout.X_AXIS));
    consummateButtonPanel.setAlignmentX(CENTER_ALIGNMENT);
    for (int i = 1; i <= diceCount; i++) {
      int finalI = i;
      if (Arrays.stream(getDialogParameter().getReRolledDiceIndexes()).noneMatch(index -> index == finalI - 1)) {
        JButton button = singleDieButton(i, consummateMnemonics.get(0));
        switch (i) {
          case 1:
            consummateButton1 = button;
            break;
          case 2:
            consummateButton2 = button;
            break;
          default:
            consummateButton3 = button;
            break;
        }
        consummateButtonPanel.add(button);
      }

      consummatePanel.add(consummateButtonPanel);
    }
    consummateMnemonics.remove(0);

    return consummatePanel;
  }


  private JButton singleDieButton(int dieNumber, int keyEvent) {
    JButton singleDieButton = new JButton(dimensionProvider(), "Die " + dieNumber, keyEvent);
    singleDieButton.addActionListener(this);
    singleDieButton.setMnemonic(keyEvent);
    singleDieButton.addKeyListener(this);
    return singleDieButton;
  }

  private void setBrawler() {
    fReRollSource = ReRollSources.BRAWLER;
  }

  public void actionPerformed(ActionEvent pActionEvent) {
    Game game = getClient().getGame();
    boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
    if (pActionEvent.getSource() == fButtonTeamReRoll) {
      fReRollSource = ReRollSources.TEAM_RE_ROLL;
    }
    if (pActionEvent.getSource() == buttonSingleUseReRoll) {
      fReRollSource = singleUseReRollSource;
    }
    if (pActionEvent.getSource() == singleDieButton) {
      fReRollSource = singleBlockDieReRollSource;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == singleDieButton1) {
      fReRollSource = singleBlockDieReRollSource;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == singleDieButton2) {
      fReRollSource = singleBlockDieReRollSource;
      proIndex = 1;
    }
    if (pActionEvent.getSource() == singleDieButton3) {
      fReRollSource = singleBlockDieReRollSource;
      proIndex = 2;
    }
    if (pActionEvent.getSource() == consummateButton) {
      fReRollSource = singleDieReRollSource;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == consummateButton1) {
      fReRollSource = singleDieReRollSource;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == consummateButton2) {
      fReRollSource = singleDieReRollSource;
      proIndex = 1;
    }
    if (pActionEvent.getSource() == consummateButton3) {
      fReRollSource = singleDieReRollSource;
      proIndex = 2;
    }
    if (pActionEvent.getSource() == fButtonProReRoll) {
      fReRollSource = ReRollSources.PRO;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == proButton1) {
      fReRollSource = ReRollSources.PRO;
      proIndex = 0;
    }
    if (pActionEvent.getSource() == proButton2) {
      fReRollSource = ReRollSources.PRO;
      proIndex = 1;
    }
    if (pActionEvent.getSource() == proButton3) {
      fReRollSource = ReRollSources.PRO;
      proIndex = 2;
    }
    if (pActionEvent.getSource() == brawlerButton) {
      setBrawler();
    }
    if (homeChoice && (fBlockDice.length >= 1) && (pActionEvent.getSource() == fBlockDice[0])) {
      fDiceIndex = 0;
    }
    if (homeChoice && (fBlockDice.length >= 2) && (pActionEvent.getSource() == fBlockDice[1])) {
      fDiceIndex = 1;
    }
    if (homeChoice && (fBlockDice.length >= 3) && (pActionEvent.getSource() == fBlockDice[2])) {
      fDiceIndex = 2;
    }

    if ((fReRollSource != null) || (fDiceIndex >= 0) || (pActionEvent.getSource() == fButtonNoReRoll)) {
      if (getCloseListener() != null) {
        getCloseListener().dialogClosed(this);
      }
    }
  }

  public void keyTyped(KeyEvent pKeyEvent) {
  }

  public ReRollSource getReRollSource() {
    return fReRollSource;
  }

  public int getDiceIndex() {
    return fDiceIndex;
  }

  public DialogBlockRollPartialReRollParameter getDialogParameter() {
    return fDialogParameter;
  }

  public int getProIndex() {
    return proIndex;
  }

  public ReRollSource getSingleDieReRollSource() {
    return singleDieReRollSource;
  }

  public ReRollSource getSingleBlockDieReRollSource() {
    return singleBlockDieReRollSource;
  }

  public void keyPressed(KeyEvent pKeyEvent) {
  }

  public void keyReleased(KeyEvent pKeyEvent) {
    Game game = getClient().getGame();
    boolean homeChoice = ((getDialogParameter().getNrOfDice() > 0) || !game.isHomePlaying());
    boolean keyHandled = false;
    switch (pKeyEvent.getKeyCode()) {
      case KeyEvent.VK_1:
        if (homeChoice && (fBlockDice.length >= 1)) {
          keyHandled = true;
          fDiceIndex = 0;
        }
        break;
      case KeyEvent.VK_2:
        if (homeChoice && (fBlockDice.length >= 2)) {
          keyHandled = true;
          fDiceIndex = 1;
        }
        break;
      case KeyEvent.VK_3:
        if (homeChoice && (fBlockDice.length >= 3)) {
          keyHandled = true;
          fDiceIndex = 2;
        }
        break;
      case KeyEvent.VK_T:
        if (getDialogParameter().hasTeamReRollOption()) {
          keyHandled = true;
          fReRollSource = ReRollSources.TEAM_RE_ROLL;
        }
        break;
      case KeyEvent.VK_L:
        if (singleUseReRollSource != null) {
          keyHandled = true;
          fReRollSource = singleUseReRollSource;
        }
        break;
      case KeyEvent.VK_P:
        if (getDialogParameter().hasProReRollOption()) {
          keyHandled = true;
          fReRollSource = ReRollSources.PRO;
          proIndex = 0;
        }
        break;
      case KeyEvent.VK_R:
        if (getDialogParameter().hasProReRollOption()) {
          keyHandled = true;
          fReRollSource = ReRollSources.PRO;
          proIndex = 1;
        }
        break;
      case KeyEvent.VK_E:
        if (getDialogParameter().hasProReRollOption()) {
          keyHandled = true;
          fReRollSource = ReRollSources.PRO;
          proIndex = 2;
        }
        break;
      case KeyEvent.VK_C:
        if (getDialogParameter().hasConsummateOption()) {
          keyHandled = true;
          fReRollSource = singleDieReRollSource;
          proIndex = 0;
        }
        break;
      case KeyEvent.VK_O:
        if (getDialogParameter().hasConsummateOption()) {
          keyHandled = true;
          fReRollSource = singleDieReRollSource;
          proIndex = 1;
        }
        break;
      case KeyEvent.VK_M:
        if (getDialogParameter().hasConsummateOption()) {
          keyHandled = true;
          fReRollSource = singleDieReRollSource;
          proIndex = 2;
        }
        break;
      case KeyEvent.VK_U:
        if (singleBlockDieReRollSource != null) {
          keyHandled = true;
          fReRollSource = singleBlockDieReRollSource;
          proIndex = 0;
        }
        break;
      case KeyEvent.VK_S:
        if (singleBlockDieReRollSource != null) {
          keyHandled = true;
          fReRollSource = singleBlockDieReRollSource;
          proIndex = 1;
        }
        break;
      case KeyEvent.VK_A:
        if (singleBlockDieReRollSource != null) {
          keyHandled = true;
          fReRollSource = singleBlockDieReRollSource;
          proIndex = 2;
        }
        break;
      case KeyEvent.VK_N:
        keyHandled = ((getDialogParameter().hasTeamReRollOption() || getDialogParameter().hasProReRollOption())
          && (getDialogParameter().getNrOfDice() < 0));
        break;
      case KeyEvent.VK_B:
        if (brawlerButton != null) {
          keyHandled = true;
          setBrawler();
        }
        break;
      default:
        break;
    }
    if (keyHandled) {
      if (getCloseListener() != null) {
        getCloseListener().dialogClosed(this);
      }
    }
  }

}
