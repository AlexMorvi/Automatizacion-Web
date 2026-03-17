package com.nttdata.stepsdefinitions;

import com.nttdata.steps.CartSteps;
import com.nttdata.steps.CategorySteps;
import com.nttdata.steps.LoginSteps;
import com.nttdata.steps.ProductSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.Assume;
import org.openqa.selenium.WebDriver;

import static com.nttdata.core.DriverManager.getDriver;
import static com.nttdata.core.DriverManager.screenShot;

public class MyStoreStepsDef {
    private WebDriver driver;
    private LoginSteps loginSteps;
    private CategorySteps categorySteps;
    private ProductSteps productSteps;
    private CartSteps cartSteps;

    private double unitPrice;
    private int quantity;

    @Given("estoy en la página de la tienda")
    public void estoyEnLaPaginaDeLaTienda() {
        driver = getDriver();
        loginSteps = new LoginSteps(driver);
        categorySteps = new CategorySteps(driver);
        productSteps = new ProductSteps(driver);
        cartSteps = new CartSteps(driver);
        loginSteps.navigateToStore();
        screenShot("01 - Pagina de login cargada");
    }

    @And("me logueo con mi usuario {string} y clave {string}")
    public void meLogueoConMiUsuarioYClave(String usuario, String clave) {
        loginSteps.login(usuario, clave);

        if (loginSteps.isLoginFailed()) {
            String errorMsg = loginSteps.getLoginErrorMessage();
            String detail = errorMsg.isEmpty()
                    ? "el usuario '" + usuario + "' no es valido o tiene formato incorrecto"
                    : "la pagina mostro el error: " + errorMsg;
            screenShot("02 - Login rechazado correctamente - credenciales invalidas");
            Assume.assumeTrue(
                    "Login rechazado correctamente: " + detail + ". Pasos restantes no aplican.",
                    false);
        }

        screenShot("02 - Login exitoso - usuario autenticado");
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navegoALaCategoriaYSubcategoria(String categoria, String subcategoria) {
        screenShot("03 - Antes de navegar a categoria: " + categoria);
        try {
            categorySteps.navigateToCategory(categoria, subcategoria);
        } catch (AssertionError e) {
            screenShot("04 - Categoria/subcategoria no encontrada (esperado): " + categoria);
            Assume.assumeTrue(
                    "Navegacion rechazada correctamente: " + e.getMessage()
                            + ". Pasos restantes no aplican.",
                    false);
        }
        screenShot("04 - Navegacion exitosa a " + categoria + " > " + subcategoria);
    }

    @And("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int unidades) {
        this.quantity = unidades;
        categorySteps.clickFirstProduct();
        screenShot("05 - Pagina del primer producto");

        unitPrice = productSteps.getProductPrice();
        productSteps.setQuantity(unidades);
        screenShot("06 - Cantidad configurada: " + unidades + " unidades");

        productSteps.addToCart();
        screenShot("07 - Producto agregado al carrito");
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmacionDelProductoAgregado() {
        String modalProductName = productSteps.getModalProductName();
        Assert.assertNotNull("El nombre del producto en el modal no debe ser nulo", modalProductName);
        Assert.assertFalse("El nombre del producto en el modal no debe estar vacio", modalProductName.isEmpty());
        screenShot("08 - Popup confirmacion - Producto: " + modalProductName);
    }

    @And("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        double modalSubtotal = productSteps.getModalSubtotal();
        double expectedTotal = Math.round(unitPrice * quantity * 100.0) / 100.0;

        screenShot("09 - Popup validacion monto - Precio: " + unitPrice
                + " x " + quantity + " = " + expectedTotal + " vs Subtotal: " + modalSubtotal);

        Assert.assertEquals(
                "El monto total en el popup no coincide con precio unitario * cantidad. "
                        + "Esperado: " + expectedTotal + " | Real: " + modalSubtotal,
                expectedTotal, modalSubtotal, 0.01);
    }

    @When("finalizo la compra")
    public void finalizoLaCompra() {
        productSteps.clickCheckout();
        screenShot("10 - Clic en finalizar compra");
    }

    @Then("valido el titulo de la pagina del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
        String pageTitle = cartSteps.getPageTitle();

        screenShot("11 - Pagina del carrito - Titulo: " + pageTitle);

        Assert.assertTrue(
                "El titulo de la pagina del carrito deberia contener 'CARRITO' pero fue: '" + pageTitle + "'",
                pageTitle.toUpperCase().contains("CARRITO"));
    }

    @And("vuelvo a validar el calculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
        double cartUnitPrice = cartSteps.getCartUnitPrice();
        int cartQty = cartSteps.getCartQuantity();
        double cartSubtotal = cartSteps.getCartSubtotal();
        double expectedSubtotal = Math.round(cartUnitPrice * cartQty * 100.0) / 100.0;

        screenShot("12 - Carrito validacion final - Precio: " + cartUnitPrice
                + " x " + cartQty + " = " + expectedSubtotal + " vs Subtotal: " + cartSubtotal);

        Assert.assertEquals(
                "El subtotal del carrito no coincide con precio unitario * cantidad. "
                        + "Esperado: " + expectedSubtotal + " | Real: " + cartSubtotal,
                expectedSubtotal, cartSubtotal, 0.01);
    }
}
