package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PlayerGender implements INamedObject {

	MALE("male", "M", "he", "his", "him", "himself", VerbForm.singular),
	FEMALE("female", "F", "she", "her", "her", "herself", VerbForm.singular),
	NONBINARY("nonbinary", "NB", "they", "their", "them", "themself", VerbForm.plural),
	NEUTRAL("neutral", "N", "it", "its", "it", "itself", VerbForm.singular);

	public enum VerbForm {
		singular, plural
	}

	private String name;
	private String typeString;
	private String nominative;
	private String genitive;
	private String dative;
	private String self;
	private VerbForm verbForm;

	private PlayerGender(String pName, String pTypeString, String pNominative, String pGenitive, String pDative,
			String pSelf, VerbForm verbForm) {
		this.name = pName;
		this.typeString = pTypeString;
		this.nominative = pNominative;
		this.genitive = pGenitive;
		this.dative = pDative;
		this.self = pSelf;
		this.verbForm = verbForm;
	}

	public String getName() {
		return name;
	}

	public String getTypeString() {
		return typeString;
	}

	public String getNominative() {
		return nominative;
	}

	public String getGenitive() {
		return genitive;
	}

	public String getDative() {
		return dative;
	}

	public String getSelf() {
		return self;
	}

	public String getVerbForm(String singularForm, String pluralForm) {
		return this.verbForm == VerbForm.singular ? singularForm : pluralForm;
	}

	public static PlayerGender fromOrdinal(int ordinal) {
		switch (ordinal) {
		case 1:
			return MALE;
		case 2:
			return FEMALE;
		case 3:
			return NONBINARY;
		default:
			return NEUTRAL;
		}
	}

}
