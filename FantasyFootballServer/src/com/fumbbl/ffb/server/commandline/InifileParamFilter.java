package com.fumbbl.ffb.server.commandline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InifileParamFilter {
	private static final String DEFAULT_VALUE = "server.ini";
	private static final String INIFILE_PARAM = "-inifile";

	public InifileParamFilterResult filterForInifile(String[] argsToFilter) {
		String inifileName = DEFAULT_VALUE;

		if (argsToFilter == null) {
			return new InifileParamFilterResult(inifileName, null);
		}

		List<String> filteredArgs = new ArrayList<>();

		Iterator<String> argsIterator = Arrays.asList(argsToFilter).iterator();
		while (argsIterator.hasNext()) {
			String currentArg = argsIterator.next();
			if (INIFILE_PARAM.equals(currentArg)) {
				if (argsIterator.hasNext()) {
					inifileName = argsIterator.next();
				}
			} else {
				filteredArgs.add(currentArg);
			}
		}

		return new InifileParamFilterResult(inifileName, filteredArgs.toArray(new String[0]));
	}
}
