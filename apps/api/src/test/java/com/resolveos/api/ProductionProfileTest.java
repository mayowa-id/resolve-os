package com.resolveos.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "RESOLVEOS_DB_URL=jdbc:h2:mem:resolveos-production;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
    "RESOLVEOS_DB_USER=sa",
    "RESOLVEOS_DB_PASSWORD=",
    "RESOLVEOS_OIDC_ISSUER=https://issuer.invalid/resolveos",
    "RESOLVEOS_OIDC_AUDIENCE=resolveos-api",
    "RESOLVEOS_WEB_ALLOWED_ORIGINS=https://resolveos.example",
    "PORT=0"
})
@ActiveProfiles("production")
class ProductionProfileTest {

    @Autowired
    private Environment environment;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void productionUsesAssignedPortWithoutLoadingLocalFixtures() {
        assertThat(environment.getProperty("server.port")).isEqualTo("0");
        assertThat(jdbcClient.sql("select count(*) from organizations").query(Integer.class).single()).isZero();
    }
}
