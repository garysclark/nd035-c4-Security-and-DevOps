package com.udacity.examples.Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;

import org.junit.jupiter.api.Test;

public class HelperTest {

	private static final long TEST_EMPTY_STRING_COUNT = 2l;
	private static final List<String> TEST_CONTAINS_EMPTY_STRINGS = Arrays.asList("Hello", "", "There", "");
	private static final List<Integer> TEST_INTEGER_LIST_FOR_STATS = Arrays.asList(1,2,3,4,5);
	private static final String TEST_MERGED_LIST = "First, Last";
	private static final List<String> TEST_LIST_TO_BE_MERGED = Arrays.asList("First", "Last");

	@Test
	public void canGetEmptyStringCount() {
		assertEquals(TEST_EMPTY_STRING_COUNT, Helper.getCount(TEST_CONTAINS_EMPTY_STRINGS));
	}
	
	@Test
	public void canGetStats() {
		IntSummaryStatistics statistics = Helper.getStats(TEST_INTEGER_LIST_FOR_STATS);
		assertEquals(TEST_INTEGER_LIST_FOR_STATS.size(), statistics.getCount());
		OptionalDouble average = TEST_INTEGER_LIST_FOR_STATS.stream().mapToDouble(a -> a).average();
		assertEquals(average.getAsDouble(), statistics.getAverage());
	}
	
	@Test
	public void canGetMergedList() {
		assertEquals(TEST_MERGED_LIST, Helper.getMergedList(TEST_LIST_TO_BE_MERGED));
	}
	
}
