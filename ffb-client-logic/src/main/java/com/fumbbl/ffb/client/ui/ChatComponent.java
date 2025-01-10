package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.ui.swing.JTextField;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ChatComponent extends JPanel implements MouseMotionListener {

	private static final int _MAX_CHAT_LENGTH = 512;
	private static final int _MAX_INPUT_LOG_SIZE = 100;

	private final ChatLogScrollPane fChatScrollPane;
	private final ChatLogTextPane fChatTextPane;
	private final JTextField fChatInputField;
	private final ReplayControl fReplayControl;
	private boolean fReplayShown;

	private final List<String> fInputLog;
	private int fInputLogPosition;
	private final DimensionProvider dimensionProvider;
	private final StyleProvider styleProvider;
	private final FantasyFootballClient fClient;

	public ChatComponent(FantasyFootballClient pClient, UiDimensionProvider dimensionProvider, StyleProvider styleProvider) {

		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
		fInputLog = new LinkedList<>();
		fInputLogPosition = -1;

		fChatTextPane = new ChatLogTextPane(styleProvider, dimensionProvider);
		fChatScrollPane = new ChatLogScrollPane(fChatTextPane);
		// TODO remove after moving component to UI element
		getClient().getActionKeyBindings().addKeyBindings(fChatScrollPane, ActionKeyGroup.ALL);

		fChatInputField = new JTextField(dimensionProvider, 35);
		// TODO remove after moving component to UI element
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.PLAYER_ACTIONS);
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.TURN_ACTIONS);

		fChatInputField.addActionListener(e -> {
			String talk = fChatInputField.getText();
			if (talk != null) {
				talk = talk.trim();
				if (talk.length() > _MAX_CHAT_LENGTH) {
					talk = talk.substring(0, _MAX_CHAT_LENGTH);
				}
				if (!talk.isEmpty()) {
					getClient().getCommunication().sendTalk(talk);
					fInputLog.add(talk);
					if (fInputLog.size() > _MAX_INPUT_LOG_SIZE) {
						fInputLog.remove(0);
					}
					fInputLogPosition = fInputLog.size();
				}
			}
			fChatInputField.setText("");
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

		fChatTextPane.addMouseMotionListener(this);
		fChatScrollPane.addMouseMotionListener(this);
		fChatInputField.addMouseMotionListener(this);

		fReplayShown = false;
		fReplayControl = new ReplayControl(getClient(), dimensionProvider);

	}

	public void initLayout() {
		Dimension size = dimensionProvider.dimension(Component.CHAT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		fReplayControl.initLayout();
		setBackground(styleProvider.getChatBackground());
		fChatTextPane.setBackground(styleProvider.getChatBackground());
		fChatScrollPane.setBackground(styleProvider.getChatBackground());
		fChatInputField.setBackground(styleProvider.getChatBackground());
		fChatInputField.setForeground(styleProvider.getInput());
		fChatTextPane.update();
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
