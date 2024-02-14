package com.fumbbl.ffb.server.commandline;

public class InifileParamFilterResult {

	private final String iniFileName;
	private final String overrideFileName;
	private final String[] filteredArgs;

	public InifileParamFilterResult(String iniFileName, String overrideFileName, String[] filteredArgs) {
		this.iniFileName = iniFileName;
		this.overrideFileName = overrideFileName;
		this.filteredArgs = filteredArgs;
	}

	public String getIniFileName() {
		return iniFileName;
	}

	public String[] getFilteredArgs() {
		return filteredArgs;
	}

	public String getOverrideFileName() {
		return overrideFileName;
	}
}
