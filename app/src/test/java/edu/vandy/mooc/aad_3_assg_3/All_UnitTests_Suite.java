package edu.vandy.mooc.aad_3_assg_3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.vandy.mooc.aad_3_assg_3.framework.orm.Entry_Tests;
import edu.vandy.mooc.aad_3_assg_3.misc.SanityTests_UnitTest;
import edu.vandy.mooc.aad_3_assg_3.suites.Orm_UnitTests_Suite;

/**
 *
 */

@SuppressWarnings("unused")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // just a sanity check to make sure integration tests running properly
        Orm_UnitTests_Suite.class,
        SanityTests_UnitTest.class,
        Entry_Tests.class,
})

public class All_UnitTests_Suite {
}
