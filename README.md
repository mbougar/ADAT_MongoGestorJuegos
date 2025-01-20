### Teoría

Responde, usando tus palabras, a las siguientes preguntas.

#### a) ¿Qué ventajas e inconvenientes encuentras al usar una base de datos documental con MongoDB?
**Respuesta: Mongo ofrece una base de datos más flexible y fácil de comprender a cambio de necesitar validaciones manuales para mantener la estructura de datos. Además son más rápidas que las bases de datos relaciones, aunque no lo he podido diferenciar en un proyecto pequeño como este.**

#### b) ¿Cuáles han sido los principales problemas que has tenido al desarrollar la aplicación?
**Respuesta: En varias ocasiones he introducido mal alguna clave de la base de datos y he tardado un poco en darme cuenta de que estaba generando errores.**

#### c) ¿Cómo solucionarías el problema de la inconsistencia de datos en una base de datos documental? (El hecho de que en los documentos de una colección no sea obligatorio seguir un esquema único)
**Respuesta: Los problemas de inconsistencia de datos se resuelven mediante las validaciones de los datos introducidos, en el caso de esta aplicación por ejemplo, cuando pido un género puedo convertirlo en mayúsculas y asi forzar que los géneros siempre sigan la misma estructura.**
