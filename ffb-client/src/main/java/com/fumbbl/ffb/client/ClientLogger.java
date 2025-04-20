package com.fumbbl.ffb.client;

import com.fumbbl.ffb.util.StringTool;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ClientLogger {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235

	private PrintWriter writer;
	private long gameId;
	private String path;
	private boolean enabled;

	public ClientLogger(String path, boolean enabled, long gameId) {
		this.enabled = enabled;
		this.gameId = gameId;
		updateLogPath(path);
	}

	public void updateLogPath(String path) {
		this.path = path;
		updateLogFile();
	}

	private void updateLogFile() {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
		if (!StringTool.isProvided(path)) {
			return;
		}

		try {
			writer = new PrintWriter(new FileWriter(filePath(), gameId != 0));
		} catch (Exception e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
		cleanUp();
	}

	private String filePath() {
		String fileName = gameId == 0 ? "ffb.log" : "ffb_" + gameId + ".log";
		return path + File.separator + fileName;
	}

	public void updateId(long id) {
		this.gameId = id;
		updateLogFile();
	}

	public void updateEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void logError(String message) {
		logLine("ERROR:", message);
	}

	private void logLine(String prefix, String message) {
		if (enabled && writer != null) {
			writer.println(DATE_FORMAT.format(new Date()) + " " + prefix + " " + message);
			writer.flush();
		} else {
			System.out.println(prefix + " " + message);
		}
	}

	public void logDebug(String message) {
		logLine("DEBUG: ", message);
	}

	public void log(Throwable throwable) {
		if (enabled && this.writer != null) {
			writer.print(DATE_FORMAT.format(new Date()) + " ");
			throwable.printStackTrace(writer);
			writer.flush();
		}	else {
			throwable.printStackTrace(System.out);
		}
	}

	private void cleanUp() {
		if (gameId == 0) {
			return;
		}
		File[] logFiles = new File(path).listFiles((dir, name) -> name.startsWith("ffb_") && !name.equals("ffb_" + gameId + ".log"));

		if (logFiles == null) {
			return;
		}

		List<File> sortedFiles = Arrays.stream(logFiles).sorted(Comparator.comparingLong(File::lastModified).reversed()).collect(Collectors.toList());

		if (sortedFiles.size() > 3) {
			//noinspection ResultOfMethodCallIgnored
			sortedFiles.subList(3, sortedFiles.size()).forEach(File::delete);
		}
	}
}
