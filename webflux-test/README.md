# Prueba Seti / Manuel Santiago Chaparro Rojas

Este proyecto se realizo con el objetivo de completar el reto llamado "PruebaNequi" donde se exponen las siguientes habilidades:

## Arquitectura

- Manejo de librería reactor Webflux
- Integración de Scaffolding Clean Architecture
- Uso de operadores map, flatMap, zip, switchIfEmpty
- Uso de señales onNext, onComplete, onError
- Uso de librería slf4j para manejo de logs (Implementado para errores y simulación de correos)
- Pruebas unitarias con mas de un 60% de coverage total (Integración con Jacoco)

## Puntos adicionales completados

| Actividad                                |  Terminado  |
|------------------------------------------|-------------|
| Dockerizacion                            |    Si    |
| Actualización de nombre para Franquicia  |    Si    |
| Actualización de nombre para Sucursal    |    Si    |
| Actualización de nombre para Producto    |    Si    |
| Despliegue en la nube                    |    Si    |
| Consideraciones de diseño                |    Si    |

# Cómo desplegar - Docker

El desarrollo cuenta con despliegue automatico con Docker, dentro del cual se pueden encontrar los siguientes archivos

- docker-compose.yml
- webflux-test/seti-webflux.dockerfile

Dentro de la carpeta raiz, a nivel del archivo **docker-compose.yml** se ejecuta el siguiente comando

```shell
docker-compose build --no-cache
```

El cual crea compila y crea la imagen del proyecto webflux-test, dentro, se encuentra empaquetadp todo el entorno de la aplicación

Para ejecutar el servicio docker, se ejecuta el siguiente comando

```shell
docker-compose up
```

Una vez que nuestra aplicación se encuentra en funcionamiento dentro de nuestro entorno local, es posible consumir nuestra API REST apuntando a **http://localhost:3000/api**

## !! Importante

El archivo **.env** debe existir al mismo nivel del archivo docker-compose.yml, el cual contiene las credenciales de conexión a la base de datos (Por motivos de seguridad, el archivo .env no se debería subir al repositorio, sin embargo por terminos prácticos y de entrega, se sube el archivo para facilidad de implementación)

# Consumo de API REST

## Consideraciones de diseño

