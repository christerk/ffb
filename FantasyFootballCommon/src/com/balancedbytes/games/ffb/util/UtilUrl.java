package com.balancedbytes.games.ffb.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author Kalimar
 */
public class UtilUrl {

	public static String createUrl(String pBaseUrl, String pRelativeUrl) {
		String url;
		if (StringTool.isProvided(pBaseUrl)) {
			if (StringTool.isProvided(pRelativeUrl)) {
				try {
					URL base = new URL(pBaseUrl);
					url = new URL(base, pRelativeUrl).toString();
				} catch (MalformedURLException mue) {
					url = null;
				}
			} else {
				url = pBaseUrl;
			}
		} else {
			url = pRelativeUrl;
		}
		return url;
	}

	public static void main(String[] args) {
		System.out.println(createUrl("http://fumbbl.com/FUMBBL/Images", "PlayerIcons/amlineman1.gif"));
		System.out.println(createUrl("http://fumbbl.com/FUMBBL/Images/", "PlayerIcons/amlineman1.gif"));
		System.out.println(createUrl("http://fumbbl.com/FUMBBL/Images/", "/PlayerIcons/amlineman1.gif"));
		System.out.println(createUrl("http://fumbbl.com/FUMBBL/Images/", "../PlayerIcons/amlineman1.gif"));
		System.out.println(createUrl("http://fumbbl.com/FUMBBL/Images/", "http://google.de/PlayerIcons/amlineman1.gif"));
	}

}
