# Prueba Seti / Manuel Santiago Chaparro Rojas

Este proyecto se realizo con el objetivo de completar el reto llamado "PruebaNequi" donde se exponen las siguientes habilidades:

## Arquitectura

- Manejo de librería reactor Webflux

```java
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
```
  
- Integración de Scaffolding Clean Architecture

<img width="343" alt="imagen" src="https://github.com/user-attachments/assets/46f9eac8-c633-486b-b6da-b233c23f9547" />

- Uso de operadores map, flatMap, zip, switchIfEmpty
- Uso de señales onNext, onComplete, onError

```java
//Ejemplo de metodo que intengra operadores y señales reactivos para actualizar una franquicia
    public Mono<Franchise> updateFranchise(Franchise franchise) {

        Mono<Boolean> isExistingByNameMono = franchiseRepository.existsByName(franchise.getName());
        Mono<Franchise> findByIdMono = franchiseRepository.findById(franchise.getId())
                .switchIfEmpty(Mono.error(new CustomException("La franquicia que desea actualizar no existe.")));

        // Usamos .zip para concatenar dos Mono y continuar con el flujo cuando ambos terminan
        return Mono.zip(
                findByIdMono,
                isExistingByNameMono)
                .flatMap(results -> {
                    Franchise existingFranchise = results.getT1();
                    Boolean existByName = results.getT2();

                    if (Boolean.TRUE.equals(existByName) && !existingFranchise.getName().equals(franchise.getName()))
                        return Mono
                                .error(new CustomException("Ya existe una franquicia diferente con ese mismo nombre"));

                    Franchise updatedFranchise = existingFranchise.applyUpdates(franchise);
                    return franchiseRepository.save(updatedFranchise);
                })
                .doOnNext(updatedItem ->
                // Cumplimiento punto 4. OnNext / DoOnNext
                // Simulamos el envío de un correo al usuario notificando que
                // la información del producto fue actualizada exitosamente
                logger.info("Franquicia actualizada: {}", updatedItem));
    }
```

- Uso de librería slf4j para manejo de logs (Implementado para errores y simulación de correos)
  
```java
//Ejemplo de logger para errores controlados
logger.error("SERVER-ERROR: Error de tipo DataIntegrityViolationException: {}", ex.getMessage());
//Ejemplo de logger para correos
logger.info("SIMULACIÓN DE CORREO: Se actualizo el stock del producto {} a {} unidades.", updatedProduct.getName(), updatedProduct.getStock());
```

- Pruebas unitarias con mas de un 80% de coverage total (Integración con Jacoco)
<img width="1220" alt="imagen" src="https://github.com/user-attachments/assets/e8a3cb2f-491c-42c6-943d-9f6aafedd3a0" />

## Puntos adicionales completados

| Actividad                                |  Terminado  |
|------------------------------------------|-------------|
| Dockerizacion                            |    Si    |
| Actualización de nombre para Franquicia  |    Si    |
| Actualización de nombre para Sucursal    |    Si    |
| Actualización de nombre para Producto    |    Si    |
| Aprovisionamiento BD con Cloudformation  |    Si    |
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
| AWS                 | http://3.15.155.166:3000/api |

Para efectos prácticos, la implementación de las API por defecto se dejan documentadas apuntando a los servicios de AWS

# Franquicia

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Franquicia | POST             | http://3.15.155.166:3000/api/franchise |

