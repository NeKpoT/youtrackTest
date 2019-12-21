package ru.spb.hse.youtest

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class Connection(private val driver: ChromeDriver, private val rootPassword: String = "root") {

    fun login(login: String, password: String) {
        driver.apply {
            get("http://localhost:8080/login")
            findElementById("id_l.L.login").sendKeys(login)
            findElementById("id_l.L.password").sendKeys(password)
            findElementById("id_l.L.loginButton").click()
        }
    }

    fun loginAsRoot() = login("root", rootPassword)

    fun register(name: String, email: String, login: String, password: String, confirmPassword: String? = null) {
        driver.apply {
            get("http://localhost:8080/registerUserForm")
            findElementById("id_l.R.user_fullName").sendKeys(name)
            findElementById("id_l.R.user_email").sendKeys(email)
            findElementById("id_l.R.user_login").sendKeys(login)
            findElementById("id_l.R.password").sendKeys(password)
            findElementById("id_l.R.confirmPassword").sendKeys(confirmPassword ?: password)
            findElementById("id_l.R.register").click()
        }
    }

    fun createUser(name: String, email: String, jabber: String, login: String, password: String, confirmPassword: String? = null) {
        loadUsersPage()
        driver.apply {
            waitElement(By.id("id_l.U.createNewUser")).click()
            waitElement(By.id("id_l.U.cr.login")).sendKeys(login)
            findElementById("id_l.U.cr.password").sendKeys(password)
            findElementById("id_l.U.cr.confirmPassword").sendKeys(confirmPassword ?: password)
            findElementById("id_l.U.cr.fullName").sendKeys(name)
            findElementById("id_l.U.cr.email").sendKeys(email)
            findElementById("id_l.U.cr.jabber").sendKeys(jabber)
            findElementById("id_l.U.cr.createUserOk").click()
        }
        Thread.sleep(200) // seems to be the only way to wait until the user is actually created
    }

    fun deleteEveryone() {
        loadUsersPage()
        val userIDs = getRegularUsersEditLinks().map { it.getAttribute("p0") }
        userIDs.forEach {
            loadUsersPage()
            waitElement(By.cssSelector("a[cn='l.U.usersList.deleteUser'][p0='$it']")).click()
            wait().until(ExpectedConditions.alertIsPresent())
            driver.switchTo().alert().accept()
        }
    }

    fun getRegularUsersEditLinks(): List<WebElement> {
        loadUsersPage()
        return driver.findElementsByCssSelector("a[cn='l.U.usersList.UserLogin.editUser']").filter { it.text != "guest" && it.text != "root" }
    }

    fun getUsersLogins(): List<String> = getRegularUsersEditLinks().map { it.text }

    fun getErrorTooltipsText(): List<String> = driver.run {
        val bulbLocator = By.className("error-bulb2")
        waitElement(bulbLocator)
        findElements(bulbLocator).sortedBy { it.location.y }.map { bulb ->
            Actions(driver).moveToElement(bulb).build().perform()
            findElementsByClassName("error-tooltip").map { it.text }.first { it.isNotEmpty() }
        }
    }

    fun getMessageErrorText(): String = waitElement(By.className("errorSeverity")).text

    private fun wait() = WebDriverWait(driver, 3)

    private fun waitElement(locator: By): WebElement = wait().until(ExpectedConditions.presenceOfElementLocated(locator))

    private fun loadUsersPage() = driver.get("http://localhost:8080/users")
}

fun main(args: Array<String>) {
    val s = "\t"
    println(listOf(
        " " to "space",
        "\t" to "tab"
    ).flatMap {(space, name) ->
        listOf(
            "${space}11",
            "11${space}",
            "11${space}11"
        )})
    return
    val driver = ChromeDriver()
    val connection = Connection(driver)
    connection.login("root", "root")
//    Thread.sleep(1000)
    connection.createUser("aa", "0", "", "aaqweq", "ewq")
    connection.createUser("ab1", "0", "", "aaqweq2", "ewq")
    connection.createUser("ab2", "0", "", "qw3eq", "ewq")
    connection.createUser("ab21", "0", "", "qw22eq", "ewq")
    connection.createUser("ab22", "0", "", "qw22eq3", "ewq")
    connection.createUser("ab23", "0", "", "aqw22eq", "ewq")
//    Thread.sleep(100)
//    println(connection.getErrorTooltipsText())

    driver.get("http://localhost:8080/users")
    Thread.sleep(100)
//    driver.findElementsByCssSelector("a[cn='l.U.usersList.deleteUser']").forEach { println(it.text + "!") }
    connection.deleteEveryone()
    driver.close()

}