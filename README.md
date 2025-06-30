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

Para el consumo de la API, se puede realizar mediante los dos siguientes host

| Ambiente            |  Host                        |
|---------------------|------------------------------|
| Despliegue Local    | http://localhost:3000/api    |
| AWS                 | http://3.148.244.6:3000/api  |

Para efectos prácticos, la implementación de las API por defecto se dejan documentadas apuntando a los servicios de AWS

# Franquicia

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Franquicia | POST             | http://3.148.244.6:3000/api/franchise |

```json
{
    "name": "Tienda Electrónica",
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Franquicia | PUT              | http://3.148.244.6:3000/api/franchise |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Electronics.co",
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Lista de Franquicias  | GET              | http://3.148.244.6:3000/api/franchise |

| Funcionalidad                           | Tipo de Petición |  API                                                                        |
|-----------------------------------------|------------------|-----------------------------------------------------------------------------|
| Productos con mayor stock por sucursal  | GET              | http://3.148.244.6:3000/api/franchise/{id_franquicia}/productsWithMoreStock |

# Sucursal

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Sucursal   | POST             | http://3.148.244.6:3000/api/branch    |

```json
{
    "name": "Electronics - Sede Medellín",
    "franchiseId": 1
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Sucursal   | PUT              | http://3.148.244.6:3000/api/branch    |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Rebranding / ElectronInc - Sede Tunja",
    "franchiseId": 2
}
```

| Funcionalidad         | Tipo de Petición |  API                               |
|-----------------------|------------------|------------------------------------|
| Lista de Sucursales   | GET              | http://3.148.244.6:3000/api/branch |

# Producto

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Producto   | POST             | http://3.148.244.6:3000/api/product   |

```json
{
    "name": "Laptop",
    "stock": 100,
    "branch": 1
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Producto   | PUT              | http://3.148.244.6:3000/api/product   |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Asus Vivobook 2025 32GB RAM 1TB SSD",
    "branch": null //(No es necesario actualizar el objeto del que depende)
}
```

| Funcionalidad         | Tipo de Petición |  API                                |
|-----------------------|------------------|-------------------------------------|
| Lista de Productos    | GET              | http://3.148.244.6:3000/api/product |

| Funcionalidad                    | Tipo de Petición   |  API                                                                                    |
|----------------------------------|--------------------|-----------------------------------------------------------------------------------------|
| Actualizar Stock de Productos    | PATCH              | http://3.148.244.6:3000/api/product/{id_del_producto}/stock/{cantidad_a_sumar_o_restar} |

Para sumar 20 productos de stock al producto

```shell
    http://3.148.244.6:3000/api/product/1/stock/20
```

Para restar 10 productos de stock al producto

```shell
    http://3.148.244.6:3000/api/product/1/stock/-10
```

## Consideraciones de diseño

A continuación se exponen las diferentes consideraciones arquitectonicas que se tuvieron en cuenta al momento de diseñar la aplicación

### Clean Architecture Scaffolding

A nivel de estructuración, el proyecto se compone principalmente de dos capas, **domain** y **infraestructure**, domain compone la logica de negocio de nuestra aplicación, mientras que infraestructure contiene las tecnologías necesarias e implementaciones de nuestra capa domain.

En base al mantenimiento futuro de una aplicación, flexibilidad y testeabilidad se propone una arquitectura limpia basada en el desacoplamiento de la capa lógica y la infraestructura de la aplicación, esto permite que, a nivel de código, tengamos mejor organizada nuestra implementación sin necesidad de acoplar fuertemente dependencias tecnologicas en nuestro sistema, aumentando la modularización de componentes y facilidad de implementacíon de nuevas features sin necesidad de malograr lo ya desarrollado.

### Springboot Webflux

Actualmente, la gran mayoría de aplicaciones empresariales asumen el desafío constante de lidiar con un alto tráfico de usuarios recurrentes dentro del sistema. Dentro de un entorno bloqueante, se vuelve una gran preocupación cuando nuestra aplicación recibe miles de solicitudes en cortos periodos de tiempo, aumentando tiempos de respuesta lo que incurre en una mala experiencia para el usuario.

Con la libreria reactor, se implementa el termino de componentes reactivos / no bloqueantes que, a partir de la distribucion de diferentes hilos para el llamado de peticiones asincronas, se disminuye notablemente los tiempos de respuesta cuando la aplicación cuenta con un alto tráfico de usuarios en un corto periodo de tiempo.

### R2DBC

### Docker


### AWS ECR, ECS Fargate, RDS
