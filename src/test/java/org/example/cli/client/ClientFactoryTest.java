package org.example.cli.client;

import org.example.database.DBAccess;
import org.example.database.DBTestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFactoryTest {
    private static DBAccess testDB;

    @BeforeAll
    static void beforeAll() {
        testDB = new DBAccess(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
    }

    @Test
    @DisplayName("should return correct client type")
    void getClient() {
        ClientFactory factory = new ClientFactory(testDB);
        assertThat(factory.getClient("admin", System.in, System.out)).isInstanceOf(AdminClient.class);
        assertThat(factory.getClient("buyer", System.in, System.out)).isInstanceOf(BuyerClient.class);
    }
}