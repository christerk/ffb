package com.balancedbytes.games.ffb.client.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.GameListEntry;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.util.UtilJTable;
import com.balancedbytes.games.ffb.client.util.UtilReflection;
import com.balancedbytes.games.ffb.dialog.DialogId;

@SuppressWarnings("serial")
public class DialogGameChoice extends Dialog {
  
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");  // 2001-07-04 12:08:56

  private GameListEntry[] fGameListEntries;
  private int fSelectedIndex;
  private JTable fTable;
  
  private JButton fButtonCancel;
  private JButton fButtonOk;
  
  private class MyTableCellRenderer extends DefaultTableCellRenderer {
    public MyTableCellRenderer(int pHorizontalAlignment) {
      super();
      setHorizontalAlignment(pHorizontalAlignment);
    }
    public Component getTableCellRendererComponent(JTable pTable, Object pValue, boolean pIsSelected, boolean pHasFocus, int pRow, int pColumn) {
      return super.getTableCellRendererComponent(pTable, pValue, pIsSelected, false, pRow, pColumn);
    }
  }

  public DialogGameChoice(FantasyFootballClient pClient, GameList pGameList) {
    
    super(pClient, "Select Game", false);
    
    fGameListEntries = pGameList.getEntries(true);
    
    String[] columnNames = null;
    if (getClient().getParameters().getMode() == ClientMode.PLAYER) {
      columnNames = new String[] { "My Team", "Opposing Team", "Opponent", "Started" }; 
    } else {
      columnNames = new String[] { "Home Team", "Home Coach", "Away Team", "Away Coach", "Started" }; 
    }
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, fGameListEntries.length) {
      public Class<?> getColumnClass(int pColumnIndex) {
        Object o = getValueAt(0, pColumnIndex);
        if (o == null) {
          return Object.class;
        } else {
          return o.getClass();
        }
      }
    };

    for (int i = 0; i < fGameListEntries.length; i++) {
      String startedTimestamp = _TIMESTAMP_FORMAT.format(fGameListEntries[i].getStarted());
      if (ClientMode.PLAYER == getClient().getMode()) {
        if (getClient().getParameters().getCoach().equals(fGameListEntries[i].getTeamHomeCoach())) {
          tableModel.setValueAt(fGameListEntries[i].getTeamHomeName(), i, 0);
          tableModel.setValueAt(fGameListEntries[i].getTeamAwayName(), i, 1);
          tableModel.setValueAt(fGameListEntries[i].getTeamAwayCoach(), i, 2);
        } else {
          tableModel.setValueAt(fGameListEntries[i].getTeamAwayName(), i, 0);
          tableModel.setValueAt(fGameListEntries[i].getTeamHomeName(), i, 1);
          tableModel.setValueAt(fGameListEntries[i].getTeamHomeCoach(), i, 2);
        }
        tableModel.setValueAt(startedTimestamp, i, 3);
      } else {
        tableModel.setValueAt(fGameListEntries[i].getTeamHomeName(), i, 0);
        tableModel.setValueAt(fGameListEntries[i].getTeamHomeCoach(), i, 1);
        tableModel.setValueAt(fGameListEntries[i].getTeamAwayName(), i, 2);
        tableModel.setValueAt(fGameListEntries[i].getTeamAwayCoach(), i, 3);
        tableModel.setValueAt(startedTimestamp, i, 4);
      }
    }
        
    fTable = new JTable(tableModel);
    UtilReflection.setFillsViewportHeight(fTable, true);
    UtilReflection.setAutoCreateRowSorter(fTable, true);
    fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fTable.getTableHeader().setReorderingAllowed(false);
    fTable.setDefaultRenderer(Object.class, new MyTableCellRenderer(SwingConstants.LEFT));
    fTable.setDefaultRenderer(Integer.class, new MyTableCellRenderer(SwingConstants.RIGHT));
    fTable.getColumnModel().getColumn(0).setCellRenderer(new MyTableCellRenderer(SwingConstants.RIGHT));
    fTable.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent lse) {
          int viewRow = fTable.getSelectedRow();
          if (viewRow < 0) {
            fButtonOk.setEnabled(false);
          } else {
            fButtonOk.setEnabled(true);
            fSelectedIndex = UtilReflection.convertRowIndexToModel(fTable, viewRow);
          }
        }
      }
    );
    
    for (int column = 0; column < fTable.getColumnCount(); column++) {
      UtilJTable.packTableColumn(fTable, column, 5);
    }
    
    // int height = fTable.getTableHeader().getHeight();
    int nrOfVisibleRows = Math.min(10, fGameListEntries.length);
    int height = (nrOfVisibleRows + 1) * fTable.getRowHeight();
    fTable.setPreferredScrollableViewportSize(new Dimension(fTable.getPreferredSize().width, height));
    
    KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0); 
    Action enterKeyAction = new AbstractAction() {
      public void actionPerformed(ActionEvent ae) {
        String actionCommand = ae.getActionCommand();
        if (actionCommand.equals("EnterKey")) {
          checkAndCloseDialog(false);
        }
      }
    };    
    InputMap inputMap = fTable.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.remove(enterKeyStroke);
    fTable.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
    fTable.unregisterKeyboardAction(enterKeyStroke);
    fTable.registerKeyboardAction(enterKeyAction, "EnterKey", enterKeyStroke, JComponent.WHEN_FOCUSED);
    
    JScrollPane scrollPane;
    if (nrOfVisibleRows < fGameListEntries.length) {
      scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    } else {
      scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
    scrollPane.setViewportView(fTable);
    
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(1, 1));
    inputPanel.add(scrollPane);
    inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    
    fButtonCancel = new JButton("Cancel");
    fButtonCancel.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent pActionEvent) {
          fSelectedIndex = -1;
          checkAndCloseDialog(true);
        }
      }
    );

    if (ClientMode.SPECTATOR == pClient.getMode()) {
      fButtonOk = new JButton("Spectate");
    } else {
      fButtonOk = new JButton("Play");
    }
    fButtonOk.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent pActionEvent) {
          checkAndCloseDialog(false);
        }
      }
    );

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
    buttonPanel.add(fButtonOk);
    buttonPanel.add(fButtonCancel);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(inputPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocationToCenter();
    
  }

  public void showDialog(IDialogCloseListener pCloseListener) {
    fTable.getSelectionModel().setSelectionInterval(0, 0);
    super.showDialog(pCloseListener);
  }
  
  private void checkAndCloseDialog(boolean pCancelSelected) {
    if (pCancelSelected || (fSelectedIndex >= 0)) {
      if (getCloseListener() != null) {
        getCloseListener().dialogClosed(this);
      }
    }
  }
  
  public DialogId getId() {
    return DialogId.GAME_CHOICE;
  }
  
  public GameListEntry[] getGameListEntires() {
    return fGameListEntries;
  }  

  public GameListEntry getSelectedGameEntry() {
    if (fSelectedIndex >= 0) {
      return fGameListEntries[fSelectedIndex];
    } else {
      return null;
    }
  }
    
} 