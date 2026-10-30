package com.fumbbl.ffb.server.db;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class DbTransaction implements IDbUpdateParameter {

	private int fUpdatedRows;
	private final List<IDbUpdateParameter> fDbUpdateParameters;
	private Runnable onCompletion;

	public DbTransaction() {
		fDbUpdateParameters = new ArrayList<>();
	}

	public void add(IDbUpdateParameter pDbUpdateParameter) {
		if (pDbUpdateParameter != null) {
			fDbUpdateParameters.add(pDbUpdateParameter);
		}
	}

	public void add(IDbUpdateParameterList pDbUpdateParameterList) {
		if (pDbUpdateParameterList != null) {
			for (IDbUpdateParameter parameter : pDbUpdateParameterList.getParameters()) {
				fDbUpdateParameters.add(parameter);
			}
		}
	}

	/**
	 * Sets an action that is run on the db updater thread after this transaction has been committed or rolled back.
	 */
	public void setOnCompletion(Runnable onCompletion) {
		this.onCompletion = onCompletion;
	}

	public IDbUpdateParameter[] getDbUpdateParameters() {
		return fDbUpdateParameters.toArray(new IDbUpdateParameter[fDbUpdateParameters.size()]);
	}

	public int size() {
		return fDbUpdateParameters.size();
	}

	public void executeUpdate(FantasyFootballServer pServer) {
		fUpdatedRows = 0;
		boolean doCommit = true;
		for (int i = 0; i < fDbUpdateParameters.size(); i++) {
			IDbUpdateParameter dbUpdateParameter = fDbUpdateParameters.get(i);
			try {
				dbUpdateParameter.executeUpdate(pServer);
				fUpdatedRows += dbUpdateParameter.getUpdatedRows();
			} catch (SQLException pSqlException) {
				doCommit = false;
				pServer.getDebugLog().logWithOutGameId(pSqlException);
				pServer.getDebugLog().logWithOutGameId(IServerLogLevel.ERROR, "*** DbTransaction Content ***");
				for (int j = 0; j <= i; j++) {
					try {
						pServer.getDebugLog().logWithOutGameId(IServerLogLevel.ERROR,
							fDbUpdateParameters.get(j).getDbUpdateStatement(pServer).toString(fDbUpdateParameters.get(j)));
					} catch (SQLException pSqlException2) {
						// just don't log
					}
				}
				break;
			}
		}
		try {
			if (doCommit) {
				pServer.getDbUpdateFactory().commit();
			} else {
				fUpdatedRows = 0;
				pServer.getDbUpdateFactory().rollback();
			}
		} catch (SQLException pCommitException) {
			pServer.getDebugLog().logWithOutGameId(pCommitException);
		}
		if (onCompletion != null) {
			try {
				onCompletion.run();
			} catch (Throwable completionException) {
				// also catch errors, an uncaught one would silently end the db updater thread
				pServer.getDebugLog().logWithOutGameId(completionException);
			}
		}
	}

	public int getUpdatedRows() {
		return fUpdatedRows;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return null;
	}

	public IDbUpdateParameter[] getParameters() {
		return new DbTransaction[] { this };
	}

}
