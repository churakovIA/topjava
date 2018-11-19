package ru.javawebinar.topjava.service.jdbc;

import org.junit.Assume;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import static ru.javawebinar.topjava.Profiles.JDBC;

@ActiveProfiles(JDBC)
public class JdbcUserServiceTest extends AbstractUserServiceTest {
    @Override
    public void testValidation() throws Exception {
        Assume.assumeTrue(false);
    }
}