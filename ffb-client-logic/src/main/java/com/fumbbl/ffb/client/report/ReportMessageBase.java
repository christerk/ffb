package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;

public abstract class ReportMessageBase<T extends IReport> implements IKeyedItem {
	
	protected StatusReport statusReport;
	protected Game game;

	public void setStatusReport(StatusReport statusReport) {
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

	protected void printTeamName(@SuppressWarnings("SameParameterValue") boolean pBold, String pTeamId) {
		statusReport.printTeamName(game, pBold, pTeamId);
	}

	protected Direction mapToLocal(Direction direction) {
		PitchDimensionProvider dimensionProvider = statusReport.getClient().getUserInterface().getPitchDimensionProvider();
		return dimensionProvider.mapToLocal(direction);
	}

	@SuppressWarnings("unchecked")
	public void renderMessage(Game game, IReport report) {
		this.game = game;
		this.render((T) report);
	}

	protected abstract void render(T report);
}
