package ru.spb.hse.youtest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.chrome.ChromeDriver

class UserCreationTest {
    private val driver = ChromeDriver()
    private val connection = Connection(driver)

    @BeforeEach
    fun init() {
        connection.loginAsRoot()
        connection.deleteEveryone()
    }
}