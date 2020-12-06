package com.balancedbytes.games.ffb.server.commandline;

public class InifileParamFilterResult {

	private final String inifileName;
	private final String[] filteredArgs;

	public InifileParamFilterResult(String inifileName, String[] filteredArgs) {
		this.inifileName = inifileName;
		this.filteredArgs = filteredArgs;
	}

	public String getInifileName() {
		return inifileName;
	}

	public String[] getFilteredArgs() {
		return filteredArgs;
	}
}
