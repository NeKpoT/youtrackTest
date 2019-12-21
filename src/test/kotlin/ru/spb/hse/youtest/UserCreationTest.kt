package ru.spb.hse.youtest

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeDriver

class UserCreationTest {
    private val driver = ChromeDriver()
    private val connection = Connection(driver)

    @BeforeEach
    fun init() {
        connection.loginAsRoot()
        connection.deleteEveryone()
    }

    @AfterEach
    fun close() {
        driver.close()
    }

    @Test
    fun basicCreateUser() {
        val login = "d"
        connection.createUser("a", "b", "c", login, "e")
        println(connection.getUsersLogins())
        assertTrue(connection.getUsersLogins().contains(login))
    }

    @Test
    fun cantCreateUsersWithSameLogin() {
        connection.apply {
            createUser("a1", "a1", "a1", "login", "password1")
            createUser("a2", "a2", "a2", "login", "password2")
            assertEquals("Value should be unique: login", getMessageErrorText())
        }
    }

    @Test
    fun canCreateUsersWithSameEverything() {
        connection.apply {
            createUser("a", "a", "a", "login1", "a")
            createUser("a", "a", "a", "login2", "a")

        }
    }
}