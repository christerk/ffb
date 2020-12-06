package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellCollection {
	private static Map<InducementType, List<SpecialEffect>> spellCollections = create();

	public static List<SpecialEffect> spells(InducementType inducementType) {
		List<SpecialEffect> spells = spellCollections.get(inducementType);
		return spells != null ? spells : new ArrayList<SpecialEffect>();
	}

	private static Map<InducementType, List<SpecialEffect>> create() {
		Map<InducementType, List<SpecialEffect>> spellCollections = new HashMap<InducementType, List<SpecialEffect>>();
		spellCollections.put(InducementType.WIZARD, wizardSpells());
		return spellCollections;
	}

	private static List<SpecialEffect> wizardSpells() {
		List<SpecialEffect> spells = new ArrayList<SpecialEffect>();
		spells.add(SpecialEffect.FIREBALL);
		spells.add(SpecialEffect.ZAP);
		return spells;
	}
}
