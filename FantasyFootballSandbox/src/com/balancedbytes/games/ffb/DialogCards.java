package com.balancedbytes.games.ffb;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DialogCards {

  /*
  BEGUILING_BRACERS("Beguiling Bracers", "Beguiling Bracers", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
      new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_GAME,
      "Player gets Hypnotic Gaze, Side Step & Bone-Head"),
  */
  
  /*
    fCardLogTextPane.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, pCard.getName());
    fCardLogTextPane.append(ParagraphStyle.INDENT_0, TextStyle.NONE, null);
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, pCard.getDescription());
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, pCard.getDuration().getDescription());
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, new InducementPhaseFactory().getDescription(pCard.getPhases()));
    fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
   */

  public static void main(String args[]) {
    JFrame f = new JFrame("Label Demo");
    f.setLayout(new FlowLayout());
    f.setSize(400, 360);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JLabel label = new JLabel(
        "<html>"
        + "<b>Beguiling Bracers</b><br>"
        + "Player gets Hypnotic Gaze, Side Step &amp; Bone-Head<br>"
        + "For the entire game<br>"
        + "<i>Play at start of own turn</i>"
        + "</html>"
    );
    label.setFont(new Font("Serif", Font.PLAIN, 14));

    JCheckBox checkbox = new JCheckBox();
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(checkbox);
    panel.add(Box.createHorizontalStrut(5));
    panel.add(label);
    
    Border border = BorderFactory.createLineBorder(Color.BLACK);
    panel.setBorder(border);
    
    f.add(panel);

    f.setVisible(true);
  }

}
