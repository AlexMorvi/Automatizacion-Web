# Automatización Web - QA Lab Store

Proyecto de automatización funcional sobre [qalab.bensg.com/store](https://qalab.bensg.com/store/pe/iniciar-sesion) usando Selenium + Cucumber + JUnit 4.

## Stack

- Java 17
- Selenium 4.8.0
- Cucumber 7.3.4
- JUnit 4.13.1
- Maven

## ¿Qué automatiza?

Flujo de compra en una tienda PrestaShop:

1. Login (credenciales válidas e inválidas)
2. Navegación por categoría y subcategoría (válidas e inexistentes)
3. Agregar N unidades del primer producto al carrito
4. Validar nombre, precio y subtotal en el popup de confirmación
5. Validar título de página del carrito y cálculo de precios

El escenario usa `Scenario Outline` con 4 ejemplos que cubren el flujo positivo y tres casos negativos (email con formato inválido, contraseña incorrecta, categoría inexistente). Los casos negativos se marcan como **SKIPPED** en el reporte, no como fallidos.

## Estructura

```
src/test/java/com/nttdata/
├── core/           DriverManager (lifecycle del WebDriver + screenshots)
├── page/           Locators por pantalla (solo By, sin lógica)
├── steps/          Lógica de interacción con el navegador
├── stepsdefinitions/  Bindings Gherkin → steps
└── runner/         RunnerTest (@loginMyStore)

src/test/resources/
├── features/       myStore.feature
└── extent.properties
```

## Requisitos previos

- Descargar [ChromeDriver](https://chromedriver.chromium.org/downloads) compatible con tu versión de Chrome
- Colocarlo en `drivers/chromedriver.exe` (Windows) o `drivers/chromedriver` (Mac/Linux)

## Ejecución

Abrir `RunnerTest.java` en IntelliJ y ejecutar con el botón Run (▶) o `Shift+F10`.

Alternativamente desde terminal:

```bash
mvn test
```

## Reportes

Al finalizar la ejecución se generan en `target/`:

| Archivo | Ruta |
|---|---|
| Cucumber HTML | `target/cucumber/cucumber-report.html` |
| Cucumber JSON | `target/cucumber/cucumber.json` |
| Extent Spark | `target/extent-reports/spark-report.html` |
| **PDF** | `target/extent-reports/cucumber-report.pdf` |
