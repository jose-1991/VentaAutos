Una empresa de venta de autos, realiza la venta de vehiculos via online, donde diariamente publica diferentes tipos de vehiculos
y tanto los vendedores como compradores interacturan  para obtener el mejor precio para que les convenga, ya que la venta es via
online, puede existir muchos compradores interesados en un mismo vehiculo, asi como puede existir vehiculos sin ninguna comprador
interesado. Por eso se cuentan los dias que un vehiculo esta publicado y no tiene ninguna oferta, en caso de existir un vehiculo 
con cero ofertas por mas de 5 dias entonces se da de baja al vehiculo de la pagina y el vendedor tiene la opcion de republicar 
el vehiculo siempre y cuando el nuevo precio sea menor al anterior publicado, un vehiculo dado de baja puede ser republicado cuantas
veces sea necesario, no hay limite.
Los datos requeridos para cada vehiculo son:
vehicleStockNumber : 535f901e-fd36-455f-a44b-1831ab6177d6
vehicleVinNumber   :  1G1RC6E42BU489241
vehicleMake
vehicleModel
vehicleYear

1er Paso: registrar Usuarios ->
2do Paso: Publicacion Vehiculo -> Vendedor, Vehiculo, fechaPublicacion
3er Paso: interaccion Vendedor-comprador -> Publicacion, comprador, oferta



Para todos los usuarios, ya sea vendedor o comprador se requiere el nombre, el apellido, el ci ( o alguna forma de identificacion)
y correo electronico de manera obligatoria, el telefono movil es opcional, si el usuario provee el telefono movil acepta 
y da su consentimiento para recibir mensajes de texto de la empresa.
El usuario por defecto se subscribe a recibir todos los tipos de notificationes via emails de la empresa y puede unsuscribirse 
de cualquier notification o todas mas adelante. En cuanto a mensajes de texto el usuario no esta suscrito a ninguna notification 
via texto por defecto, el usuario decide a que notificationes se suscribe, como tambien puede unsuscribirse de cualquier tipo de notification
o todas via texto en cualquier momento.

Las notificationes para vendedores son:

TipoNotificacion,EnvioEmail,EnvioSms
CompradorPrimeraOferta,true,true
CompradorNuevaOferta, true, true
CompradorAcceptaOferta,true,true
CompradorRetiraOferta,true,false
VehiculoExpirado,true,false

Las notificationes para compradores son:

TipoNotificacion,EnvioEmail,EnvioSms
NuevoVehiculoEnVenta,true,true
VendedorContraoferta,true,false
VendedorAceptaOferta,true,true
VehiculoNoDisponible,true,false

Si el usuario ha solicitado unsuscribirse de alguna notification via texto o todas, la empresa no deberia enviar ningun mensaje
de texto de la/las notificationes unsuscritas a partir de ese momento. En caso de enviar un mensaje de texto a un usuario que se
unsubcrito anteriorment la empresa recibe una multa de $100 por cada mensaje de texto enviado.
En el caso de emails, es similar, pero en caso de la empresa continue enviando emails la empresa recibira una multa de $100 
por cada 100 emails enviados.