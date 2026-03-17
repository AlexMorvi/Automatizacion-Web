package com.nttdata.steps;

import com.nttdata.page.CartPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartSteps {
    private WebDriver driver;
    private WebDriverWait wait;

    public CartSteps(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getPageTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(CartPage.PAGE_TITLE));
        return title.getText().trim();
    }

    public double getCartUnitPrice() {
        WebElement priceElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(CartPage.PRODUCT_UNIT_PRICE));
        return parsePrice(priceElement.getText());
    }

    public int getCartQuantity() {
        WebElement qtyElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(CartPage.PRODUCT_QUANTITY));
        String value = qtyElement.getAttribute("value");
        if (value != null && !value.isEmpty()) {
            return Integer.parseInt(value.trim());
        }
        return Integer.parseInt(qtyElement.getText().trim());
    }

    public double getCartSubtotal() {
        WebElement subtotalElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(CartPage.CART_SUBTOTAL));
        return parsePrice(subtotalElement.getText());
    }

    private double parsePrice(String priceText) {
        // Paso 1: conservar solo digitos, comas y puntos
        String cleaned = priceText.replaceAll("[^0-9.,]", "").trim();

        int lastDot   = cleaned.lastIndexOf('.');
        int lastComma = cleaned.lastIndexOf(',');

        if (lastComma > lastDot) {
            // La coma es el separador decimal (ej: "19,12" o "1.912,20")
            cleaned = cleaned.replace(".", "").replace(",", ".");
        } else {
            // El punto es el separador decimal o solo hay puntos (ej: ".191.20" de "S/. 191.20")
            cleaned = cleaned.replace(",", "");
            if (cleaned.indexOf('.') != cleaned.lastIndexOf('.')) {
                int decimalPos = cleaned.lastIndexOf('.');
                String intPart = cleaned.substring(0, decimalPos).replace(".", "");
                String decPart = cleaned.substring(decimalPos + 1);
                cleaned = intPart + "." + decPart;
            }
        }

        cleaned = cleaned.replaceAll("^\\.|\\.$", "");

        return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
    }
}
