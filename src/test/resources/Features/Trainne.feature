# language: es
@InicioSesion
Característica: Inicio de sesión
  Como un usuario de la entidad cuando ingrese credenciales entonces ingreso al aplicativo.

  Antecedentes:
    #Dados los datos del ambiente y usuarios del sistema

    @AgregararticuloComprarlo
  Escenario: Iniciar sesion y agregar y comprar un articulo
      Dado inicio sesion en el aplicativo con email y clave
      Cuando ingresa las credenciales correctas podra visualizar los articulos
      Entonces podremos agregar un articulo
      Dado ya agregado el articulo podremos verificarlo
      Cuando  ingresar primer nombre segundo nombre y codigo postal
      Entonces verificar el articulo y visualizar la pantalla descripcion
      Dado visualizar descripcion del articulo
      Cuando si el articulo es correcto finalizaremos
      Entonces vizualizar la pantalla cuando finalizemos la compra


  @AgregararticuloCancelarlo
  Escenario: Iniciar sesion y cancelar un articulo
    Dado inicio sesion en el aplicativo con email y clave
    Cuando ingresa las credenciales correctas podra visualizar los articulos
    Entonces podremos agregar un articulo
    Dado ya agregado el articulo podremos verificarlo
    Cuando  ingresar primer nombre segundo nombre y codigo postal
    Entonces verificar el articulo y visualizar la pantalla descripcion
    Dado visualizar descripcion del articulo
    Cuando cancelar el articulo
    Entonces vizualizar la pantalla principal y cerrar sesion
      
      