```json
{
    "name": "Tienda Electrónica",
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Franquicia | PUT              | http://3.15.155.166:3000/api/franchise |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Electronics.co",
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Lista de Franquicias  | GET              | http://3.15.155.166:3000/api/franchise|

| Funcionalidad                           | Tipo de Petición |  API                                                                        |
|-----------------------------------------|------------------|-----------------------------------------------------------------------------|
| Productos con mayor stock por sucursal  | GET              | http://3.15.155.166:3000/api/franchise/{id_franquicia}/productsWithMoreStock |

# Sucursal

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Sucursal   | POST             | http://3.15.155.166:3000/api/branch    |

```json
{
    "name": "Electronics - Sede Medellín",
    "franchiseId": 1
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Sucursal   | PUT              | http://3.15.155.166:3000/api/branch    |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Rebranding / ElectronInc - Sede Tunja",
    "franchiseId": 2
}
```

| Funcionalidad         | Tipo de Petición |  API                               |
|-----------------------|------------------|------------------------------------|
| Lista de Sucursales   | GET              | http://3.15.155.166:3000/api/branch |

# Producto

| Funcionalidad    | Tipo de Petición |  API                                  |
|------------------|------------------|---------------------------------------|
| Crear Producto   | POST             | http://3.15.155.166:3000/api/product   |

```json
{
    "name": "Laptop",
    "stock": 100,
    "branch": 1
}
```

| Funcionalidad         | Tipo de Petición |  API                                  |
|-----------------------|------------------|---------------------------------------|
| Actualizar Producto   | PUT              | http://3.15.155.166:3000/api/product   |

```json
{
    "id": 1, //(Simulando que el ID 1 Existe en la DB)
    "name": "Asus Vivobook 2025 32GB RAM 1TB SSD",
    "branch": null //(No es necesario actualizar el objeto del que depende)
}
```

| Funcionalidad         | Tipo de Petición |  API                                |
|-----------------------|------------------|-------------------------------------|
| Lista de Productos    | GET              | http://3.15.155.166:3000/api/product |

| Funcionalidad                    | Tipo de Petición   |  API                                                                                    |
|----------------------------------|--------------------|-----------------------------------------------------------------------------------------|
| Actualizar Stock de Productos    | PATCH              | http://3.15.155.166:3000/api/product/{id_del_producto}/stock/{cantidad_a_sumar_o_restar} |

Para sumar 20 productos de stock al producto

```shell
    http://3.15.155.166:3000/api/product/1/stock/20
```

Para restar 10 productos de stock al producto

```shell
    http://3.15.155.166:3000/api/product/1/stock/-10
```

# Despliegue de toda la solución en la nube

A continuación se presenta la evidencia del despliegue de la base de datos Postgresql con Cloudformation y la imagen de docker ejecutandose en ECS Fargate

## Archivo para generar BD Postgresql con Cloudformation

```yml
AWSTemplateFormatVersion: '2010-09-09'
Description: Despliegue de BD Postgresql con Cloudformation para prueba de SETI

Parameters:
  DBInstanceIdentifier:
    Type: String
    Default: seti-postgresql-cloudformation
  DBName:
    Type: String
    Default: setidb //Cambié el nombre para no usar la BD por defecto, la implementacion actualmente se conecta a la BD por defecto 'public'
  DBUsername:
    Type: String
    Default: setitest
    NoEcho: true
  DBPassword:
    Type: String
    Default: setitest2025*
    NoEcho: true
  DBInstanceClass:
    Type: String
    Default: db.t4g.micro
  DBAllocatedStorage:
    Type: Number
    Default: 20

