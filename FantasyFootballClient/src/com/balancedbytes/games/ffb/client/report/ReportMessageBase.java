package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportId;

public abstract class ReportMessageBase<T extends IReport> implements IKeyedItem {
	
	protected StatusReport statusReport;
	protected Game game;

	public ReportMessageBase(StatusReport statusReport) {
		this.statusReport = statusReport;
	}
	
	@Override
	public String getKey() {
		ReportId reportId = this.getClass().getAnnotation(ReportMessageType.class).value();
		return reportId.getKey();
	}
	
	public int getIndent() {
		return statusReport.getIndent();
	}

	public void setIndent(int indent) {
		statusReport.setIndent(indent);
	}
	
	protected void print(int pIndent, TextStyle pTextStyle, String pText) {
		statusReport.print(pIndent, pTextStyle, pText);
	}

	protected void print(int pIndent, String pText) {
		statusReport.print(pIndent, pText);
	}

	protected void print(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
		statusReport.print(pParagraphStyle, pTextStyle, pText);
	}

	protected void println(int pIndent, TextStyle pTextStyle, String pText) {
		statusReport.println(pIndent, pTextStyle, pText);
	}

	protected void println(int pIndent, String pText) {
		statusReport.println(pIndent, pText);
	}

	protected void println() {
		statusReport.println();
	}

	protected void println(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
		statusReport.println(pParagraphStyle, pTextStyle, pText);
	}

	protected void print(int pIndent, boolean pBold, Player<?> pPlayer) {
		statusReport.print(pIndent, pBold, pPlayer);
	}

	protected void printTeamName(Game pGame, boolean pBold, String pTeamId) {
		statusReport.printTeamName(pGame, pBold, pTeamId);
	}	
	
	@SuppressWarnings("unchecked")
	public void renderMessage(Game game, IReport report) {
		this.game = game;
			this.render((T) report);
	}
	protected abstract void render(T report);
}
