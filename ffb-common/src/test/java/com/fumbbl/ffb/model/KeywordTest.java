package com.fumbbl.ffb.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeywordTest {

	@Test
	void forNameResolvesOfficialKeywordIgnoringCase() {
		assertSame(Keyword.BIG_GUY, Keyword.forName("bIG guY"));
		assertFalse(Keyword.forName("Big Guy").isCustom());
	}

	@Test
	void forNameReturnsUnknownForMissingName() {
		assertSame(Keyword.UNKNOWN, Keyword.forName(null));
		assertSame(Keyword.UNKNOWN, Keyword.forName(" "));
	}

	@Test
	void customKeywordKeepsSpellingAndCasing() {
		Keyword keyword = Keyword.forName("sKink  warrior");

		assertTrue(keyword.isCustom());
		assertEquals("sKink  warrior", keyword.getName());
		assertTrue(keyword.isCanGetEvenWith());
	}

	@Test
	void customKeywordsAreDistinctFromEachOtherAndFromUnknown() {
		Keyword kroxigor = Keyword.forName("Kroxigor");

		assertNotEquals(kroxigor, Keyword.forName("Fungus"));
		assertNotEquals(Keyword.UNKNOWN, kroxigor);
	}

	@Test
	void customKeywordsAreEqualIgnoringCase() {
		Keyword lower = Keyword.forName("kroxigor");
		Keyword upper = Keyword.forName("Kroxigor");

		assertEquals(lower, upper);
		assertEquals(lower.hashCode(), upper.hashCode());
		assertEquals(0, lower.compareTo(upper));
		assertEquals(1, new HashSet<>(Arrays.asList(lower, upper)).size());
	}

	@Test
	void displayNameUpperCasesFirstLetterOfEachWordOfCustomKeyword() {
		assertEquals("Skink  Warrior", Keyword.forName("skink  warrior").getDisplayName());
		assertEquals("McKroxigor", Keyword.forName("mcKroxigor").getDisplayName());
	}

	@Test
	void displayNameOfOfficialKeywordIsUnchanged() {
		assertEquals("Big Guy", Keyword.BIG_GUY.getDisplayName());
		assertEquals("all", Keyword.ALL.getDisplayName());
	}

	@Test
	void sortsOfficialKeywordsInDefinedOrderBeforeCustomOnes() {
		List<Keyword> keywords = Arrays.asList(Keyword.forName("zebra"), Keyword.ORC, Keyword.forName("Aardvark"),
			Keyword.ANIMAL);

		Collections.sort(keywords);

		assertEquals(Arrays.asList(Keyword.ANIMAL, Keyword.ORC, Keyword.forName("Aardvark"), Keyword.forName("zebra")),
			keywords);
	}

	@Test
	void customKeywordCanBeRemovedFromSetByNameFromOtherSource() {
		Set<Keyword> available = new HashSet<>(Arrays.asList(Keyword.forName("Kroxigor"), Keyword.ORC));

		available.remove(Keyword.forName("KROXIGOR"));

		assertEquals(Collections.singleton(Keyword.ORC), available);
	}
}
