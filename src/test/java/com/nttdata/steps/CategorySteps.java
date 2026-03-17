package com.nttdata.steps;

import com.nttdata.page.CategoryPage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CategorySteps {
    private WebDriver driver;
    private WebDriverWait wait;

    public CategorySteps(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToCategory(String category, String subcategory) {
        // Esperar que el top-menu sea visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#top-menu")));

        // Recoger todos los links de nivel 0 (data-depth="0") del menu principal
        List<WebElement> topLinks = driver.findElements(
                By.cssSelector("#top-menu > li > a[data-depth='0']"));

        // Buscar la categoria (case-insensitive: acepta "CLOTHES", "Clothes", "clothes")
        WebElement matchedCategory = null;
        for (WebElement link : topLinks) {
            if (link.getText().trim().equalsIgnoreCase(category)) {
                matchedCategory = link;
                break;
            }
        }

        // Falla inmediata con mensaje descriptivo si la categoria no existe en el menu
        Assert.assertNotNull(
                "La categoria '" + category + "' no existe en el menu de navegacion. "
                        + "Categorias disponibles: " + buildList(topLinks, false),
                matchedCategory);

        // Obtener el <li> padre ANTES del hover
        // Nota: By.xpath("./..") es el unico uso justificado de XPath ya que CSS
        // no tiene selector de elemento padre
        WebElement parentLi = matchedCategory.findElement(By.xpath("./.."));

        // Hacer hover sobre el <li> padre: el listener JS de mouseenter esta en .category <li>
        new Actions(driver).moveToElement(parentLi).perform();

        // Buscar los links de subcategoria DENTRO del parentLi usando CSS (no depende de IDs)
        // getAttribute("textContent") funciona incluso cuando el submenu esta oculto (display:none)
        List<WebElement> subLinks = parentLi.findElements(
                By.cssSelector(".sub-menu a[data-depth='1']"));

        Assert.assertFalse(
                "La categoria '" + category + "' no tiene subcategorias detectadas en el menu.",
                subLinks.isEmpty());

        // Localizar la subcategoria por textContent (robusto en elementos visibles u ocultos)
        WebElement subcategoryLink = null;
        String subcatHref = null;
        for (WebElement link : subLinks) {
            if (link.getAttribute("textContent").trim().equals(subcategory)) {
                subcategoryLink = link;
                subcatHref = link.getAttribute("href");
                break;
            }
        }

        Assert.assertNotNull(
                "Subcategoria '" + subcategory + "' no encontrada dentro de '" + category + "'. "
                        + "Subcategorias disponibles: " + buildList(subLinks, true),
                subcategoryLink);

        // Intentar click si el submenu fue desplegado por el hover (elemento visible)
        // Fallback: navegar via href si el hover no desplego el submenu
        // Ambos producen el mismo resultado funcional (se llega a la misma URL de subcategoria)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOf(subcategoryLink));
            subcategoryLink.click();
        } catch (TimeoutException e) {
            driver.get(subcatHref);
        }
    }

    private String buildList(List<WebElement> links, boolean useTextContent) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < links.size(); i++) {
            if (i > 0) sb.append(", ");
            String text = useTextContent
                    ? links.get(i).getAttribute("textContent").trim()
                    : links.get(i).getText().trim();
            sb.append("'").append(text).append("'");
        }
        sb.append("]");
        return sb.toString();
    }

    public void clickFirstProduct() {
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(CategoryPage.FIRST_PRODUCT));
        firstProduct.click();
    }
}
