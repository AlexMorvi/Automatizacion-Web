package com.nttdata.steps;

import com.nttdata.page.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginSteps {
    private WebDriver driver;
    private WebDriverWait wait;

    public LoginSteps(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToStore() {
        driver.get(LoginPage.LOGIN_URL);
    }

    public void login(String email, String password) {
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPage.EMAIL_INPUT));
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement passInput = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPage.PASSWORD_INPUT));
        passInput.clear();
        passInput.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(LoginPage.LOGIN_BUTTON)).click();
    }

    /**
     * Detecta fallo de login con dos verificaciones rapidas:
     *
     * 1. Email con formato invalido (validacion HTML5): el browser bloquea el submit sin POST.
     *    Se detecta instantaneamente via JS (validity.valid = false).
     *
     * 2. Credenciales invalidas (error del servidor): PrestaShop muestra .alert-danger.
     *    Se usa ExpectedConditions.or() para esperar REDIRECT (exito) o ALERTA (fallo)
     *    simultaneamente, sin timeout fijo.
     */
    public boolean isLoginFailed() {
        // Si ya hubo redirect fuera de la pagina de login → login exitoso
        if (!driver.getCurrentUrl().contains("iniciar-sesion")) {
            return false;
        }

        // Caso 1: validacion HTML5 bloqueo el submit (formato de email invalido)
        Boolean emailValid = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return document.getElementById('field-email').validity.valid");
        if (Boolean.FALSE.equals(emailValid)) {
            return true;
        }

        // Caso 2: credenciales llegaron al servidor, esperar redirect o alerta de error
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.or(
                            ExpectedConditions.not(ExpectedConditions.urlContains("iniciar-sesion")),
                            ExpectedConditions.visibilityOfElementLocated(LoginPage.LOGIN_ERROR_ALERT)
                    ));
            return driver.getCurrentUrl().contains("iniciar-sesion");
        } catch (TimeoutException e) {
            return true;
        }
    }

    /**
     * Lee el texto del error del servidor (.alert-danger).
     * Si el fallo fue por HTML5 (formato de email invalido), retorna string vacio.
     */
    public String getLoginErrorMessage() {
        try {
            WebElement alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(LoginPage.LOGIN_ERROR_ALERT));
            return alert.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
