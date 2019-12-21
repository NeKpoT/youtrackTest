import org.openqa.selenium.By
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
        driver.apply {
            get("http://localhost:8080/users")
            waitOne().until(ExpectedConditions.presenceOfElementLocated(By.id("id_l.U.createNewUser"))).click()
            waitOne().until(ExpectedConditions.presenceOfElementLocated(By.id("id_l.U.cr.login"))).sendKeys(login)
            findElementById("id_l.U.cr.password").sendKeys(password)
            findElementById("id_l.U.cr.confirmPassword").sendKeys(confirmPassword ?: password)
            findElementById("id_l.U.cr.fullName").sendKeys(name)
            findElementById("id_l.U.cr.email").sendKeys(email)
            findElementById("id_l.U.cr.jabber").sendKeys(jabber)
            findElementById("id_l.U.cr.createUserOk").click()
        }
    }

    fun deleteEveryone() {
        driver.apply {
            get("http://localhost:8080/users")
            driver.findElementsByCssSelector("a[cn='l.U.usersList.deleteUser']").forEach {
                it.click()
                driver.switchTo().alert().accept()
            }
        }
    }

    fun getErrorTooltipsText(): List<String> = driver.run {
        findElementsByClassName("error-bulb2").sortedBy { it.location.y }.map { bulb ->
            Actions(driver).moveToElement(bulb).build().perform()
            findElementsByClassName("error-tooltip").map { it.text }.first { it.isNotEmpty() }
        }
    }

    private fun waitOne() = WebDriverWait(driver, 1)
}

fun main(args: Array<String>) {
    val driver = ChromeDriver()
    val connection = Connection(driver)
    connection.login("root", "root")
//    Thread.sleep(1000)
    connection.createUser("ab", "0", "", "qweq", "ewq")
//    Thread.sleep(100)
//    println(connection.getErrorTooltipsText())

    driver.get("http://localhost:8080/users")
    Thread.sleep(100)
//    driver.findElementsByCssSelector("a[cn='l.U.usersList.deleteUser']").forEach { println(it.text + "!") }
    connection.deleteEveryone()
    driver.close()

}