package com.nttdata.page;

import org.openqa.selenium.By;

public class ProductPage {
    // Precio actual del producto
    public static final By PRODUCT_PRICE = By.cssSelector(".current-price-value");

    // Cantidad - Prioridad 1: By.id
    public static final By QUANTITY_INPUT = By.id("quantity_wanted");

    // Boton agregar al carrito
    public static final By ADD_TO_CART_BUTTON = By.cssSelector(".add-to-cart[data-button-action='add-to-cart']");

    // Modal popup despues de agregar al carrito - Prioridad 1: By.id
    public static final By MODAL = By.id("blockcart-modal");

    // Elementos dentro del modal
    public static final By MODAL_PRODUCT_NAME = By.cssSelector("#blockcart-modal .product-name");
    public static final By MODAL_SUBTOTAL = By.cssSelector("#blockcart-modal .subtotal.value");
    public static final By MODAL_CHECKOUT_BUTTON = By.cssSelector("#blockcart-modal .cart-content-btn a.btn-primary");
}
