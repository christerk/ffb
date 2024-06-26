package com.fumbbl.ffb;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class TeamList implements IXmlSerializable, IJsonSerializable {

//  <teams coach="47257">
//    <team>
//      <id>492614</id>
//      <status>1</status>
//      <division>1</division>
//      <name>Bauernopfer</name>
//      <rating>147</rating>
//      <strength>152</strength>
//      <race>Elf</race>
//      <treasury>0</treasury>
//    </team>
//  </teams>

	public static final String XML_TAG = "teams";

	private static final String _XML_ATTRIBUTE_COACH = "coach";

	private String fCoach;
	private final List<TeamListEntry> fTeamListEntries;

	public TeamList() {
		fTeamListEntries = new ArrayList<>();
	}

	public TeamList(String pCoach, TeamListEntry[] pTeamListEntries) {
		this();
		fCoach = pCoach;
		add(pTeamListEntries);
	}

	public String getCoach() {
		return fCoach;
	}

	public TeamListEntry[] getTeamListEntries() {
		return fTeamListEntries.toArray(new TeamListEntry[fTeamListEntries.size()]);
	}

	public void add(TeamListEntry pTeamListEntry) {
		if (pTeamListEntry != null) {
			fTeamListEntries.add(pTeamListEntry);
		}
	}

	private void add(TeamListEntry[] pTeamListEntries) {
		if (ArrayTool.isProvided(pTeamListEntries)) {
			for (TeamListEntry teamListEntry : pTeamListEntries) {
				add(teamListEntry);
			}
		}
	}

	public int size() {
		return fTeamListEntries.size();
	}

	public TeamList filterActiveTeams() {
		TeamList filteredTeamList = new TeamList();
		for (TeamListEntry teamListEntry : getTeamListEntries()) {
			if (TeamStatus.ACTIVE == teamListEntry.getTeamStatus()) {
				filteredTeamList.add(teamListEntry);
			}
		}
		return filteredTeamList;
	}

	public TeamList filterDivisions(String[] pDivisions) {
		TeamList filteredTeamList = new TeamList();
		if (ArrayTool.isProvided(pDivisions)) {
			for (TeamListEntry teamListEntry : getTeamListEntries()) {
				for (String division : pDivisions) {
					if (StringTool.isProvided(teamListEntry.getDivision()) && teamListEntry.getDivision().equals(division)) {
						filteredTeamList.add(teamListEntry);
					}
				}
			}
		}
		return filteredTeamList;
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
		UtilXml.startElement(pHandler, XML_TAG, attributes);

		TeamListEntry[] teamListEntries = getTeamListEntries();
		for (TeamListEntry teamListEntry : teamListEntries) {
			teamListEntry.addToXml(pHandler);
		}

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (XML_TAG.equals(pXmlTag)) {
			fCoach = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_COACH);
		}
		if (TeamListEntry.XML_TAG.equals(pXmlTag)) {
			TeamListEntry teamListEntry = new TeamListEntry();
			teamListEntry.startXmlElement(game, pXmlTag, pXmlAttributes);
			add(teamListEntry);
			xmlElement = teamListEntry;
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		JsonArray teamList = new JsonArray();
		TeamListEntry[] teamListEntries = getTeamListEntries();
		for (TeamListEntry teamListEntry : teamListEntries) {
			teamList.add(teamListEntry.toJsonValue());
		}
		IJsonOption.TEAM_LIST_ENTRIES.addTo(jsonObject, teamList);
		return jsonObject;
	}

	public TeamList initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		JsonArray teamListEntries = IJsonOption.TEAM_LIST_ENTRIES.getFrom(source, jsonObject);
		for (int i = 0; i < teamListEntries.size(); i++) {
			TeamListEntry teamListEntry = new TeamListEntry();
			teamListEntry.initFrom(source, teamListEntries.get(i));
			add(teamListEntry);
		}
		return this;
	}

}
