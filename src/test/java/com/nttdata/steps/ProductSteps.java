package com.nttdata.steps;

import com.nttdata.page.ProductPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductSteps {
    private WebDriver driver;
    private WebDriverWait wait;

    public ProductSteps(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void setQuantity(int quantity) {
        WebElement qtyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(ProductPage.QUANTITY_INPUT));
        qtyInput.sendKeys(Keys.CONTROL + "a");
        qtyInput.sendKeys(String.valueOf(quantity));
    }

    public void addToCart() {
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(ProductPage.ADD_TO_CART_BUTTON));
        addBtn.click();
    }

    public double getProductPrice() {
        WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(ProductPage.PRODUCT_PRICE));
        // Intentar obtener el precio del atributo data-product-price (más confiable)
        String dataPrice = priceElement.getAttribute("content");
        if (dataPrice == null || dataPrice.isEmpty()) {
            dataPrice = priceElement.getAttribute("data-product-price");
        }
        if (dataPrice != null && !dataPrice.isEmpty()) {
            return Double.parseDouble(dataPrice);
        }
        return parsePrice(priceElement.getText());
    }

    public String getModalProductName() {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(ProductPage.MODAL));
        WebElement nameElement = modal.findElement(ProductPage.MODAL_PRODUCT_NAME);
        return nameElement.getText().trim();
    }

    public double getModalSubtotal() {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(ProductPage.MODAL));
        // Buscar todos los .value dentro de cart-content y tomar el subtotal
        List<WebElement> values = modal.findElements(By.cssSelector(".cart-content .value"));
        // El primer .value es el subtotal de productos
        if (!values.isEmpty()) {
            return parsePrice(values.get(0).getText());
        }
        // Fallback al localizador directo
        WebElement subtotalElement = modal.findElement(ProductPage.MODAL_SUBTOTAL);
        return parsePrice(subtotalElement.getText());
    }

    public void clickCheckout() {
        WebElement checkoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(ProductPage.MODAL_CHECKOUT_BUTTON));
        checkoutBtn.click();
    }

    private double parsePrice(String priceText) {
        // Paso 1: conservar solo digitos, comas y puntos
        String cleaned = priceText.replaceAll("[^0-9.,]", "").trim();

        int lastDot   = cleaned.lastIndexOf('.');
        int lastComma = cleaned.lastIndexOf(',');

        if (lastComma > lastDot) {
            // La coma es el separador decimal (ej: "19,12" o "1.912,20")
            // Los puntos son separadores de miles → eliminarlos; coma → punto
            cleaned = cleaned.replace(".", "").replace(",", ".");
        } else {
            // El punto es el separador decimal o solo hay puntos (ej: ".191.20" de "S/. 191.20")
            // Eliminar comas (separadores de miles si los hay)
            cleaned = cleaned.replace(",", "");
            // Si quedan multiples puntos: el ultimo es decimal, los anteriores son de miles
            if (cleaned.indexOf('.') != cleaned.lastIndexOf('.')) {
                int decimalPos = cleaned.lastIndexOf('.');
                String intPart = cleaned.substring(0, decimalPos).replace(".", "");
                String decPart = cleaned.substring(decimalPos + 1);
                cleaned = intPart + "." + decPart;
            }
        }

        // Eliminar puntos residuales al inicio o al final (ej: ".20" -> nunca deberia pasar)
        cleaned = cleaned.replaceAll("^\\.|\\.$", "");

        return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
    }
}
