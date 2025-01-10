# Getting Started

## Requirements

Ton run this project, you need to have the following tools installed on your computer :

* Java 21
* Maven 3.8.3
* Docker : https://www.docker.com/get-started/

If you use sdkman to manage your java version, you can install java 21 and use the following command :

``` bash
sdk install 21.0.4-oracle
sdk use java 21.0.4-oracle
``` 

you can also find a file name .sdkmanrc to use :

``` bash
sdk env
```

## DB

command to init a new db in docker :

``` bash
docker run --name chatop -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypass -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_DATABASE=mydb -p 3306:3306 -d mysql
```

to start db :

``` bash
docker start chatop
```

to stop db :

``` bash
docker stop chatop
```

to remove db :

``` bash
docker rm chatop
```

### Script to init your data base :

You dont need to populate your data as the app will sync the db with the entities at the start of the app by the
synchonize option is true.
For production, think about removing synchronize and use the following script to create your db :

``` sql

CREATE TABLE `USERS` (
`id` integer PRIMARY KEY AUTO_INCREMENT,
`email` varchar(255),
`name` varchar(255),
`password` varchar(255),
`created_at` timestamp,
`updated_at` timestamp
);

CREATE TABLE `RENTALS` (
`id` integer PRIMARY KEY AUTO_INCREMENT,
`name` varchar(255),
`surface` numeric,
`price` numeric,
`picture` varchar(255),
`description` varchar(2000),
`owner_id` integer NOT NULL,
`created_at` timestamp,
`updated_at` timestamp
);

CREATE TABLE `MESSAGES` (
`id` integer PRIMARY KEY AUTO_INCREMENT,
`rental_id` integer,
`user_id` integer,
`message` varchar(2000),
`created_at` timestamp,
`updated_at` timestamp
);

CREATE UNIQUE INDEX `USERS_index` ON `USERS` (`email`);

ALTER TABLE `RENTALS`
ADD CONSTRAINT `fk_owner`
FOREIGN KEY (`owner_id`) REFERENCES `USERS`(`id`);

ALTER TABLE `MESSAGES`
ADD CONSTRAINT `fk_user`
FOREIGN KEY (`user_id`) REFERENCES `USERS`(`id`);

ALTER TABLE `MESSAGES`
ADD CONSTRAINT `fk_rental`
FOREIGN KEY (`rental_id`) REFERENCES `RENTALS`(`id`);

```

## OPEN API documentation :

You can access the open api documentation at the following url :
http://localhost:3001/swagger-ui/index.html#/

### Commit

This project is versioned on github and using the conventional commit message format.
You can find more information about it here : https://www.conventionalcommits.org/en/v1.0.0/

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#using.devtools)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.security)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.
