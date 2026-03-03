package com.fumbbl.ffb.server.admin;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.ArrayTool;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Kalimar
 */
public class UtilBackup {

	private static File findBackupFile(FantasyFootballServer pServer, long pGameId) {
		if ((pServer == null) || (pGameId <= 0)) {
			return null;
		}
		String gameIdString = Long.toString(pGameId);
		String pathName = pServer.getProperty(IServerProperty.BACKUP_DIR) + "/" +
			calculateFolderPathForGame(pServer, gameIdString);
		return new File(pathName);
	}

	public static String calculateFolderPathForGame(FantasyFootballServer pServer, String gameIdString) {
		StringBuilder pathName = new StringBuilder();
		int index = 0;
		for (int i = 7; i > 3; i--) {
			if (gameIdString.length() < i) {
				pathName.append("0");
			} else {
				pathName.append(gameIdString.charAt(index++));
			}
			pathName.append("/");
		}
		pathName.append(gameIdString).append(".").append(pServer.getProperty(IServerProperty.BACKUP_EXTENSION));
		return pathName.toString();
	}

	public static boolean save(GameState pGameState) {
		if (pGameState == null) {
			return false;
		}
		File backupFile = findBackupFile(pGameState.getServer(), pGameState.getId());
		//noinspection ResultOfMethodCallIgnored
		backupFile.getParentFile().mkdirs();
		try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(backupFile.toPath()))) {
			out.write(UtilJson.gzip(pGameState.toJsonValue()));
		} catch (IOException pIoException) {
			pGameState.getServer().getDebugLog().log(pGameState.getId(), pIoException);
			return false;
		}
		// nothing to be done here
		return true;
	}

	public static byte[] loadAsGzip(FantasyFootballServer pServer, long pGameId) {
		if ((pServer == null) || (pGameId <= 0)) {
			return null;
		}
		File backupFile = findBackupFile(pServer, pGameId);
		if (!backupFile.exists()) {
			return null;
		}
		byte[] gzippedJson = new byte[(int) backupFile.length()];
		try (DataInputStream in = new DataInputStream(Files.newInputStream(backupFile.toPath()))) {
			in.readFully(gzippedJson);
		} catch (IOException pIoException) {
			pServer.getDebugLog().log(pGameId, pIoException);
			return null;
		}
		// nothing to be done here
		return gzippedJson;
	}

	private static GameState load(FantasyFootballServer pServer, long pGameId) {
		byte[] gzippedJson = loadAsGzip(pServer, pGameId);
		if (ArrayTool.isProvided(gzippedJson)) {
			JsonValue jsonValue = null;
			try {
				jsonValue = UtilJson.gunzip(gzippedJson);
			} catch (IOException pIoException) {
				pServer.getDebugLog().log(pGameId, pIoException);
			}
			GameState gameState;
			if (jsonValue != null) {
				gameState = new GameState(pServer);
				gameState.initFrom(gameState.getGame().getRules(), jsonValue);
				return gameState;
			}

		}
		return null;
	}

	public static GameState loadGameState(long gameId, FantasyFootballServer server) {
		GameState gameState = UtilBackup.load(server, gameId);
		if (gameState != null) {
			server.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from file system.");
		}
		if (gameState == null) {
			// fallback: try to load gameState from db
			gameState = server.getGameCache().queryFromDb(gameId);
			if (gameState != null) {
				server.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from database.");
			}
		}
		if ((gameState == null)) {
			gameState = loadFromS3(gameId, server);
			if (gameState != null) {
				server.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from s3 bucket.");
			}
		}
		return gameState;
	}


	private static GameState loadFromS3(long gameId, FantasyFootballServer server) {
		String basePath = server.getProperty(IServerProperty.BACKUP_S3_BASE_PATH);
		if (!basePath.endsWith("/")) {
			basePath += "/";
		}
		String fileName = basePath + UtilBackup.calculateFolderPathForGame(server, String.valueOf(gameId));
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(server.getProperty(IServerProperty.BACKUP_S3_REGION))
			.withCredentials(new ProfileCredentialsProvider(server.getProperty(IServerProperty.BACKUP_S3_PROFILE))).build();

		byte[] buffer = new byte[1024];
		int buffer_size;

		try (S3Object s3Replay = s3.getObject(server.getProperty(IServerProperty.BACKUP_S3_BUCKET), fileName);
		     S3ObjectInputStream s3Stream = s3Replay.getObjectContent();
		     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			while ((buffer_size = s3Stream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, buffer_size);
			}
			GameState gameState = new GameState(server);
			gameState.initFrom(gameState.getGame().getRules(), UtilJson.gunzip(byteArrayOutputStream.toByteArray()));
			return gameState;
		} catch (Exception e) {
			server.getDebugLog().log(gameId, e);
		}
		return null;
	}

}