Resources:
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsSupport: true
      EnableDnsHostnames: true

  InternetGateway:
    Type: AWS::EC2::InternetGateway
  VPCGatewayAttachment:
    Type: AWS::EC2::VPCGatewayAttachment
    Properties:
      VpcId: !Ref VPC
      InternetGatewayId: !Ref InternetGateway

  PublicSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.1.0/24
      AvailabilityZone: !Select [0, !GetAZs '']
      MapPublicIpOnLaunch: true
  PublicSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.2.0/24
      AvailabilityZone: !Select [1, !GetAZs '']
      MapPublicIpOnLaunch: true

  PublicRouteTable:
    Type: AWS::EC2::RouteTable
    Properties:
      VpcId: !Ref VPC
  InternetRoute:
    Type: AWS::EC2::Route
    DependsOn: InternetGateway
    Properties:
      RouteTableId: !Ref PublicRouteTable
      DestinationCidrBlock: 0.0.0.0/0
      GatewayId: !Ref InternetGateway
  Subnet1RouteTableAssociation:
    Type: AWS::EC2::SubnetRouteTableAssociation
    Properties:
      SubnetId: !Ref PublicSubnet1
      RouteTableId: !Ref PublicRouteTable
  Subnet2RouteTableAssociation:
    Type: AWS::EC2::SubnetRouteTableAssociation
    Properties:
      SubnetId: !Ref PublicSubnet2
      RouteTableId: !Ref PublicRouteTable

  RDSPublicSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      VpcId: !Ref VPC
      GroupDescription: Permisos de acceso publicos por IP 0.0.0.0 a RDS Postgresql
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 5432
          ToPort: 5432
          CidrIp: 0.0.0.0/0

  DBSubnetGroup:
    Type: AWS::RDS::DBSubnetGroup
    Properties:
      DBSubnetGroupDescription: Subredes estandar para servicio rds
      SubnetIds:
        - !Ref PublicSubnet1
        - !Ref PublicSubnet2

  RDSInstance:
    Type: AWS::RDS::DBInstance
    Properties:
      DBInstanceIdentifier: !Ref DBInstanceIdentifier
      DBName: !Ref DBName
      AllocatedStorage: !Ref DBAllocatedStorage
      DBInstanceClass: !Ref DBInstanceClass
      Engine: postgres
      EngineVersion: 17.4
      MasterUsername: !Ref DBUsername
      MasterUserPassword: !Ref DBPassword
      DBSubnetGroupName: !Ref DBSubnetGroup
      VPCSecurityGroups:
        - !GetAtt RDSPublicSecurityGroup.GroupId
      PubliclyAccessible: true
      DeletionProtection: false

Outputs:
  DBEndpoint:
    Description: Cadena de conexion para la base de datos generada con CLoudformation
    Value: !GetAtt RDSInstance.Endpoint.Address
