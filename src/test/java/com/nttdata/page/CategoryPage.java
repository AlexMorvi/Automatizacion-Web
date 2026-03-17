package com.nttdata.page;

import org.openqa.selenium.By;

public class CategoryPage {
    // Primer producto de la lista de categoria
    public static final By FIRST_PRODUCT = By.cssSelector(
            "#js-product-list .product-miniature:first-child a.product-thumbnail");
}
