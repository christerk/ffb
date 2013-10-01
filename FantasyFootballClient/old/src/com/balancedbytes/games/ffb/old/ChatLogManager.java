package com.balancedbytes.games.ffb.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.client.ActionKeyGroup;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.ReplayControl;
import com.balancedbytes.games.ffb.client.TextStyle;

/**
 * 
 * @author Dominic Schabel
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class ChatLogManager {

//  public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
//  
//  private class CommandHighlightArea {
//    
//    private int fCommandNr;
//    private int fStartPosition;
//    private int fEndPosition;
//    
//    public CommandHighlightArea(int pCommandNr) {
//      fCommandNr = pCommandNr;
//    }
//    
//    public int getCommandNr() {
//      return fCommandNr;
//    }
//    
//    public int getStartPosition() {
//      return fStartPosition;
//    }
//    
//    public void setStartPosition(int pStartPosition) {
//      fStartPosition = pStartPosition;
//    }
//    
//    public int getEndPosition() {
//      return fEndPosition;
//    }
//    
//    public void setEndPosition(int pEndPosition) {
//      fEndPosition = pEndPosition;
//    }
//    
//  }
//
//  private class MyScrollPane extends JScrollPane implements AdjustmentListener, ComponentListener {
//
//    private int fOldVisibleMaximum;
//
//    public MyScrollPane(Component pView) {
//      super(pView);
//      setHorizontalScrollBarPolicy(ScrollPaneLayout.HORIZONTAL_SCROLLBAR_NEVER);
//      setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_AS_NEEDED);
//      getVerticalScrollBar().addAdjustmentListener(this);
//      addComponentListener(this);
//    }
//
//    public void adjustmentValueChanged(AdjustmentEvent pE) {
//      JScrollBar scrollBar = (JScrollBar) pE.getSource();
//      int visibleMaximum = findVisibleMaximum(scrollBar);
//      if (visibleMaximum > fOldVisibleMaximum) {
//        if (scrollBar.getValue() == fOldVisibleMaximum) {
//          scrollBar.setValue(visibleMaximum);
//        }
//        fOldVisibleMaximum = visibleMaximum;
//      }
//    }
//
//    private int findVisibleMaximum(JScrollBar pScrollBar) {
//      return (pScrollBar.getMaximum() - pScrollBar.getVisibleAmount());
//    }
//
//    public void componentResized(ComponentEvent pE) {
//      fOldVisibleMaximum = findVisibleMaximum(getVerticalScrollBar());
//    }
//
//    public void componentHidden(ComponentEvent pE) {
//    }
//
//    public void componentMoved(ComponentEvent pE) {
//    }
//
//    public void componentShown(ComponentEvent pE) {
//      fOldVisibleMaximum = findVisibleMaximum(getVerticalScrollBar());
//    }
//    
//    public void setScrollBarToMaximum() {
//      getVerticalScrollBar().setValue(findVisibleMaximum(getVerticalScrollBar()));
//    }
//
//  }
//  
//  private class MyHighlighter extends DefaultHighlighter implements Highlighter.HighlightPainter {
//    
//    private Object fHighlight;
//    private JTextComponent fTextComponent;
//    private Rectangle fLastUpdatedArea;
//
//    public void changeHighlight(int pP0, int pP1) throws BadLocationException {
//      if (fHighlight == null) {
//        try {
//          // fHighlight = getHighlighter().addHighlight(0, 0, new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
//          fHighlight = addHighlight(0, 0, this);
//        } catch (BadLocationException ble) {
//          ble.printStackTrace();
//        }
//      }
//      super.changeHighlight(fHighlight, pP0, pP1);
//      repaintLastUpdatedArea();
//    }
//    public void paint(Graphics pGraphics, int pP0, int pP1, Shape pBounds, JTextComponent pTextComponent) {
//      try {
//        fTextComponent = pTextComponent;
//        pGraphics.setColor(Color.LIGHT_GRAY);
//        Rectangle leftUpperCorner = pTextComponent.modelToView(pP0);
//        Rectangle rightLowerCorner = pTextComponent.modelToView(pP1);
//        Insets insets = pTextComponent.getInsets();
////        pG.fillRect(insets.left, leftUpperCorner.y + insets.top, pC.getWidth() - insets.left - insets.right, rightLowerCorner.y - leftUpperCorner.y - insets.bottom - insets.top);
//        Rectangle updatedArea = new Rectangle(insets.left, leftUpperCorner.y, pTextComponent.getWidth() - insets.left - insets.right, rightLowerCorner.y - leftUpperCorner.y);
//        pGraphics.fillRect(updatedArea.x, updatedArea.y, updatedArea.width, updatedArea.height);
//        if (fLastUpdatedArea == null) {
//          fLastUpdatedArea = updatedArea;
//        } else {
//          fLastUpdatedArea.add(updatedArea);
//        }
//      } catch (BadLocationException ble) {
//        ble.printStackTrace();
//      }
//    }
//    
//    public void repaintLastUpdatedArea() {
//      if ((fTextComponent != null) && (fLastUpdatedArea != null)) {
//        fTextComponent.repaint(fLastUpdatedArea);
//        fLastUpdatedArea = null;
//      }
//    }
//    
//  }
//  
//  private class MyTextPane extends JTextPane {
//    
//    private boolean fReplayable;
//
//    public MyTextPane(boolean pReplayable) {
//      fReplayable = pReplayable;
//      setEditable(false);
//      // keep textpane from autoscrolling
//      DefaultCaret caret = (DefaultCaret) getCaret();
//      caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
//      setDocument(new ChatLogDocument());
//      setHighlighter(new MyHighlighter());
//    }
//    
//    protected void processMouseEvent(MouseEvent pMouseEvent) {
//      if (fReplayable && getClient().getReplayer().isReplaying()) {
//        if (MouseEvent.MOUSE_PRESSED == pMouseEvent.getID()) {
//          mousePressedForReplay(pMouseEvent);
//        }
//      } else {
//        super.processMouseEvent(pMouseEvent);
//      }
//    }
//    
//    private void mousePressedForReplay(MouseEvent pMouseEvent) {
//      ClientReplayer replayer = getClient().getReplayer();
//      int position = viewToModel(pMouseEvent.getPoint());
//      int commandNr = findCommandNr(position);
//      if (commandNr > 0) {
//        replayer.replayToCommand(commandNr);
//      }
//    }
//
//  }
//
//  private class MyFocusListener implements FocusListener {
//
//    private JComponent fComponent;
//    private boolean fChatMode;
//
//    public MyFocusListener(JComponent pComponent, boolean pChatMode) {
//      fComponent = pComponent;
//      fChatMode = pChatMode;
//    }
//
//    public void focusGained(FocusEvent pE) {
//      fLastFocusedComponent = fComponent;
//      getClient().getUserInterface().getStatusBar().setChatMode(fChatMode);
//      getClient().getUserInterface().getStatusBar().refresh();
//    }
//
//    public void focusLost(FocusEvent pE) {
//    }
//
//  }
//
//  private MyTextPane fAllTextPane;
//  private MyTextPane fChatTextPane;
//  private MyTextPane fLogTextPane;
//  
//  private ChatLogDocument fLogDocument;
//  private ChatLogDocument fAllDocument;
//
//  private MyScrollPane fAllScrollPane;
//  private MyScrollPane fChatScrollPane;
//  private MyScrollPane fLogScrollPane;
//
//  private JTextField fChatInputField;
//  private ReplayControl fReplayControl;
//  private boolean fReplayShown;
//
//  private JFrame fChatWindow;
//  private JPanel fLogPanel;
//
//  private FantasyFootballClient fClient;
//
//  private JComponent fLastFocusedComponent;
//  private Map<Integer, CommandHighlightArea> fCommandHighlightAreaByCommandNr;
//  private CommandHighlightArea fCurrentCommandHighlight;
//  private int fMinimumCommandNr;
//  
//  public ChatLogManager(FantasyFootballClient pClient) {
//
//    fClient = pClient;
//    fCommandHighlightAreaByCommandNr = new HashMap<Integer, CommandHighlightArea>();
//
//    fAllTextPane = new MyTextPane(false);
//    fAllTextPane.addFocusListener(new MyFocusListener(fAllTextPane, false));
//    fAllScrollPane = new MyScrollPane(fAllTextPane);
//    getClient().getActionKeyBindings().addKeyBindings(fAllScrollPane, ActionKeyGroup.ALL);
//
//    fChatTextPane = new MyTextPane(false);
//    fChatTextPane.addFocusListener(new MyFocusListener(fChatTextPane, false));
//    fChatScrollPane = new MyScrollPane(fChatTextPane);
//    getClient().getActionKeyBindings().addKeyBindings(fChatScrollPane, ActionKeyGroup.ALL);
//
//    fLogTextPane = new MyTextPane(true);
//    fLogTextPane.addFocusListener(new MyFocusListener(fLogTextPane, false));
//    fLogScrollPane = new MyScrollPane(fLogTextPane);
//    getClient().getActionKeyBindings().addKeyBindings(fLogScrollPane, ActionKeyGroup.ALL);
//    
//    fChatInputField = new JTextField(35);
//    fChatInputField.addFocusListener(new MyFocusListener(fChatInputField, true));
//    getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.PLAYER_ACTIONS);
//    getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.TURN_ACTIONS);
//
//    fChatInputField.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        String talk = fChatInputField.getText();
//        if (talk != null) {
//          talk = talk.trim();
//          if (talk.length() > 0) {
//            getClient().getCommunication().sendTalk(talk);
//          }
//        }
//        fChatInputField.setText("");
//      }
//    });
//    
//    fLogPanel = new JPanel();
//    fLogPanel.setLayout(new BorderLayout(0, 2));
//    fLogPanel.add(fLogScrollPane, BorderLayout.CENTER);
//
//    fChatWindow = new JFrame();
//    fChatWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//    fChatWindow.setLayout(new BorderLayout());
//    fChatWindow.add(fChatScrollPane, BorderLayout.CENTER);
//    fChatWindow.pack();
//    fChatWindow.setSize(200, 400);
//    fChatWindow.toFront();
//
//    fMinimumCommandNr = 0;
//    fReplayControl = new ReplayControl(getClient());
//
//  }
//
//  public JPanel getLogPanel() {
//    return fLogPanel;
//  }
//
//  public JFrame getChatWindow() {
//    return fChatWindow;
//  }
//
//  public void focusChat() {
//    fChatInputField.requestFocus();
//  }
//
//  public void focusLog() {
//    if (fReplayShown || ((fChatWindow != null) && fChatWindow.isVisible())) {
//      fLogTextPane.requestFocus();
//    } else {
//      fAllTextPane.requestFocus();
//    }
//  }
//
//  public void showChat() {
//    fReplayShown = false;
//    fChatWindow.setTitle("FantasyFootball Chat " + getClient().getLoginCoach());
//    if (IClientPropertyValue.SETTING_CHATLOG_EXTRA_CHAT.equals(getClient().getProperty(IClientProperty.SETTING_CHATLOG))) {
//      fLogPanel.removeAll();
//      fLogPanel.add(fLogScrollPane, BorderLayout.CENTER);
//      fLogPanel.validate();
//      fLogPanel.repaint();
//      fChatWindow.add(fChatInputField, BorderLayout.SOUTH);
//      fChatWindow.validate();
//      fChatWindow.setVisible(true);
//      fLogScrollPane.setScrollBarToMaximum();
//    } else {
//      fChatWindow.setVisible(false);
//      fChatWindow.remove(fChatInputField);
//      fChatWindow.validate();
//      fLogPanel.removeAll();
//      fLogPanel.add(fAllScrollPane, BorderLayout.CENTER);
//      fLogPanel.add(fChatInputField, BorderLayout.SOUTH);
//      fLogPanel.validate();
//      fLogPanel.repaint();
//      fAllScrollPane.setScrollBarToMaximum();
//    }
//    focusChat();
//  }
//  
//  public void showReplay() {
//    fReplayShown = true;
//    fLogPanel.removeAll();
//    fLogPanel.add(fLogScrollPane, BorderLayout.CENTER);
//    fLogPanel.add(fReplayControl, BorderLayout.SOUTH);
//    fLogPanel.validate();
//    fLogPanel.repaint();
//    focusLog();
//  }
//
//  public void invokeChatAreaUpdate(ParagraphStyle pTextIndent, TextStyle pStyle, ChatLogAppendMode pAppendMode, String pText) {
//    // SwingUtilities.invokeLater(new ChatAreaUpdate(pTextIndent, pStyle, pIsChat, pText));
//    append(pTextIndent, pStyle, pAppendMode, pText);
//  }
//
//  public JScrollPane getLogScrollPane() {
//    return fLogScrollPane;
//  }
//
//  public JScrollPane getChatScrollPane() {
//    return fChatScrollPane;
//  }
//
//  public JScrollPane getAllScrollPane() {
//    return fAllScrollPane;
//  }
//  
//  private ChatLogDocument getLogDocument() {
//    if (fLogDocument != null) {
//      return fLogDocument;
//    } else {
//      return (ChatLogDocument) fLogTextPane.getStyledDocument();
//    }
//  }
//  
//  private ChatLogDocument getAllDocument() {
//    if (fAllDocument != null) {
//      return fAllDocument;
//    } else {
//      return (ChatLogDocument) fAllTextPane.getStyledDocument();
//    }
//  }
//  
//  private ChatLogDocument getChatDocument() {
//    return (ChatLogDocument) fChatTextPane.getStyledDocument();
//  }
//  
//  public void detachLogDocuments() {
//    fLogDocument = new ChatLogDocument();
//    fAllDocument = new ChatLogDocument();
//    fCommandHighlightAreaByCommandNr.clear();
//  }
//
//  public void attachLogDocuments() {
//    if (fLogDocument != null) {
//      fLogTextPane.setDocument(fLogDocument);
//      fLogDocument = null;
//    }
//    if (fAllDocument != null) {
//      fAllTextPane.setDocument(fAllDocument);
//      fAllDocument = null;
//    }
//  }
//  
//
//  public void append(ParagraphStyle pTextIndent, TextStyle pStyle, ChatLogAppendMode pAppendMode, String pText) {
//    if (pAppendMode == ChatLogAppendMode.LOG) {
//      append(getLogDocument(), pTextIndent, pStyle, pText);
//    }
//    if ((pAppendMode == ChatLogAppendMode.CHAT) || (pAppendMode == ChatLogAppendMode.CHAT_ONLY)) {
//      append(getChatDocument(), pTextIndent, pStyle, pText);
//    }
//    if ((pAppendMode == ChatLogAppendMode.LOG) || (pAppendMode == ChatLogAppendMode.CHAT) || (pAppendMode == ChatLogAppendMode.MIXED_ONLY)) {
//      append(getAllDocument(), pTextIndent, pStyle, pText);
//    }
//  }
//
//  private void append(ChatLogDocument pDocument, ParagraphStyle pTextIndent, TextStyle pStyle, String pText) {
//
//    try {
//
//      if (pText != null) {
//
//        if (pStyle == null) {
//          pStyle = TextStyle.NONE;
//        }
//        if (pTextIndent == null) {
//          pTextIndent = ParagraphStyle.TEXT_INDENT_0;
//        }
//
//        pDocument.setParagraphAttributes(pDocument.getLength(), 1, pDocument.getStyle(pTextIndent.getName()), false);
//        pDocument.insertString(pDocument.getLength(), pText, pDocument.getStyle(pStyle.getName()));
//
//      } else {
//        pDocument.insertString(pDocument.getLength(), LINE_SEPARATOR, pDocument.getStyle(TextStyle.NONE.getName()));
//      }
//
//    } catch (BadLocationException ex) {
//      throw new FantasyFootballException(ex);
//    }
//
//  }
//
//  public FantasyFootballClient getClient() {
//    return fClient;
//  }
//
//  public JTextField getChatInputField() {
//    return fChatInputField;
//  }
//
//  public JComponent getLastFocusedComponent() {
//    return fLastFocusedComponent;
//  }
//  
//  public void markCommandBegin(int pCommandNr) {
//    fCurrentCommandHighlight = fCommandHighlightAreaByCommandNr.get(pCommandNr);
//    if (fCurrentCommandHighlight == null) {
//      fCurrentCommandHighlight = new CommandHighlightArea(pCommandNr);
//    }
//    int logOffset = getLogDocument().getEndPosition().getOffset() - 1;
//    fCurrentCommandHighlight.setStartPosition(logOffset);
//  }
//  
//  public void markCommandEnd(int pCommandNr) {
//    if (fCurrentCommandHighlight.getCommandNr() == pCommandNr) {
//      if (fMinimumCommandNr > pCommandNr) {
//        fMinimumCommandNr = pCommandNr;
//      }
//      int logOffset = getLogDocument().getEndPosition().getOffset() - 1;
//      fCurrentCommandHighlight.setEndPosition(logOffset);
//      fCommandHighlightAreaByCommandNr.put(fCurrentCommandHighlight.getCommandNr(), fCurrentCommandHighlight);
//    }
//  }
//    
//  public boolean isReplayShown() {
//    return fReplayShown;
//  }
//  
//  public ReplayControl getReplayControl() {
//    return fReplayControl;
//  }
//  
//  public boolean hasCommandHighlight(int pCommandNr) {
//    CommandHighlightArea highlightArea = fCommandHighlightAreaByCommandNr.get(pCommandNr);
//    return ((highlightArea != null) && ((highlightArea.getEndPosition() - highlightArea.getStartPosition()) > 0));
//  }
//  
//  public boolean highlightCommand(int pCommandNr, boolean pShowEnd) {
//    CommandHighlightArea highlightArea = fCommandHighlightAreaByCommandNr.get(pCommandNr);
//    boolean highlightShown = ((highlightArea != null) && ((highlightArea.getEndPosition() - highlightArea.getStartPosition()) > 0));
//    if (highlightShown) {
//      try {
//        ((MyHighlighter) fLogTextPane.getHighlighter()).changeHighlight(highlightArea.getStartPosition(), highlightArea.getEndPosition());
//        if (pShowEnd) {
//          fLogTextPane.setCaretPosition(highlightArea.getEndPosition());
//        } else {
//          fLogTextPane.setCaretPosition(Math.max(highlightArea.getStartPosition() - 1, 0));
//        }
//      } catch (BadLocationException e) {
//      }
//    }
//    return highlightShown;
//  }
//  
//  public int findCommandNr(int pPosition) {
//    int commandNr = -1;
//    CommandHighlightArea[] highlights = fCommandHighlightAreaByCommandNr.values().toArray(new CommandHighlightArea[fCommandHighlightAreaByCommandNr.size()]);
//    for (int i = 0; i < highlights.length; i++) {
//      if ((pPosition >= highlights[i].getStartPosition()) && (pPosition <= highlights[i].getEndPosition())) {
//        commandNr = highlights[i].getCommandNr();
//        break;
//      }
//    }
//    return commandNr;
//  }
//  
//  public void hideHighlight() {
//    try {
//      ((MyHighlighter) fLogTextPane.getHighlighter()).changeHighlight(0, 0);
//    } catch (BadLocationException e) {
//    }
//  }
//  
//  public int getMinimumCommandNr() {
//    return fMinimumCommandNr;
//  }

}
