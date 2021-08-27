package com.fumbbl.ffb.server.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class AdminList {

	private List<AdminListEntry> fEntries;

	public AdminList() {
		fEntries = new ArrayList<>();
	}

	public AdminListEntry[] getEntries() {
		return fEntries.toArray(new AdminListEntry[fEntries.size()]);
	}

	public void add(AdminListEntry pAdminListEntry) {
		if (pAdminListEntry != null) {
			fEntries.add(pAdminListEntry);
		}
	}

	public int size() {
		return fEntries.size();
	}

}
