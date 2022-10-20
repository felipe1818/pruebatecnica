package StepDefinitions;

import Functions.SeleniumFunctions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.io.IOException;


public class PropertiesScenario {

    SeleniumFunctions functions = new SeleniumFunctions();

    @Dado("inicio sesion en el aplicativo con email y clave")
    public void iniciosesioaplicativo() throws Exception {
        functions.switchToNewTab("https://www.saucedemo.com/", "suacedemo");
        functions.iLoadTheDOMInformation("Principal.json");
        functions.iClicInElement("email");
        functions.iSetElementWithText("email", "standard_user");
        functions.iClicInElement("clave");
        functions.iSetElementWithText("clave", "secret_sauce");
        functions.attachScreenShot();
        functions.iClicInElement("login");
        functions.iWaitTime(4);
        functions.attachScreenShot();
    }

    @Cuando("ingresa las credenciales correctas podra visualizar los articulos")
    public void ingresacredenciales() throws IOException {
        functions.attachScreenShot();
    }

    @Entonces("podremos agregar un articulo")
    public void podremosagregar() throws Exception {
        functions.iClicInElement("articulo1");
        functions.iClicInElement("articulo2");
        functions.attachScreenShot();
    }

    @Dado("ya agregado el articulo podremos verificarlo")
    public void agregadoarticuloverificar() throws Exception {
        functions.iClicInElement("carrito");
        functions.attachScreenShot();
        functions.iClicInElement("verificar");
    }

    @Cuando("ingresar primer nombre segundo nombre y codigo postal")
    public void ingresarnombrecodigo() throws Exception {
        functions.attachScreenShot();
        functions.iClicInElement("nombreP");
        functions.iSetElementWithText("nombreP", "Johan");
        functions.iClicInElement("nombreS");
        functions.iSetElementWithText("nombreS", "Felipe");
        functions.iClicInElement("codigo");
        functions.iSetElementWithText("codigo", "110321");
        functions.attachScreenShot();
    }

    @Entonces("verificar el articulo y visualizar la pantalla descripcion")
    public void verificararticulo() throws Exception {
        functions.iClicInElement("continuar");
        functions.iWaitTime(5);
        functions.attachScreenShot();
    }

    @Dado("visualizar descripcion del articulo")
    public void visualizardescripcion() throws IOException, InterruptedException {
        functions.attachScreenShot();
        functions.iWaitTime(5);
    }

    @Cuando("si el articulo es correcto finalizaremos")
    public void articulocorrecto() throws Exception {
        functions.iClicInElement("finalizar");
    }

    @Entonces("vizualizar la pantalla cuando finalizemos la compra")
    public void vizualizarpantalla() throws Exception {
        functions.attachScreenShot();
        functions.iWaitTime(5);
        functions.iClicInElement("home");
        functions.iWaitTime(5);
        functions.attachScreenShot();
    }

    @Cuando("cancelar el articulo")
    public void cancelararticulo() throws Exception {
        functions.iClicInElement("cancelar");
        functions.iWaitTime(5);
    }

    @Entonces("vizualizar la pantalla principal y cerrar sesion")
    public void vizualizarpantallaprincipal() throws Exception {
        functions.attachScreenShot();
        functions.iWaitTime(5);
        functions.iClicInElement("menu");
        functions.attachScreenShot();
        functions.iClicInElement("logout");
        functions.iWaitTime(5);
        functions.attachScreenShot();
    }

}
