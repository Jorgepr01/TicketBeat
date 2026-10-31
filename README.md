# TicketBeat

Descripción

Proyecto del grupo 6 de Diseño de Software. Es un prototipo en Java para buscar eventos musicales, reservar entradas, simular pagos y consultar boletos comprados.

## Integrantes

Adriel David Celi Aguirre, Paulo José Lema Romero, Jorge Martín Santamaría Navia y Ricardo Andrés Soledispa Pihuave.

Estado del proyecto

Los cuatro casos de uso principales funcionan mediante una demostración por consola. Los datos se guardan en memoria mientras se ejecuta el programa. Los pagos, reembolsos y canales de notificación son simulados. No se realizan cobros ni se envían mensajes reales.

## Búsqueda de eventos

Se pueden buscar eventos por nombre, artista, fecha, lugar o género musical. Las búsquedas sin coincidencias devuelven una lista vacía. La demostración muestra la fecha, el lugar y la disponibilidad de una presentación.

## Reservas

El sistema comprueba el tipo de boleto, la cantidad disponible y el límite acumulado por usuario para un mismo evento. Una reserva dura quince minutos por defecto. Los boletos reservados dejan de estar disponibles para otros compradores. Cancelar o vencer una reserva devuelve sus boletos al inventario.

GestorReservas verifica los vencimientos cada segundo mientras está activo. También se comprueba la vigencia antes de pagar y al intentar nuevas reservas. Las reservas simultáneas gestionadas por el servicio se coordinan para no asignar el mismo boleto a dos compradores.

## Compras y pagos

La compra calcula el total de los boletos. Solo puede confirmarse con un pago aprobado y una reserva vigente. Se incluyen procesadores simulados para tarjeta, transferencia y pago móvil. Un pago rechazado permite reintentar mientras la reserva esté vigente. Una compra confirmada no vuelve a cobrarse.

Si la reserva deja de poder confirmarse durante el procesamiento de un pago aprobado, se simula el reembolso y se cancela la compra. Si falla el canal de notificación, la compra aprobada conserva su estado.

## Boletos y soporte

Cada usuario consulta únicamente los boletos vendidos de sus compras confirmadas. Los boletos tienen un código identificador. También se incluye el registro, escalamiento y resolución de incidentes, junto con reembolsos simulados que no pueden superar el importe pagado.

## Organización

La carpeta src contiene App.java y los paquetes de ticketbeat. modelo contiene las clases del sistema. servicios contiene las búsquedas, reservas, compras, pagos y consultas. pagos contiene la interfaz ProcesadorPago y sus implementaciones. notificaciones contiene la interfaz Notificador y sus canales. enums contiene los tipos y estados.

La carpeta test contiene las pruebas del prototipo. La carpeta bin contiene los archivos compilados y está excluida de Git. No se necesitan librerías externas.

## Compilación

Se requiere JDK 17. Desde la carpeta TicketBeat, ejecutar estos comandos en orden. En Visual Studio Code también se puede ejecutar el método main de App.java.

mkdir -p bin

find src -name '*.java' > bin/fuentes.txt

javac -encoding UTF-8 -d bin @bin/fuentes.txt

java -cp bin App

Pruebas

Después de compilar el proyecto, ejecutar estos dos comandos.

javac -encoding UTF-8 -cp bin -d bin test/PruebasTicketBeat.java

java -cp bin PruebasTicketBeat

Las pruebas comprueban los cuatro casos de uso, las búsquedas sin resultados, los límites acumulados, el inventario agotado, las cancelaciones, los pagos rechazados, los reintentos, las reservas vencidas, las notificaciones y las devoluciones. También se comprueba que dos compradores no reserven el último boleto y que dos reembolsos no superen el monto pagado. La prueba del fallo de notificación muestra un aviso esperado en la consola.

## Correspondencia con los diagramas

Se usaron las dos imágenes del diagrama de clases: la PNG para los atributos y métodos y la JPEG para las relaciones. Las asociaciones se representan con referencias a objetos y listas.

Se corrigió ResrvaService como ReservaService y se unificó TipoBooleto como TipoBoleto. Date se representa con LocalDate y DateTime con LocalDateTime. generoMusical en Evento se interpretó como atributo. consultarDisponibilidad en Presentacion devuelve int y se mantiene una referencia a Lugar.

MetodoPago conserva las tres opciones del diagrama. Los valores de las otras enumeraciones son decisiones del prototipo porque las imágenes no los detallan. El tiempo restante de una reserva se expresa en milisegundos.

Se agregaron constructores, métodos de consulta, referencias necesarias para las asociaciones y dependencias de los servicios. actualizarDatos y actualizarInformacion tienen versiones con parámetros para recibir cambios. cambiarContrasena necesita un String con el nuevo valor; su versión sin parámetros informa que debe usarse la versión con datos. registrarResultado permite indicar si el pago fue aprobado o rechazado; su versión sin parámetros aprueba el pago simulado.

Estos ajustes deben reflejarse en el UML final. Los diagramas de secuencia deben usar los mismos nombres y métodos que el código. La implementación organiza los flujos con servicios; los nombres de vistas, controladores y base de datos del avance de secuencias todavía deben coordinarse con el grupo.

## Alcance

Es un prototipo por consola para la sección D. No incluye interfaz web o móvil, base de datos, inicio de sesión real ni servicios externos. Los datos y los identificadores se reinician al cerrar el programa. Las políticas se representan en el modelo, pero la gestión completa de cancelaciones de eventos, cambios de fecha e intercambios de boletos queda fuera de esta demostración inicial. La contraseña del modelo usa datos ficticios; no se implementó un sistema de autenticación.

# Integrantes
- Adriel Celi
- Ricardo Soledispa 
- Paulo Lema
- Jorge Santamaria
