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
        assertHaveUser(login)
    }

    @Test
    fun cantCreateUsersWithSameLogin() {
        connection.apply {
            createUser("a1", "a1", "a1", "samelogin", "password1")
            createUser("a2", "a2", "a2", "samelogin", "password2")
            assertErrorLoginExists()
        }
    }

    @Test
    fun canCreateUsersWithSameEverythingExceptLogin() {
        connection.apply {
            createUser("a", "a", "a", "login1", "a")
            createUser("a", "a", "a", "login2", "a")

        }
    }

    @Test
    fun niceErrorWhenEmptyLogin() {
        connection.apply {
            createUser("name", "email", "jabber", "", "password")
            assertEquals(listOf("Login is required!"), getErrorTooltipsText())
            assertDontHaveUser("")
        }
    }

    @Test
    fun niceErrorWhenEmptyPassword() {
        connection.apply {
            createUser("name", "email", "jabber", "login", "")
            assertEquals(listOf("Password is required!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    @Test
    fun niceErrorWhenPasswordsDontMatch() {
        connection.apply {
            createUser("name", "email", "jabber", "login", "p1", "p2")
            assertEquals(listOf("Password doesn't match!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    // fails
    // it displays several error bulbs on user registration page, but seems to show only one at a time here...
    @Test
    fun twoNiceErrorsWhenEmptyPasswordAndLogin() {
        connection.apply {
            createUser("name", "email", "jabber", "", "")
            assertEquals(listOf("Login is required!", "Password is required!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    // fails
    @Test
    fun twoNiceErrorsWhenEmptyLoginAndPasswordsDontMatch() {
        connection.apply {
            createUser("name", "email", "jabber", "", "")
            assertEquals(listOf("Login is required!", "Password doesn't match!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    @Test
    fun cantCreateUserIfSomebodyRegistredWithSameLogin() {
        connection.apply {
            register("", "", "login", "password")
            createUser("", "", "", "login", "password")
            
        }
    }

    @Test
    fun canCreateUserWithoutAdditionalInfo() {
        connection.createUser("", "", "", "login", "password")
        assertHaveUser("login")
    }

    private fun assertHaveUser(login: String) {
        assertTrue(connection.getUsersLogins().contains(login))
    }

    private fun assertDontHaveUser(login: String) {
        assertFalse(connection.getUsersLogins().contains(login))
    }
    
    private fun assertErrorLoginExists() {
        assertEquals("Value should be unique: login", connection.getMessageErrorText())
    }

}