package Tasks;

import Functions.CreateDriver;
import Functions.SeleniumFunctions;
import Integration.Conexion;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class ApiRequest {

SeleniumFunctions functions = new SeleniumFunctions();
    Conexion con = new Conexion();

    public void pagarFactura() throws Exception {
        String Environment = CreateDriver.defaultAmbiente();
        String endPoint="";
        if(Environment.equals("QAInvima")) {
            endPoint = "http://srvpago-qa-transversal.apps.ocp4devqa.invima.gov.co/v1/Pago/registroPagoRealizado";
        }  else  if(Environment.equals("QAInterno")) {
            endPoint = "http://srvpago-qa-transversal.apps.openshiftdev.soain.lcl/v1/Pago/registroPagoRealizado";
        }  else  if(Environment.equals("Desarrollo")) {
            endPoint = "http://srvpago-des-transversal.apps.openshiftdev.soain.lcl/v1/Pago/registroPagoRealizado";
        }

        String numeroSolicitud = functions.ScenaryData.get("Numero de Solicitud");
        String numeroRecibo = con.obtenerCodigoFactura(numeroSolicitud);
        int ceros = 14-numeroRecibo.length();
        numeroRecibo = ("0".repeat(ceros)+numeroRecibo);
        System.out.println(numeroRecibo);
        functions.iSaveTextInScenario("Numero Recibo",numeroRecibo);


        int valor = Integer.parseInt(functions.ScenaryData.get("Tarifa"));
        String numDoc = functions.ScenaryData.get("NumeroDocEmpresa");

    String response = given()
            .log().all()
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "\"objAuditoria\":{\n" +
                    "\"usuario\":\"tramitesgestor22@gmail.com\",\n" +
                    "\"ip\":\"181.49.173.42\"\n" +
                    "},\n" +
                    "\"objOperacion\":{\n" +
                    "\"codigoEstado\":\"APR\",\n" +
                    "\"estado\":\"PAGO APROBADO\",\n" +
                    "\"NumeroReciboPago\":\""+numeroRecibo+"\",\n" +
                    "\"montoPagado\": "+valor+",\n" +
                    "\"NumeroDocumento\":\""+numDoc+"\"\n" +
                    "}\n" +
                    "}")
            .post(endPoint)
            .then()
            .log().all()
            .extract()
            .asString();
        System.out.println(response);
    }

    public void simularRPA() throws IOException {
        String radicado = functions.ScenaryData.get("Radicado");
        String idTramite = con.obtenerIdTramite(radicado);

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://srvtipodocumental-qa-transversal.apps.ocp4devqa.invima.gov.co/v1/TipoDocumental/SimularRPA/"+idTramite)
                .then()
                .log().all()
                .extract().response();
    }



    public void pagarFactura(String numeroRecibo,int valor,String numDoc) throws Exception {

        String Environment = CreateDriver.defaultAmbiente();
        String endPoint="";
        if(Environment.equals("QAInvima")) {
            endPoint = "http://srvpago-qa-transversal.apps.ocp4devqa.invima.gov.co/v1/Pago/registroPagoRealizado";
        }  else  if(Environment.equals("QAInterno")) {
            endPoint = "http://srvpago-qa-transversal.apps.openshiftdev.soain.lcl/v1/Pago/registroPagoRealizado";
        }  else  if(Environment.equals("Desarrollo")) {
            endPoint = "http://srvpago-des-transversal.apps.openshiftdev.soain.lcl/v1/Pago/registroPagoRealizado";
        }

        String response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\"objAuditoria\":{\n" +
                        "\"usuario\":\"tramitesgestor22@gmail.com\",\n" +
                        "\"ip\":\"181.49.173.42\"\n" +
                        "},\n" +
                        "\"objOperacion\":{\n" +
                        "\"codigoEstado\":\"APR\",\n" +
                        "\"estado\":\"PAGO APROBADO\",\n" +
                        "\"NumeroReciboPago\":\""+numeroRecibo+"\",\n" +
                        "\"montoPagado\": "+valor+",\n" +
                        "\"NumeroDocumento\":\""+numDoc+"\"\n" +
                        "}\n" +
                        "}")
                .post(endPoint)
                .then()
                .log().all()
                .extract()
                .asString();
        System.out.println(response);
        functions.iSaveTextInScenario("Codigo", response);
    }
}