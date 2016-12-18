package edu.vandy.mooc.aad_3_assg_3.shared;

// imports

import edu.vandy.mooc.aad_3_assg_3.framework.orm.Entry;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared Entry testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class Entry_Utils {

    static final public long TEST_ID = 123456L;
    static final public String TEST_ENTRY_ID = "test entry ID";
    static final public String TEST_TITLE = "Lets party like its 1999";
    static final public String TEST_LINK = "https://www.vanderbilt.edu";
    static final public String TEST_PUBLISHED = "1999-12-31";
    static final public String TEST_AUTHOR = "CNN";

    static public Entry newEntry(long id) {
        return new Entry(id, TEST_ENTRY_ID, TEST_TITLE, TEST_LINK, TEST_PUBLISHED, TEST_AUTHOR);
    }

    static public Entry newEntry() {
        return new Entry(TEST_ENTRY_ID, TEST_TITLE, TEST_LINK, TEST_PUBLISHED, TEST_AUTHOR);
    }


    static public void checkEntry(Entry testEntry, long id) {
        assertThat(testEntry.get_ID(), is(id));
        assertThat(testEntry.getLINK(), is(TEST_LINK));
        assertThat(testEntry.getPUBLISHED(), is(TEST_PUBLISHED));
        assertThat(testEntry.getTITLE(), is(TEST_TITLE));
        assertThat(testEntry.getENTRY_ID(), is(TEST_ENTRY_ID));
        assertThat(testEntry.getAUTHOR(), is(TEST_AUTHOR));
    }

}
