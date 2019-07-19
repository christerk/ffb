package com.balancedbytes.games.ffb.server.commandline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InifileParamFilterTest {

    private static final String OTHER_PARAM_1 = "otherParam1";
    private static final String OTHER_PARAM_2 = "otherParam2";
    private static final String INIFILE_PARAM = "-inifile";
    private static final String INIFILE_VALUE = "inifile_value";
    private static final String DEFAULT_INIFUILE_VALUE = "server.ini";

    private InifileParamFilter filter = new InifileParamFilter();

    @Test
    void filterForInifileFiltersExistingParam() {
        String[] input = new String[]{OTHER_PARAM_1, INIFILE_PARAM, INIFILE_VALUE, OTHER_PARAM_2};
        String[] expected = new String[]{OTHER_PARAM_1, OTHER_PARAM_2};
        InifileParamFilterResult result = filter.filterForInifile(input);
        assertArrayEquals(expected, result.getFilteredArgs(), "Inifile param and value were not filtered correctly");
        assertEquals(INIFILE_VALUE, result.getInifileName(), "Inifile value has not been extracted correctly");
    }

    @Test
    void filterForInifileReturnsDefaultForMissingParam() {
        String[] input = new String[]{OTHER_PARAM_1, OTHER_PARAM_2};
        String[] expected = new String[]{OTHER_PARAM_1, OTHER_PARAM_2};
        InifileParamFilterResult result = filter.filterForInifile(input);
        assertArrayEquals(expected, result.getFilteredArgs(), "Other params must be retained as passed in.");
        assertEquals(DEFAULT_INIFUILE_VALUE, result.getInifileName(), "Inifile value must be set to the default value");
    }

    @Test
    void filterForInifileReturnsDefaultForMissingValue() {
        String[] input = new String[]{OTHER_PARAM_1, INIFILE_PARAM};
        String[] expected = new String[]{OTHER_PARAM_1};
        InifileParamFilterResult result = filter.filterForInifile(input);
        assertArrayEquals(expected, result.getFilteredArgs(), "Inifile param was not filtered correctly");
        assertEquals(DEFAULT_INIFUILE_VALUE, result.getInifileName(), "Inifile value must be set to the default value");
    }

    @Test
    void filterForInifileReturnsDefaultForEmptyArray() {
        InifileParamFilterResult result = filter.filterForInifile(new String[0]);
        assertEquals(0, result.getFilteredArgs().length, "Empty input must result in empty output");
        assertEquals(DEFAULT_INIFUILE_VALUE, result.getInifileName(), "Inifile value must be set to the default value");
    }

    @Test
    void filterForInifileReturnsDefaultForNullArray() {
        InifileParamFilterResult result = filter.filterForInifile(null);
        assertNull(result.getFilteredArgs(), "Null input must result in null output");
        assertEquals(DEFAULT_INIFUILE_VALUE, result.getInifileName(), "Inifile value must be set to the default value");
    }

}