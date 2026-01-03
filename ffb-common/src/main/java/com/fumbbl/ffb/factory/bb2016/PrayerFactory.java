package com.fumbbl.ffb.factory.bb2016;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.model.Game;

import java.util.HashMap;

@FactoryType(FactoryType.Factory.PRAYER)
@RulesCollection(Rules.BB2016)
public class PrayerFactory extends com.fumbbl.ffb.factory.PrayerFactory {

  @Override
  public void initialize(Game game) {
    prayers = new HashMap<>();
  }

  @Override
  public Prayer intensivePrayer() {
    return null;
  }

  @Override
  public Prayer valueOf(String enumName) {
    return null;
  }
}
