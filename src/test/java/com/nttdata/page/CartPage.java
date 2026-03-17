package com.nttdata.page;

import org.openqa.selenium.By;

public class CartPage {
    // Titulo de la pagina
    public static final By PAGE_TITLE = By.cssSelector("h1.h1");

    // Precio unitario del producto en el carrito
    public static final By PRODUCT_UNIT_PRICE = By.cssSelector(".cart-item .product-price .price");

    // Cantidad del producto en el carrito
    public static final By PRODUCT_QUANTITY = By.cssSelector(".cart-item .js-cart-line-product-quantity");

    // Subtotal de productos - Prioridad 1: By.id compuesto con CSS
    public static final By CART_SUBTOTAL = By.cssSelector("#cart-subtotal-products .value");
}
