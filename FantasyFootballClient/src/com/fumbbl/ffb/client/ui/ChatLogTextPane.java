package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.TextStyle;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kalimar
 */
public class ChatLogTextPane extends JTextPane {

	private ChatLogDocument fChatLogDocument;
	private IReplayMouseListener fReplayMouseListener;
	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;
	private final boolean waitForDispatch;

	public ChatLogTextPane(StyleProvider styleProvider, DimensionProvider dimensionProvider, ClientMode clientMode) {
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;
		this.waitForDispatch = clientMode == ClientMode.REPLAY;
		setEditable(false);
		((DefaultCaret) getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		detachDocument();
		attachDocument();
	}

	public ChatLogDocument getChatLogDocument() {
		return fChatLogDocument;
	}

	public void addReplayMouseListener(IReplayMouseListener pReplayMouseListener) {
		fReplayMouseListener = pReplayMouseListener;
	}

	public void removeReplayMouseListener() {
		fReplayMouseListener = null;
	}

	protected void processMouseEvent(MouseEvent pMouseEvent) {
		if (fReplayMouseListener != null) {
			if (MouseEvent.MOUSE_PRESSED == pMouseEvent.getID()) {
				int position = viewToModel(pMouseEvent.getPoint());
				fReplayMouseListener.mousePressedForReplay(position);
			}
		} else {
			super.processMouseEvent(pMouseEvent);
		}
	}

	public void detachDocument() {
		fChatLogDocument = new ChatLogDocument(styleProvider, dimensionProvider);
	}

	public void attachDocument() {
		setDocument(fChatLogDocument);
	}

	public void append(ParagraphStyle pTextIndent, TextStyle pStyle, String pText) {

		try {
			Runnable runnable;
			if (pText != null) {

				if (pStyle == null) {
					pStyle = TextStyle.NONE;
				}
				if (pTextIndent == null) {
					pTextIndent = ParagraphStyle.INDENT_0;
				}

				fChatLogDocument.setParagraphAttributes(fChatLogDocument.getLength(), 1,
					fChatLogDocument.getStyle(pTextIndent.getName()), false);
				String name = pStyle.getName();

				runnable = () -> {
					try {
						fChatLogDocument.insertString(fChatLogDocument.getLength(), pText, fChatLogDocument.getStyle(name));
					} catch (BadLocationException ex) {
						throw new FantasyFootballException(ex);
					}
				};
			} else {
				runnable = () -> {
					try {
						fChatLogDocument.insertString(fChatLogDocument.getLength(), ChatLogDocument.LINE_SEPARATOR,
							fChatLogDocument.getStyle(TextStyle.NONE.getName()));
					} catch (BadLocationException ex) {
						throw new FantasyFootballException(ex);
					}
				};
			}

			if (SwingUtilities.isEventDispatchThread()) {
				runnable.run();
			} else if (waitForDispatch) {
				SwingUtilities.invokeAndWait(runnable);
			} else {
				SwingUtilities.invokeLater(runnable);
			}

		} catch (InterruptedException | InvocationTargetException e) {
			throw new FantasyFootballException(e);
		}

	}

	public void update() {
		fChatLogDocument.setStyles();
	}

}
