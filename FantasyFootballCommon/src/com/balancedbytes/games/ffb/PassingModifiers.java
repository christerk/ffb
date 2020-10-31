package com.balancedbytes.games.ffb;

import java.util.HashSet;
import java.util.Set;

public class PassingModifiers {
  public static Set<PassModifier> tackleZoneModifiers = new HashSet<PassModifier>();
  public static Set<PassModifier> disturbingPresenceModifiers = new HashSet<PassModifier>();
  
  public static final PassModifier ACCURATE = new PassModifier("Accurate", -1, false, false);
  public static final PassModifier NERVES_OF_STEEL = new PassModifier("Nerves of Steel", 0, false, false);
  public static final PassModifier VERY_SUNNY = new PassModifier("Very Sunny", 1, false, false);
  public static final PassModifier BLIZZARD = new PassModifier("Blizzard", 0, false, false);
  public static final PassModifier STUNTY = new PassModifier("Stunty", 1, false, false);
  public static final PassModifier TACKLEZONES_1 = new PassModifier("1 Tacklezone", 1, true, false);
  public static final PassModifier TACKLEZONES_2 = new PassModifier("2 Tacklezones", 2, true, false);
  public static final PassModifier TACKLEZONES_3 = new PassModifier("3 Tacklezones", 3, true, false);
  public static final PassModifier TACKLEZONES_4 = new PassModifier("4 Tacklezones", 4, true, false);
  public static final PassModifier TACKLEZONES_5 = new PassModifier("5 Tacklezones", 5, true, false);
  public static final PassModifier TACKLEZONES_6 = new PassModifier("6 Tacklezones", 6, true, false);
  public static final PassModifier TACKLEZONES_7 = new PassModifier("7 Tacklezones", 7, true, false);
  public static final PassModifier TACKLEZONES_8 = new PassModifier("8 Tacklezones", 8, true, false);
  public static final PassModifier DISTURBING_PRESENCES_1 = new PassModifier("1 Disturbing Presence", 1, false, true);
  public static final PassModifier DISTURBING_PRESENCES_2 = new PassModifier("2 Disturbing Presences", 2, false, true);
  public static final PassModifier DISTURBING_PRESENCES_3 = new PassModifier("3 Disturbing Presences", 3, false, true);
  public static final PassModifier DISTURBING_PRESENCES_4 = new PassModifier("4 Disturbing Presences", 4, false, true);
  public static final PassModifier DISTURBING_PRESENCES_5 = new PassModifier("5 Disturbing Presences", 5, false, true);
  public static final PassModifier DISTURBING_PRESENCES_6 = new PassModifier("6 Disturbing Presences", 6, false, true);
  public static final PassModifier DISTURBING_PRESENCES_7 = new PassModifier("7 Disturbing Presences", 7, false, true);
  public static final PassModifier DISTURBING_PRESENCES_8 = new PassModifier("8 Disturbing Presences", 8, false, true);
  public static final PassModifier DISTURBING_PRESENCES_9 = new PassModifier("9 Disturbing Presences", 9, false, true);
  public static final PassModifier DISTURBING_PRESENCES_10 = new PassModifier("10 Disturbing Presences", 10, false, true);
  public static final PassModifier DISTURBING_PRESENCES_11 = new PassModifier("11 Disturbing Presences", 11, false, true);
  public static final PassModifier GROMSKULLS_EXPLODING_RUNES = new PassModifier("Gromskull's Exploding Runes", 1, false, false);

  public static final PassModifier STRONG_ARM = new PassModifier("Strong Arm", -1, false, false) {
    @Override
    public boolean appliesToContext(PassContext context) {
      return context.distance != PassingDistance.QUICK_PASS;
    }
  };
  
  public static final PassModifier THROW_TEAM_MATE = new PassModifier("Throw Team-Mate", 1, false, false) {
    @Override
    public boolean appliesToContext(PassContext context) {
      return context.duringThrowTeamMate;
    }
  };
  
  public class PassContext {
    public PassingDistance distance;
    public boolean duringThrowTeamMate;

    public PassContext(PassingDistance distance, boolean duringThrowTeamMate) {
      this.distance = distance;
      this.duringThrowTeamMate = duringThrowTeamMate;
    }
  }
  
}