import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.WebDriverWait

class Connection(private val driver: ChromeDriver) {

    fun login(login: String, password: String) {
        driver.apply {
            get("http://localhost:8080/login")
            findElementById("id_l.L.login").sendKeys(login)
            findElementById("id_l.L.password").sendKeys(password)
            findElementById("id_l.L.loginButton").click()
        }
    }

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

    fun getErrorTooltipsText(): List<String> = driver.run {
        findElementsByClassName("error-bulb2").sortedBy { it.location.y }.map { bulb ->
            Actions(driver).moveToElement(bulb).build().perform()
            findElementsByClassName("error-tooltip").map { it.text }.first { it.isNotEmpty() }
        }
    }
}

fun main(args: Array<String>) {
    val driver = ChromeDriver()
    val connection = Connection(driver)
    connection.login("root", "root")
//    Thread.sleep(1000)
    connection.register("", "0", "", "")
    Thread.sleep(100)
    println(connection.getErrorTooltipsText())

    driver.close()

}