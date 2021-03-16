package com.balancedbytes.games.ffb.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.balancedbytes.games.ffb.client.ActionKeyGroup;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.ReplayControl;
import com.balancedbytes.games.ffb.client.TextStyle;

/**
 * 
 * @author Kalimar
 */
public class ChatComponent extends JPanel implements MouseMotionListener {

	public static final int WIDTH = 389;
	public static final int HEIGHT = 226;

	private static final int _MAX_CHAT_LENGTH = 512;
	private static final int _MAX_INPUT_LOG_SIZE = 100;

	private ChatLogScrollPane fChatScrollPane;
	private ChatLogTextPane fChatTextPane;
	private JTextField fChatInputField;
	private ReplayControl fReplayControl;
	private boolean fReplayShown;

	private List<String> fInputLog;
	private int fInputLogPosition;

	private FantasyFootballClient fClient;

	public ChatComponent(FantasyFootballClient pClient) {

		fClient = pClient;
		fInputLog = new LinkedList<String>();
		fInputLogPosition = -1;

		fChatTextPane = new ChatLogTextPane();
		fChatScrollPane = new ChatLogScrollPane(fChatTextPane);
		getClient().getActionKeyBindings().addKeyBindings(fChatScrollPane, ActionKeyGroup.ALL);

		fChatInputField = new JTextField(35);
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.PLAYER_ACTIONS);
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.TURN_ACTIONS);

		fChatInputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String talk = fChatInputField.getText();
				if (talk != null) {
					talk = talk.trim();
					if (talk.length() > _MAX_CHAT_LENGTH) {
						talk = talk.substring(0, _MAX_CHAT_LENGTH);
					}
					if (talk.length() > 0) {
						getClient().getCommunication().sendTalk(talk);
						fInputLog.add(talk);
						if (fInputLog.size() > _MAX_INPUT_LOG_SIZE) {
							fInputLog.remove(0);
						}
						fInputLogPosition = fInputLog.size();
					}
				}
				fChatInputField.setText("");
			}
		});

		fChatInputField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent pKeyEvent) {
				super.keyReleased(pKeyEvent);
				if (pKeyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
					if (fInputLogPosition > 0) {
						fInputLogPosition--;
					}
					if ((fInputLogPosition >= 0) && (fInputLogPosition < fInputLog.size())) {
						fChatInputField.setText(fInputLog.get(fInputLogPosition));
					}
				}
				if (pKeyEvent.getKeyCode() == KeyEvent.VK_UP) {
					if (fInputLogPosition < fInputLog.size() - 1) {
						fInputLogPosition++;
					}
					if ((fInputLogPosition >= 0) && (fInputLogPosition < fInputLog.size())) {
						fChatInputField.setText(fInputLog.get(fInputLogPosition));
					}
				}
			}
		});

		setLayout(new BorderLayout(0, 1));
		add(fChatScrollPane, BorderLayout.CENTER);
		add(fChatInputField, BorderLayout.SOUTH);

		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		fChatTextPane.addMouseMotionListener(this);
		fChatScrollPane.addMouseMotionListener(this);
		fChatInputField.addMouseMotionListener(this);

		fReplayShown = false;
		fReplayControl = new ReplayControl(getClient());

	}

	public void append(ParagraphStyle pTextIndent, TextStyle pStyle, String pText) {
		fChatTextPane.append(pTextIndent, pStyle, pText);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void showReplay(boolean pShowReplay) {
		removeAll();
		if (pShowReplay) {
			add(fReplayControl, BorderLayout.NORTH);
		}
		add(fChatScrollPane, BorderLayout.CENTER);
		add(fChatInputField, BorderLayout.SOUTH);
		revalidate();
		repaint();
		fReplayShown = pShowReplay;
	}

	public boolean isReplayShown() {
		return fReplayShown;
	}

	public ReplayControl getReplayControl() {
		return fReplayControl;
	}

	public boolean hasChatInputFocus() {
		return fChatInputField.hasFocus();
	}

	public void requestChatInputFocus() {
		fChatInputField.requestFocus();
	}

}
