package org.example.cli.client;

import org.example.database.DBManager;
import org.example.database.DBTestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFactoryTest {
    private static DBManager testDB;

    @BeforeAll
    static void beforeAll() {
        testDB = new DBManager(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
    }

    @Test
    @DisplayName("should return correct client type")
    void getClient() {
        ClientFactory factory = new ClientFactory(testDB);
        assertThat(factory.getClient("admin", System.in, System.out)).isInstanceOf(AdminClient.class);
        assertThat(factory.getClient("buyer", System.in, System.out)).isInstanceOf(BuyerClient.class);
    }

    @Test
    void getClient_null() {
        ClientFactory factory = new ClientFactory(testDB);
        assertThat(factory.getClient("foo", System.in, System.out)).isNull();
    }
}