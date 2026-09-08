package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.ui.chat.ChatSegment;
import com.fumbbl.ffb.client.util.UiDispatcher;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Kalimar
 */
public class ChatLogTextPane extends JTextPane {

	private ChatLogDocument fChatLogDocument;
	private IReplayMouseListener fReplayMouseListener;
	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;
	private final UiDispatcher uiDispatcher = new UiDispatcher();

	public ChatLogTextPane(StyleProvider styleProvider, DimensionProvider dimensionProvider) {
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;
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

		TextStyle style = (pStyle != null) ? pStyle : TextStyle.NONE;
		ParagraphStyle textIndent = (pTextIndent != null) ? pTextIndent : ParagraphStyle.INDENT_0;

		Runnable runnable = () -> {
			try {
				if (pText != null) {
					fChatLogDocument.setParagraphAttributes(fChatLogDocument.getLength(), 1,
						fChatLogDocument.getStyle(textIndent.getName()), false);
					fChatLogDocument.insertString(fChatLogDocument.getLength(), pText,
						fChatLogDocument.getStyle(style.getName()));
				} else {
					fChatLogDocument.insertString(fChatLogDocument.getLength(), ChatLogDocument.LINE_SEPARATOR,
						fChatLogDocument.getStyle(TextStyle.NONE.getName()));
				}
			} catch (BadLocationException ex) {
				throw new FantasyFootballException(ex);
			}
		};

		if (isDocumentDetached()) {
			// the document is not shown by any component, so it can be filled without involving the event dispatch
			// thread, this avoids tens of thousands of round trips while a replay is initialized
			synchronized (fChatLogDocument) {
				runnable.run();
			}
		} else {
			uiDispatcher.runOnUiThread(runnable);
		}

	}

	private boolean isDocumentDetached() {
		return getDocument() != fChatLogDocument;
	}

	public void update() {
		fChatLogDocument.setStyles();
	}

	/**
	 * Batch insert of chat segments.
	 *
	 * Performs all inserts in one EDT run, avoiding overhead from many
	 * small append calls (e.g. 150+ segments in stress test).
	 */
	public void appendBatch(List<ChatSegment> segments, ParagraphStyle paragraphStyle) {
		Runnable runnable = () -> {
			try {

				int startOffset = fChatLogDocument.getParagraphElement(fChatLogDocument.getLength()).getStartOffset();

				for (ChatSegment segment : segments) {
					if (segment.icon != null) {
						setCaretPosition(fChatLogDocument.getLength());
						insertIcon(new OffsetIcon(segment.icon.getImage(), segment.offset));
					} else if (segment.text != null) {
						TextStyle style = (segment.style == null ? TextStyle.NONE : segment.style);
						fChatLogDocument.insertString(fChatLogDocument.getLength(), segment.text,
							fChatLogDocument.getStyle(style.getName()));
					} else {
						fChatLogDocument.insertString(fChatLogDocument.getLength(), ChatLogDocument.LINE_SEPARATOR,
							fChatLogDocument.getStyle(TextStyle.NONE.getName()));
					}
				}

				int endOffset = fChatLogDocument.getParagraphElement(startOffset).getEndOffset();
				fChatLogDocument.setParagraphAttributes(startOffset, endOffset - startOffset,
					fChatLogDocument.getStyle(paragraphStyle.getName()), false);

			} catch (BadLocationException ex) {
				throw new FantasyFootballException(ex);
			}
		};

		uiDispatcher.runOnUiThread(runnable);
	}

}