```

## RDS Postgresql 
<img width="1508" alt="imagen" src="https://github.com/user-attachments/assets/94f0feb6-9c5a-44cc-8100-12931c9814c8" />
<img width="1511" alt="imagen" src="https://github.com/user-attachments/assets/5cd4c8c4-46c4-4e8b-84de-e531e95cf9dd" />
**(Las credenciales de conexión se pueden encontrar en el archivo .env)**

## ECR / ECS Fargate
**ECR con imagen del proyecto en Docker**
<img width="1511" alt="imagen" src="https://github.com/user-attachments/assets/c038821b-8297-4620-a28e-f890c1883222" />
**Cluster ECS con servicio Fargate**
<img width="1510" alt="imagen" src="https://github.com/user-attachments/assets/85017dcd-c6e5-47e0-896f-6bdb06a05e00" />
<img width="1511" alt="imagen" src="https://github.com/user-attachments/assets/44bcd320-466b-4fad-b66f-fa2321269859" />

## Consideraciones de diseño

A continuación se exponen las diferentes consideraciones arquitectonicas que se tuvieron en cuenta al momento de diseñar la aplicación

### Clean Architecture Scaffolding

A nivel de estructuración, el proyecto se compone principalmente de dos capas, **domain** y **infraestructure**, domain compone la logica de negocio de nuestra aplicación, mientras que infraestructure contiene las tecnologías necesarias e implementaciones de nuestra capa domain.

En base al mantenimiento futuro de una aplicación, flexibilidad y testeabilidad se propone una arquitectura limpia basada en el desacoplamiento de la capa lógica y la infraestructura de la aplicación, esto permite que, a nivel de código, tengamos mejor organizada nuestra implementación sin necesidad de acoplar fuertemente dependencias tecnologicas en nuestro sistema, aumentando la modularización de componentes y facilidad de implementacíon de nuevas features sin necesidad de malograr lo ya desarrollado.

### Springboot Webflux

Actualmente, la gran mayoría de aplicaciones empresariales asumen el desafío constante de lidiar con un alto tráfico de usuarios recurrentes dentro del sistema. Dentro de un entorno bloqueante, se vuelve una gran preocupación cuando nuestra aplicación recibe miles de solicitudes en cortos periodos de tiempo, aumentando tiempos de respuesta lo que incurre en una mala experiencia para el usuario.

Con la libreria reactor, se implementa el termino de componentes reactivos / no bloqueantes que, a partir de la distribucion de diferentes hilos para el llamado de peticiones asincronas, se disminuye notablemente los tiempos de respuesta cuando la aplicación cuenta con un alto tráfico de usuarios en un corto periodo de tiempo.

### R2DBC

Generalmente, el uso de bases de datos relacionales ha sido el estándar para la implementación de aplicaciones empresariales de alto nivel, debido a la consistencia, fiabilidad y fuerte relación de almacenamiento de registros. **Postresql** es un motor de bases de datos relacional que actualmente está fuertemente optimizado para el uso de implementaciones reactivas con el driver R2DBC, el cual es un ORM que nos permite simplificar el uso de querys sql y proporcionando directamente el manejo de eventos reactivos con Mono<T> y Flux<T>.

Inicialmente se pensó implementar una solución con MongoDB, debido a la rapidez que ofrecen las bases de datos no relacionales en cuanto a lectura de datos, sin embargo, comúnmente en entornos empresariales o bancarios, la consistencia de datos y fiabilidad en los registros y transacciones es lo más importante a la hora de implementar soluciones tecnológicas, por lo cual, se tomó la decisión de trabajar con una DB relacional capaz de soportar transacciones reactivas no bloqueantes, Postgresql y el ORM R2DBC son el candidato perfecto.

### Docker

Un desafío común a la hora de trabajar en equipos de desarrollo e incluso a la hora de llevar despliegues a producción o pruebas, es la dificultad de implementar un ambiente estable replicando el funcionamiento del entorno local en el cual se desarrollo la aplicación. Para ello, Docker soluciona muchos de estos problemas.

Docker es una maquina virtual que, en escencia, corre por debajo el SO Linux y que, estableciendo una serie de comandos los cuales se podrian asimilar a los mismo que se corren en una terminal, se puede realizar el despliegue de una aplicación. Docker, al ejecutar Linux independientemente del entorno en el cual se despliegue, no tiene problema en ejecutar los mismos comandos de despliegue independientemente de si estamos en un entorno local, pruebas o productivo.

Se decidio usarlo debido a que, como ya se ha mencionado, facilita el despliegue de la aplicación independientemente del entorno en el que estemos o quien lo esté ejecutando, (Ideal para este caso en el cual se desea realizar el análisis del API REST realizado).

### AWS ECR, ECS Fargate, RDS

Al momento de realizar cualquier despliegue de aplicación en un entorno real, es necesario evaluar si es rentable realizar la compra de nuestro propio servidor, realizar mantenimientos y establecer las conexiones necesarias a la red para que nuestra aplicación funcione correctamente vía web, sin contar gastos constantes como electricidad, internet y diferentes herramientas de mantenimiento.

Debido a que se trabajó con Docker, AWS ofrece una serie de servicios que permiten el despliegue rápido de una aplicación sin necesidad de preocuparnos de los puntos anteriormente mencionados, además, por facilidad de calificación de la prueba, se exponen los endpoints a partir de estos servicios con el objetivo de aumentar la velocidad de revisión de la implementación.

Se uso ECR y ECS debido a que son dos servicios gestores de imágenes que, nos permiten registrar un historial con las imagenes desarrolladas para nuestra aplicación (ECR) y, desplegar estas imagenes usando Fargate, un concepto de despliegue de imagenes serverless que nos permite exponer nuestra API unicamente configurando parametros como la CPU, SO y tiempos maximo de uso (ECS), finalmente, el almacenamiento de nuestra infomación con el motor Postgresql se llevo a cabo en la nube con el servicio RDS que nos permite desplegar bases de datos relacionales, configurando unicamente IPs de entrada, CPU, RAM y Almacenamiento.

