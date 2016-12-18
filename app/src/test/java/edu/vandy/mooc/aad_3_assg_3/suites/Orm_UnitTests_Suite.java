package edu.vandy.mooc.aad_3_assg_3.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.vandy.mooc.aad_3_assg_3.framework.orm.Entry_Tests;

/**
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // just a sanity check to make sure integration tests running properly
        Entry_Tests.class,
})

public class Orm_UnitTests_Suite {
}
