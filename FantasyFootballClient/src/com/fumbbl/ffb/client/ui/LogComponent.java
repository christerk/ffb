package com.fumbbl.ffb.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import com.fumbbl.ffb.client.ActionKeyGroup;
import com.fumbbl.ffb.client.ClientReplayer;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;

/**
 *
 * @author Kalimar
 */
public class LogComponent extends JPanel implements MouseMotionListener, IReplayMouseListener {

	public static final int WIDTH = 389;
	public static final int HEIGHT = 226;

	private ChatLogScrollPane fLogScrollPane;
	private ChatLogTextPane fLogTextPane;

	private Map<Integer, CommandHighlightArea> fCommandHighlightAreaByCommandNr;
	private CommandHighlightArea fCurrentCommandHighlight;
	private int fMinimumCommandNr;

	private FantasyFootballClient fClient;

	public LogComponent(FantasyFootballClient pClient) {
		fClient = pClient;
		fLogTextPane = new ChatLogTextPane();
		fLogScrollPane = new ChatLogScrollPane(fLogTextPane);
		getClient().getActionKeyBindings().addKeyBindings(fLogScrollPane, ActionKeyGroup.ALL);
		setLayout(new BorderLayout());
		add(fLogScrollPane, BorderLayout.CENTER);
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		fLogTextPane.setHighlighter(new CommandHighlighter());
		fLogTextPane.addMouseMotionListener(this);
		fLogScrollPane.addMouseMotionListener(this);
		fCommandHighlightAreaByCommandNr = new HashMap<>();
	}

	public void append(ParagraphStyle pTextIndent, TextStyle pStyle, String pText) {
		fLogTextPane.append(pTextIndent, pStyle, pText);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void markCommandBegin(int pCommandNr) {
		fCurrentCommandHighlight = fCommandHighlightAreaByCommandNr.get(pCommandNr);
		if (fCurrentCommandHighlight == null) {
			fCurrentCommandHighlight = new CommandHighlightArea(pCommandNr);
		}
		int logOffset = fLogTextPane.getChatLogDocument().getEndPosition().getOffset() - 1;
		fCurrentCommandHighlight.setStartPosition(logOffset);
	}

	public void markCommandEnd(int pCommandNr) {
		if (fCurrentCommandHighlight.getCommandNr() == pCommandNr) {
			if (fMinimumCommandNr > pCommandNr) {
				fMinimumCommandNr = pCommandNr;
			}
			int logOffset = fLogTextPane.getChatLogDocument().getEndPosition().getOffset() - 1;
			fCurrentCommandHighlight.setEndPosition(logOffset);
			fCommandHighlightAreaByCommandNr.put(fCurrentCommandHighlight.getCommandNr(), fCurrentCommandHighlight);
		}
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void detachLogDocument() {
		fLogTextPane.detachDocument();
		fCommandHighlightAreaByCommandNr.clear();
	}

	public void attachLogDocument() {
		fLogTextPane.attachDocument();
	}

	public boolean hasCommandHighlight(int pCommandNr) {
		CommandHighlightArea highlightArea = fCommandHighlightAreaByCommandNr.get(pCommandNr);
		return ((highlightArea != null) && ((highlightArea.getEndPosition() - highlightArea.getStartPosition()) > 0));
	}

	public boolean highlightCommand(int pCommandNr, boolean pShowEnd) {
		CommandHighlightArea highlightArea = fCommandHighlightAreaByCommandNr.get(pCommandNr);
		boolean highlightShown = ((highlightArea != null)
				&& ((highlightArea.getEndPosition() - highlightArea.getStartPosition()) > 0));
		if (highlightShown) {
			try {
				((CommandHighlighter) fLogTextPane.getHighlighter()).changeHighlight(highlightArea.getStartPosition(),
						highlightArea.getEndPosition());
				if (pShowEnd) {
					fLogTextPane.setCaretPosition(highlightArea.getEndPosition());
				} else {
					fLogTextPane.setCaretPosition(Math.max(highlightArea.getStartPosition() - 1, 0));
				}
			} catch (BadLocationException e) {
			}
		}
		return highlightShown;
	}

	public int findCommandNr(int pPosition) {
		int commandNr = -1;
		CommandHighlightArea[] highlights = fCommandHighlightAreaByCommandNr.values()
				.toArray(new CommandHighlightArea[fCommandHighlightAreaByCommandNr.size()]);
		for (int i = 0; i < highlights.length; i++) {
			if ((pPosition >= highlights[i].getStartPosition()) && (pPosition <= highlights[i].getEndPosition())) {
				commandNr = highlights[i].getCommandNr();
				break;
			}
		}
		return commandNr;
	}

	public void hideHighlight() {
		try {
			((CommandHighlighter) fLogTextPane.getHighlighter()).changeHighlight(0, 0);
		} catch (BadLocationException e) {
		}
	}

	public int getMinimumCommandNr() {
		return fMinimumCommandNr;
	}

	public void mousePressedForReplay(int pPosition) {
		ClientReplayer replayer = getClient().getReplayer();
		int commandNr = findCommandNr(pPosition);
		if (commandNr > 0) {
			replayer.replayToCommand(commandNr);
		}
	}

	public void enableReplay(boolean pEnabled) {
		if (pEnabled) {
			fLogTextPane.addReplayMouseListener(this);
		} else {
			fLogTextPane.removeReplayMouseListener();
		}
	}

	public ChatLogScrollPane getLogScrollPane() {
		return fLogScrollPane;
	}

}
