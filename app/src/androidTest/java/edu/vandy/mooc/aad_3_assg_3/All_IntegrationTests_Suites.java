package edu.vandy.mooc.aad_3_assg_3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.vandy.mooc.aad_3_assg_3.misc.SanityTest_InstrumentedTest;
import edu.vandy.mooc.aad_3_assg_3.suites.Orm_IntegrationTests_Suite;
import edu.vandy.mooc.aad_3_assg_3.suites.Provider_IntegrationTests_Suite;

/**
 *
 */
// Runs all unit tests.
@SuppressWarnings("unused")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // just a sanity check to make sure integration tests running properly
        SanityTest_InstrumentedTest.class,
        Provider_IntegrationTests_Suite.class,
        Orm_IntegrationTests_Suite.class,
})


public class All_IntegrationTests_Suites {
}
