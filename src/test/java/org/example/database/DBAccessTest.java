package org.example.database;

import org.example.model.Show;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBAccessTest {
    static DBAccess testDB;
    
    @BeforeAll
    static void setUp() {
        testDB = new DBAccess(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
        testDB.initDatabase();
    }
    
    @Order(1)
    @Test
    @DisplayName("save show and verify")
    void saveShow() {
        Show show = new Show("test1", 3, 3, 5);
        testDB.saveShow(show);

        assertThat(testDB.getShow("test1")).isNotNull();
    }

    
}