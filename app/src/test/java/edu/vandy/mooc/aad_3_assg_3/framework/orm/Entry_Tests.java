package edu.vandy.mooc.aad_3_assg_3.framework.orm;

// imports

import org.junit.Before;
import org.junit.Test;

import edu.vandy.mooc.aad_3_assg_3.shared.Entry_Utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * TODO COMMENT CLASS BETTER
 */
//@RunWith(JUnit4.class)
public class Entry_Tests {

    private Entry mEntryA;
    private Entry mEntryB;

    private static Entry newA() {
        return Entry_Utils.newEntry(Entry_Utils.TEST_ID);
    }

    static private Entry newB() {
        return Entry_Utils.newEntry();
    }

    @Before
    public void setUp() {
        mEntryA = newA();
        mEntryB = newB();
    }

    @Test
    public void equalsTest() {
        Entry testEntry = newA();
        // check that 'testEntry' has the proper start values.
        Entry_Utils.checkEntry(testEntry, Entry_Utils.TEST_ID);
        // check that other Entry match it (ignoring _ID)
        assertTrue(mEntryA.equals(testEntry));
        assertTrue(mEntryB.equals(testEntry));
        // check that it matches other Entry (ignoring _ID)
        assertTrue(testEntry.equals(mEntryA));
        assertTrue(testEntry.equals(mEntryB));
    }

    @Test
    public void testGetAndSet() {

        Entry testEntry = newA();

        // long(s)
        testEntry.set_ID(Entry_Utils.TEST_ID + 30L);
        // string(s)
        testEntry.setENTRY_ID(Entry_Utils.TEST_ENTRY_ID + "new1");
        testEntry.setTITLE(Entry_Utils.TEST_TITLE + "new2");
        testEntry.setLINK(Entry_Utils.TEST_LINK + "new3");
        testEntry.setPUBLISHED(Entry_Utils.TEST_PUBLISHED + "new4");
        testEntry.setAUTHOR(Entry_Utils.TEST_AUTHOR + "new5");

        // long(s)
        assertThat(testEntry.get_ID(), is(Entry_Utils.TEST_ID + 30L));
        // string(s)
        assertThat(testEntry.getENTRY_ID(), is(Entry_Utils.TEST_ENTRY_ID + "new1"));
        assertThat(testEntry.getTITLE(), is(Entry_Utils.TEST_TITLE + "new2"));
        assertThat(testEntry.getLINK(), is(Entry_Utils.TEST_LINK + "new3"));
        assertThat(testEntry.getPUBLISHED(), is(Entry_Utils.TEST_PUBLISHED + "new4"));
        assertThat(testEntry.getAUTHOR(), is(Entry_Utils.TEST_AUTHOR + "new5"));

    }

}
