package edu.vandy.mooc.aad_3_assg_3.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.vandy.mooc.aad_3_assg_3.framework.orm.Entry_AndroidTest;

/**
 *
 */
// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        Entry_AndroidTest.class,
})


public class Orm_IntegrationTests_Suite {
}
