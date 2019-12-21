package ru.spb.hse.youtest

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
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
    fun `create user creates a user`() {
        val login = "d"
        connection.createUser("a", "b", "c", login, "e")
        assertHaveUser(login)
    }

    @Test
    fun `can't create users with same login`() {
        connection.apply {
            createUser("a1", "a1", "a1", "samelogin", "password1")
            createUser("a2", "a2", "a2", "samelogin", "password2")
            assertErrorLoginExists()
        }
    }

    @Test
    fun `can create users with same everything except login`() {
        connection.apply {
            createUser("a", "a", "a", "login1", "a")
            createUser("a", "a", "a", "login2", "a")

        }
    }

    @Test
    fun `nice error when empty login`() {
        connection.apply {
            createUser("name", "email", "jabber", "", "password")
            assertEquals(listOf("Login is required!"), getErrorTooltipsText())
            assertDontHaveUser("")
        }
    }

    @Test
    fun `nice error when empty password`() {
        connection.apply {
            createUser("name", "email", "jabber", "login", "")
            assertEquals(listOf("Password is required!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    @Test
    fun `nice error when passwords don't match`() {
        connection.apply {
            createUser("name", "email", "jabber", "login", "p1", "p2")
            assertEquals(listOf("Password doesn't match!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    // fails
    // it displays several error bulbs on user registration page, but seems to show only one at a time here...
    @Test
    fun `two nice errors when empty password and login`() {
        connection.apply {
            createUser("name", "email", "jabber", "", "")
            assertEquals(listOf("Login is required!", "Password is required!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    // fails
    @Test
    fun `two nice errors when empty login and passwords don't match`() {
        connection.apply {
            createUser("name", "email", "jabber", "", "")
            assertEquals(listOf("Login is required!", "Password doesn't match!"), getErrorTooltipsText())
            assertDontHaveUser("login")
        }
    }

    @Test
    fun `cant create user if somebody registred with same login`() {
        connection.apply {
            register("", "", "login", "password")
            createUser("", "", "", "login", "password")
            
        }
    }

    @Test
    fun `does not brake on long logins`() {
        connection.apply {
            createUser("", "", "", "a".repeat(100), "1")
            assertTrue(getUsersLogins().any { it.startsWith("aaaaaa") })
        }
    }

    @Test
    fun `can create user without additional info`() {
        connection.createUser("", "", "", "login", "password")
        assertHaveUser("login")
    }

    @TestFactory
    fun `allows weird symbols in login`() = listOf(
        "!",
        "`",
        "}",
        "$",
        "%",
        "'",
        "\"",
        ")",
        ";",
        "-",
        "\\",
        "_",
        "русскиебуквы",
        "@#$%^&*()"
    ).map {login ->
        DynamicTest.dynamicTest("Accepts login $login") {
            connection.deleteEveryone()
            connection.createUser("q", "w", "e", login, "password")
            assertHaveUser(login)
        }
    }

    @TestFactory
    fun `does not allow to use space in login`() = listOf(
        " 11",
        "11 ",
        "11 11"
    ).map {login ->
        DynamicTest.dynamicTest("Forbids login '$login'") {
            connection.deleteEveryone()
            connection.createUser("q", "w", "e", login, "password")
            assertTrue(connection.getMessageErrorText().startsWith("Restricted character"))
        }
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