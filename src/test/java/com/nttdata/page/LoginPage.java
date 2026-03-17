package com.nttdata.page;

import org.openqa.selenium.By;

public class LoginPage {
    public static final String LOGIN_URL = "https://qalab.bensg.com/store/pe/iniciar-sesion?back=my-account";

    // Prioridad 1: By.id
    public static final By EMAIL_INPUT = By.id("field-email");
    public static final By PASSWORD_INPUT = By.id("field-password");
    public static final By LOGIN_BUTTON = By.id("submit-login");

    // Prioridad 2: By.cssSelector
    public static final By LOGIN_ERROR_ALERT = By.cssSelector(".alert.alert-danger");
}
