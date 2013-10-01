package com.balancedbytes.games.ffb.old;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailPlayerSkills extends JPanel {
  
  // public static final int WIDTH = 92;
  public static final int WIDTH = 128;
  public static final int HEIGHT = PlayerDetailPlayerPortrait.HEIGHT;
  
  private PlayerDetailComponent fPlayerDetailComponent;
  private JList fSkillList;
  private DefaultListModel fSkillListModel;

  private class ListItem {
    
    private Skill fSkill;
    private boolean fUsed;
    
    protected ListItem(Skill pSkill, boolean pUsed) {
      fSkill = pSkill;
      fUsed = pUsed;
    }
    
    public String toString() {
      if (!fUsed) {
        return fSkill.getName();
      } else {
        return new StringBuilder().append("* ").append(fSkill.getName()).toString();
      }
    }
    
  }
    
  public PlayerDetailPlayerSkills(PlayerDetailComponent pPlayerDetailComponent) {
    
    super(new BorderLayout());
    
    fPlayerDetailComponent = pPlayerDetailComponent;

    fSkillListModel = new DefaultListModel();
    fSkillList = new JList(fSkillListModel);
    fSkillList.setFont(new Font("Sans Serif", Font.BOLD, 12));
    fSkillList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fSkillList.setSelectionModel(
      new DefaultListSelectionModel() {
        public void setSelectionInterval(int pIndex0, int pIndex1) {
        }
        public void addSelectionInterval(int pIndex0, int pIndex1) {
        }
      }
    );

    JScrollPane skillListScroller = new JScrollPane(fSkillList);
    skillListScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    skillListScroller.setBorder(BorderFactory.createEmptyBorder());
    
    add(skillListScroller, BorderLayout.CENTER);
    
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
        
  }
  
  public void refresh(Player pPlayer) {
    fSkillListModel.clear();
    if (pPlayer != null) {
      Skill[] skills = pPlayer.getSkills();
      Arrays.sort(
        skills,
        new Comparator<Skill>() {
          public int compare(Skill pSkill1, Skill pSkill2) {
            return pSkill1.getName().compareTo(pSkill2.getName());
          }
        }
      );
      Game game = getPlayerDetailComponent().getClient().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
      for (int i = 0; i < skills.length; i++) {
        boolean skillUsed = (((pPlayer == actingPlayer.getPlayer()) && actingPlayer.isSkillUsed(skills[i])) || ((skills[i] == Skill.PRO) && playerResult.hasUsedPro()));
        fSkillListModel.addElement(new ListItem(skills[i], skillUsed));
      }
    }
    repaint();
  }
  
  public PlayerDetailComponent getPlayerDetailComponent() {
    return fPlayerDetailComponent;
  }
    
}
