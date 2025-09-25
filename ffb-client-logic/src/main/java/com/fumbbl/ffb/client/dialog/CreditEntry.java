package com.fumbbl.ffb.client.dialog;

public class CreditEntry {
  public final String name;
  public final String licenseName;
  public final String homepageUrl;
  public final String licenseResource;

  public CreditEntry(String name, String licenseName, String homepageUrl, String licenseResource) {
    this.name = name;
    this.licenseName = licenseName;
    this.homepageUrl = homepageUrl;
    this.licenseResource = licenseResource;
  }
}