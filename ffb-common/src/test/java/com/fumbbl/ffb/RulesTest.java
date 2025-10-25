package com.fumbbl.ffb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesTest {

	@Test
	public void commonIsEligibleFor2016() {
		assertTrue(RulesCollection.Rules.BB2016.isOrExtends(RulesCollection.Rules.COMMON));
	}

	@Test
	public void commonIsEligibleFor2020() {
		assertTrue(RulesCollection.Rules.BB2020.isOrExtends(RulesCollection.Rules.COMMON));
	}

	@ParameterizedTest
	@MethodSource("allRules")
	public void identityIsEligible(RulesCollection.Rules rule) {
		assertTrue(rule.isOrExtends(rule));
	}

	@Test
	public void commonIsEligibleFor2025() {
		assertTrue(RulesCollection.Rules.BB2025.isOrExtends(RulesCollection.Rules.COMMON));
	}

	@Test
	public void _2016IsNotEligibleFor2020() {
		assertFalse(RulesCollection.Rules.BB2020.isOrExtends(RulesCollection.Rules.BB2016));
	}

	@Test
	public void _2020IsNotEligibleForCommon() {
		assertFalse(RulesCollection.Rules.COMMON.isOrExtends(RulesCollection.Rules.BB2020));
	}


	static Stream<RulesCollection.Rules> allRules() {
		return Arrays.stream(RulesCollection.Rules.values());
	}
}
