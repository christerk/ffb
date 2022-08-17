package com.fumbbl.ffb.server.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class AdminList {

	private final List<AdminListEntry> fEntries;

	public AdminList() {
		fEntries = new ArrayList<>();
	}

	public AdminListEntry[] getEntries() {
		return fEntries.toArray(new AdminListEntry[0]);
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
