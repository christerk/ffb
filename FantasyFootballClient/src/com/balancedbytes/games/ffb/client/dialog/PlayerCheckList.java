package com.balancedbytes.games.ffb.client.dialog;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.ArrayTool;

@SuppressWarnings("serial")
public class PlayerCheckList extends JList {
  
  // Handles rendering cells in the list using a check box

  private class PlayerCheckListRenderer extends JPanel implements ListCellRenderer {
    
    private JCheckBox fCheckBox;
    private JLabel fLabel;
    
    public PlayerCheckListRenderer() {
      super();
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      fCheckBox = new JCheckBox();
      add(fCheckBox);
      fLabel = new JLabel();
      add(fLabel);
    }
    
    public Component getListCellRendererComponent(JList pList, Object pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
      setEnabled(pList.isEnabled());
      setFont(pList.getFont());
      setBackground(pList.getBackground());
      setForeground(pList.getForeground());
      fCheckBox.setBackground(pList.getBackground());
      PlayerCheckListItem listItem = (PlayerCheckListItem) pValue;
      fCheckBox.setSelected(listItem.isSelected());
      fLabel.setIcon(listItem.getIcon());
      fLabel.setText(listItem.getText());
      return this;
    }
    
  }
  
  private class PlayerCheckListMouseAdapter extends MouseAdapter {
    
    private int fMaxSelects;
    
    public PlayerCheckListMouseAdapter(int pMaxSelects) {
      fMaxSelects = pMaxSelects;
    }

    public void mouseReleased(MouseEvent event) {
      JList list = (JList) event.getSource();
      int index = list.locationToIndex(event.getPoint());
      PlayerCheckListItem selectedItem = (PlayerCheckListItem) list.getModel().getElementAt(index);
      if (!selectedItem.isSelected()) {
        if (fMaxSelects > 1) {
          int nrOfSelectedItems = 0;
          for (int i = 0; i < list.getModel().getSize(); i++) {
            PlayerCheckListItem item = (PlayerCheckListItem) list.getModel().getElementAt(i); 
            if (item.isSelected()) {
              nrOfSelectedItems++;
            }
          }
          if (nrOfSelectedItems < fMaxSelects) {
            selectedItem.setSelected(true);
          }
        } else {
          for (int i = 0; i < list.getModel().getSize(); i++) {
            PlayerCheckListItem item = (PlayerCheckListItem) list.getModel().getElementAt(i);
            if (item.isSelected()) {
              item.setSelected(false);
              list.repaint(list.getCellBounds(i, i));
            }
          }
          selectedItem.setSelected(true);
        }
      } else {
        selectedItem.setSelected(false);
      }
      list.repaint(list.getCellBounds(index, index));
    }

  }
  
  public PlayerCheckList(FantasyFootballClient pClient, String[] pPlayerIds, String[] pDescriptions, int pMaxSelects) {
    
    if (!ArrayTool.isProvided(pPlayerIds)) {
      throw new IllegalArgumentException("Argument players must not be empty or null.");
    }
    
    Game game = pClient.getGame();
    List<PlayerCheckListItem> checkListItems = new ArrayList<PlayerCheckListItem>();
    PlayerIconFactory playerIconFactory = pClient.getUserInterface().getPlayerIconFactory();
    for (int i = 0; i < pPlayerIds.length; i++) {
      Player player = game.getPlayerById(pPlayerIds[i]);
      if (player != null) {
        boolean homePlayer = game.getTeamHome().hasPlayer(player);
        BufferedImage playerIcon = playerIconFactory.getBasicIcon(pClient, player, homePlayer, false, false, false);
        StringBuilder text = new StringBuilder();
        text.append(player.getName());
        if (ArrayTool.isProvided(pDescriptions)) {
          text.append(" ").append(pDescriptions[i]);
        }
        PlayerCheckListItem checkListItem = new PlayerCheckListItem(player, new ImageIcon(playerIcon), text.toString());
        checkListItem.setSelected(pPlayerIds.length == 1);
        checkListItems.add(checkListItem);
      }
    }
    setListData(checkListItems.toArray(new PlayerCheckListItem[checkListItems.size()]));

    // Use a CheckListRenderer (see below) to renderer list cells
    setCellRenderer(new PlayerCheckListRenderer());
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Add a mouse listener to handle changing selection
    addMouseListener(new PlayerCheckListMouseAdapter(pMaxSelects));
    
  }
  
  public Player getPlayerAtIndex(int pIndex) {
    PlayerCheckListItem checkListItem = (PlayerCheckListItem) getModel().getElementAt(pIndex);
    if (checkListItem != null) {
      return checkListItem.getPlayer();
    } else {
      return null;
    }
  }
  
  public Player[] getSelectedPlayers() {
    List<Player> selectedPlayers = new ArrayList<Player>();
    for (int i = 0; i < getModel().getSize(); i++) {
      PlayerCheckListItem item = (PlayerCheckListItem) getModel().getElementAt(i);
      if (item.isSelected()) {
        selectedPlayers.add(item.getPlayer());
      }
    }
    return selectedPlayers.toArray(new Player[selectedPlayers.size()]);
  }
  
}