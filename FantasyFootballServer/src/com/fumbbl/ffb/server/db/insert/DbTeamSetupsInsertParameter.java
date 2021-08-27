package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TeamSetup;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbTeamSetupsInsertParameter extends DefaultDbUpdateParameter {

	private String fTeamId;
	private String fName;
	private byte[] fPlayerNumbers;
	private byte[] fXCoordinates;
	private byte[] fYCoordinates;

	public DbTeamSetupsInsertParameter(TeamSetup pTeamSetup) {
		fTeamId = pTeamSetup.getTeamId();
		fName = pTeamSetup.getName();
		fPlayerNumbers = new byte[11];
		fXCoordinates = new byte[11];
		fYCoordinates = new byte[11];
		int[] playerNumbers = pTeamSetup.getPlayerNumbers();
		FieldCoordinate[] coordinates = pTeamSetup.getCoordinates();
		for (int i = 0; i < fPlayerNumbers.length; i++) {
			if ((i < playerNumbers.length) && (i < coordinates.length)) {
				fPlayerNumbers[i] = (byte) playerNumbers[i];
				fXCoordinates[i] = (byte) coordinates[i].getX();
				fYCoordinates[i] = (byte) coordinates[i].getY();
			} else {
				fPlayerNumbers[i] = (byte) 0;
				fXCoordinates[i] = (byte) -1;
				fYCoordinates[i] = (byte) -1;
			}
		}
	}

	public String getTeamId() {
		return fTeamId;
	}

	public String getName() {
		return fName;
	}

	public byte[] getPlayerNumbers() {
		return fPlayerNumbers;
	}

	public byte[] getXCoordinates() {
		return fXCoordinates;
	}

	public byte[] getYCoordinates() {
		return fYCoordinates;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.TEAM_SETUPS_INSERT);
	}

}
