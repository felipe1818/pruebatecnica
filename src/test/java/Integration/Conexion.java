package Integration;

import Functions.CreateDriver;
import Functions.SeleniumFunctions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

    public class Conexion {


        String connectionUrl = "";
        String value = null;

        SeleniumFunctions functions = new SeleniumFunctions();

        public String ambienteCon() throws IOException {
            String Environment = CreateDriver.defaultAmbiente();
            if(Environment.equals("QAInvima")) {
                System.out.println("Conectando a BD "+Environment);
                connectionUrl = "jdbc:sqlserver://192.168.10.198:1433;databaseName=tramites;user=usr_admin_tramites;password=usr_admin_tramites*;encrypt=true;trustServerCertificate=true";
            } else  if(Environment.equals("QAInterno")) {
                System.out.println("Conectando a BD "+Environment);
                connectionUrl = "jdbc:sqlserver://192.168.1.23:1433;databaseName=tramites;user=jvillamil;password=Colombia123;encrypt=true;trustServerCertificate=true";
            }  else if(Environment.equals("Desarrollo")) {
                System.out.println("Conectando a BD "+Environment);
                connectionUrl = "jdbc:sqlserver://192.168.1.35:1433;databaseName=tramites;user=jvillamil;password=Colombia123;encrypt=true;trustServerCertificate=true";
            } else  if(Environment.equals("Produccion")) {
                System.out.println("Conectando a BD "+Environment);
                connectionUrl = "jdbc:sqlserver://192.168.10.198:1433;databaseName=tramites;user=usr_admin_tramites;password=usr_admin_tramites*;encrypt=true;trustServerCertificate=true";
            }
            return connectionUrl;
        }

        public String obtenerCodigoFactura(String numeroSolicitud) throws IOException {
             connectionUrl = ambienteCon();
            try {
                // Load SQL Server JDBC driver and establish connection.
                System.out.print("Connecting to SQL Server ... ");
                try (Connection connection = DriverManager.getConnection(connectionUrl)) {
                    System.out.println("Conectado Correctamente.");
                    Statement stmt = connection.createStatement();
                    String query = "SELECT NumeroReciboPago  FROM tramite.Pago p  INNER JOIN tramite.Solicitud s ON s.IdSolicitud  = p.IdSolicitud  WHERE s.NumeroSolicitud  = "+numeroSolicitud+" ";
                    ResultSet rs =  stmt.executeQuery(query);
                    while (rs.next())
                    {
                        value = String.valueOf(rs.getInt(1));
                        System.out.println(value);
                    }
                }
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
            }

            return  value;


        }

        public String obtenerIdTramite(String radicado) throws IOException {
            connectionUrl = ambienteCon();
            try {
                // Load SQL Server JDBC driver and establish connection.
                System.out.print("Connecting to SQL Server ... ");
                try (Connection connection = DriverManager.getConnection(connectionUrl)) {
                    System.out.println("Conectado Correctamente.");
                    Statement stmt = connection.createStatement();
                    String query = "SELECT IdTramite  FROM tramite.Tramite t WHERE Radicado = '"+radicado+"'";
                    ResultSet rs =  stmt.executeQuery(query);
                    while (rs.next())
                    {
                        value = rs.getString(1);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }

            return  value;


        }

        public String obtenerCodigoOTP(String numeroSolicitud) throws IOException {
            connectionUrl = ambienteCon();
            try {
                // Load SQL Server JDBC driver and establish connection.
                System.out.print("Connecting to SQL Server ... ");
                try (Connection connection = DriverManager.getConnection(connectionUrl)) {
                    System.out.println("Conectado Correctamente.");
                    Statement stmt = connection.createStatement();
                    String query = "SELECT TOP 1 CodigoOtp FROM Maestra.dbo.MA_FirmaOtp p  INNER JOIN tramite.Solicitud s ON s.IdSolicitud  = p.IdSolicitud WHERE s.NumeroSolicitud  = "+numeroSolicitud+" ORDER BY p.IdFirmaOtp DESC";
                    ResultSet rs =  stmt.executeQuery(query);
                    while (rs.next())
                    {
                        value = String.valueOf(rs.getInt(1));
                        System.out.println(value);
                    }
                }
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
            }

            return  value;


        }


    }
