package edu.vandy.mooc.aad_3_assg_3.framework.orm;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vandy.mooc.aad_3_assg_3.shared.Entry_Utils;

import static edu.vandy.mooc.aad_3_assg_3.shared.Entry_Utils.TEST_ID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * TODO COMMENT CLASS BETTER
 */
@RunWith(AndroidJUnit4.class)
public class Entry_AndroidTest {

    private Entry mEntryA;
    private Entry mEntryB;

    static private Entry newA() {
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
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mEntryA.writeToParcel(parcel, mEntryA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        Entry resultingEntry = Entry.CREATOR.createFromParcel(parcel);

        System.out.println(mEntryA);
        System.out.println(resultingEntry);

        // Verify that the received data is correct.
        Entry_Utils.checkEntry(resultingEntry, TEST_ID);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mEntryA.writeToParcel(parcel, mEntryA.describeContents());
        mEntryB.writeToParcel(parcel, mEntryB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        Entry[] resultingArray = Entry.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = Entry.CREATOR.createFromParcel(parcel);
        resultingArray[1] = Entry.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        Entry_Utils.checkEntry(resultingArray[0], TEST_ID);
        Entry_Utils.checkEntry(resultingArray[1], -1L);
    }


}



















