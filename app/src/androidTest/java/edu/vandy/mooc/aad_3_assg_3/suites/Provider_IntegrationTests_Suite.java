package edu.vandy.mooc.aad_3_assg_3.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.vandy.mooc.aad_3_assg_3.framework.provider.FeedDBAdapter_AndroidTest;
import edu.vandy.mooc.aad_3_assg_3.framework.provider.FeedProvider_AndroidTest;

/**
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FeedProvider_AndroidTest.class,
        FeedDBAdapter_AndroidTest.class,
})


public class Provider_IntegrationTests_Suite {
}
