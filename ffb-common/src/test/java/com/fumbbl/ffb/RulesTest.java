package com.fumbbl.ffb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesTest {

	@Test
	public void commonIsEligibleFor2016() {
		assertTrue(RulesCollection.Rules.BB2016.matches(RulesCollection.Rules.COMMON));
	}

	@Test
	public void commonIsEligibleFor2020() {
		assertTrue(RulesCollection.Rules.BB2020.matches(RulesCollection.Rules.COMMON));
	}

	@ParameterizedTest
	@MethodSource("allRules")
	public void identityIsEligible(RulesCollection.Rules rule) {
		assertTrue(rule.matches(rule));
	}

	static Stream<RulesCollection.Rules> allRules() {
		return Arrays.stream(RulesCollection.Rules.values());
	}
}
