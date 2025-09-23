package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.ui.chat.Autocomplete;
import com.fumbbl.ffb.client.ui.chat.ChatSegment;
import com.fumbbl.ffb.client.ui.chat.MessageParser;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.client.ui.swing.WrappingEditorKit;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
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
	private Autocomplete autocomplete;
	private JPanel inputPanel;
	private final IconCache iconCache;
	private final ChatButtonComponent fChatButtonComponent;

	public ChatComponent(FantasyFootballClient pClient, UiDimensionProvider dimensionProvider, StyleProvider styleProvider, IconCache iconCache) {

		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
		this.iconCache = iconCache;
		fInputLog = new LinkedList<>();
		fInputLogPosition = -1;

		fChatTextPane = new ChatLogTextPane(styleProvider, dimensionProvider);
		fChatScrollPane = new ChatLogScrollPane(fChatTextPane);
		getClient().getActionKeyBindings().addKeyBindings(fChatScrollPane, ActionKeyGroup.ALL);

		fChatInputField = new JTextField(dimensionProvider, 35);
		fChatInputField.setFocusTraversalKeysEnabled(false);
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.PLAYER_ACTIONS);
		getClient().getActionKeyBindings().addKeyBindings(fChatInputField, ActionKeyGroup.TURN_ACTIONS);

		fChatInputField.addActionListener(e -> {
			if (autocomplete.isVisible()) {
				autocomplete.commit();
				return;
			}
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
			public void keyPressed(KeyEvent pKeyEvent) {
				if (!autocomplete.isVisible()) {
					return;
				}
				int keyCode = pKeyEvent.getKeyCode();
				if (keyCode == KeyEvent.VK_DOWN) {
					autocomplete.moveSelection(1);
					pKeyEvent.consume();
				}
				if (keyCode == KeyEvent.VK_UP) {
					autocomplete.moveSelection(-1);
					pKeyEvent.consume();
				}
				if (keyCode == KeyEvent.VK_ESCAPE) {
					autocomplete.hide();
					pKeyEvent.consume();
				}
				if (keyCode == KeyEvent.VK_TAB) {
					autocomplete.commit();
					pKeyEvent.consume();
				}
			}
			public void keyReleased(KeyEvent pKeyEvent) {
				super.keyReleased(pKeyEvent);
				if (pKeyEvent.isConsumed() || autocomplete.isVisible()) {
					return;
				}
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
		
		fChatButtonComponent = new ChatButtonComponent(fClient, fChatInputField, dimensionProvider, iconCache);
		inputPanel = new JPanel(new BorderLayout(0, 0));
		inputPanel.setOpaque(false);
		inputPanel.add(buildChatInputPanel(), BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);


		fChatTextPane.addMouseMotionListener(this);
		fChatScrollPane.addMouseMotionListener(this);
		fChatInputField.addMouseMotionListener(this);

		fReplayShown = false;
		fReplayControl = new ReplayControl(getClient(), dimensionProvider);
		autocomplete = new Autocomplete(fChatInputField, getClient(), dimensionProvider, iconCache);
		
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

		// maybe a hacky solution but couldnt get fChatInputField to resize after client rescale.
		fChatInputField.setFont(UIManager.getFont("TextField.font"));
		dimensionProvider.scaleFont(fChatInputField);
	  
		fChatTextPane.setEditorKit(new WrappingEditorKit());
		fChatTextPane.attachDocument();
		fChatTextPane.update();

		autocomplete.refresh();
		
		fChatButtonComponent.initLayout(fChatTextPane);

	}

	public void append(TextStyle pStyle, String pText) {
		fChatTextPane.append(null, pStyle, pText);
		fChatTextPane.append(null, null, null);
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

	public void parseAndAppend(TextStyle style, TextStyle prefixStyle, String prefix, String message) {
		try {
			List<ChatSegment> chatSegments = new ArrayList<>();
			
			if (StringTool.isProvided(prefix)) {
				chatSegments.add(new ChatSegment(prefixStyle, prefix));
			}

			chatSegments.addAll(MessageParser.parse(message, getClient().getParameters().getCoach(), style,	iconCache, dimensionProvider));
			
			chatSegments.add(ChatSegment.newline());
			fChatTextPane.appendBatch(chatSegments, ParagraphStyle.CHAT_BODY);
			
		} catch (Exception e) {
			throw new FantasyFootballException(e);
		}
	}

	// Wrap chat input + emoji button in a container panel.
	// This preserves the original text field border around both,
	// while letting the button appear "inside" the field. Adding
	// the button directly would draw a second border and look wrong.
	private JComponent buildChatInputPanel() {
		final int pad = dimensionProvider.scale(2);
		Border original = fChatInputField.getBorder();

		JPanel outer = new JPanel(new BorderLayout(0, 0));
		outer.setBorder(original);
		outer.setBackground(fChatInputField.getBackground());

		fChatInputField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, pad));
		outer.add(fChatInputField, BorderLayout.CENTER);

		JPanel east = new JPanel(new GridBagLayout());
		east.setOpaque(false);
		east.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, pad));
		east.add(fChatButtonComponent);
		outer.add(east, BorderLayout.EAST);

		return outer;
	}

}