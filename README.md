Schedule Payment Service

Schedule Payment Service is responsible for scheduling future and recurring payment and process the scheduled payments in the background.


## Table of Contents

- [Prerequisites](#prerequisites)
- [Dependencies Services](#dependencies-services)
- [Build](#build)
- [Configuration](#configuration)
- [Local Setup](#local-setup)
- [Unit Testing](#unit-testing)
- [Deployment](#deployment)
- [Project Details](#project-details)
- [Contributions](#contributions)
- [Metrics](#metrics)

## Prerequisites

Before Building and Deploying this Project, ensure that the following prerequisites are available:

- Java Development Kit (JDK) from 8
- Apache Maven (https://maven.apache.org/download.cgi)
- Database in localhost
- Redis Cache in localhost
- Kafka in localhost
- IDE With lombok enable
- Familiar with git command
- Familiar with maven command
- Docker (for deploying the project to a container) (Optional)

For complete list of prerequisites [See Here](https://101digital.atlassian.net/wiki/spaces/1W/pages/354615297/Local+environment+for+coding)

For Best Practices [See Here](https://github.com/101digital/spring-boot-template/wiki)
## Dependencies Services
- Account Opening Service
  https://github.com/101digital/account-opening-service

- Payment Service
  https://github.com/101digital/payment-service

- Wallet Service
  https://github.com/101digital/wallet-service
  
- Membership Service
  https://github.com/101digital/membership-service

- Mfa Service
  https://github.com/101digital/mfa-service

## Build

To Build the project, follow these steps:

- Clone the repository to your local machine.
- Navigate to the root directory of the project.
- Run the command "mvn clean install" to build the project.

For more info on how to setup the local environment [See Here](https://101digital.atlassian.net/wiki/spaces/1W/pages/354615297/Local+environment+for+coding)

## Configuration

This configuration is used for the local development only. [SHOULD NOT]commit to git using [.gitignore](https://git-scm.com/docs/gitignore)

* Spring boot config ./config/application.yml
For more information on the Spring Boot Template [See Here](https://github.com/101digital/spring-boot-template/wiki)
```yaml
server.port: 8080
security.jwt.enable: false
logging.level:
  io.marketplace: TRACE
```
* Generate Internal JWT for testing
https://101digital.atlassian.net/wiki/spaces/1W/pages/385056879/Generate+JWT+locally+for+testing

* Setup local environment to use the dependencies service in the DEV environment
https://101digital.atlassian.net/wiki/spaces/1W/pages/391348674/How+to+connect+local+code+with+dependency+services+in+a+Remote+server 

## Local Setup

To set up the project locally, follow these steps:

- Clone the repository to your local machine.
- Navigate to the root directory of the project.
- Run the command "mvn spring-boot:run" to start the development server.
For more info [See Here](https://101digital.atlassian.net/wiki/spaces/1W/pages/354615297/Local+environment+for+coding)

Verify following url:
* http://localhost:8080/swagger-ui.htm
* http://localhost:8080/actuator
* http://localhost:8080/actuator/prometheus

## Unit Testing

To run Unit Tests for the project, follow these steps:

- Navigate to the root directory of the project.
- Run the command "mvn test" to run all unit tests.

For Unit Testing details [See Here](https://101digital.atlassian.net/wiki/spaces/KB/pages/493289494/Unit+Test+Case+Guideline+Draft)


## Deployment

Notes: In 101digital sandbox. service port is 8080 and management port is 8081
```bash
git checkout -b develop
git push origin develop
```
* Verify using 101digital Jenkins https://jenkins.101digital.io/

## Project Details

For details about this Project Technical and Non Technical details [See Here](https://101digital.atlassian.net/wiki/spaces/1W/pages/406159748/Account+Statement+Domain+Service)


## Contributions

Contributions to this Project are welcome. To contribute, follow these steps:

1. Fork the repository.
2. Create a new branch for your changes:
    git checkout -b adb/developer
3. Make your changes and commit them:
    git commit -m "<commit-message>"
4. Push your changes to your forked repository:
    git push origin adb/developer
5. Open a pull request to merge your changes into the original repository following PR process.

#### Mandatory for 101 developer
##### Using 101 logging framework
** Important**:
Using
```java
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
```
instead of other logging framework.
```java
//-- SLf4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//---Log4J ---------
import org.apache.log4j.Logger;
//--------------

//-- Apache commong loging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//-- JAVA common logging
import java.util.logging.*;
```
*[marketplace-logging-common](https://github.com/101digital/marketplace-commons/tree/master/marketplace-logging-common)

#### Error handling

* All error code in application should [list here](https://docs.google.com/spreadsheets/d/162QSo5D8EVtvUk4U61-egRsURTZKkAQh0IVKgWkBb70/edit#gid=0) 
* https://github.com/101digital/spring-boot-template/wiki#error-handling
* Use exception pattern to handle fail response [marketplace-exception-common](https://github.com/101digital/marketplace-commons/tree/master/marketplace-exception-common)

#### Changing the service name

* once the spring boot application is clone into a new service, it contains the default service name which is service_name. shell script has been created to convert the default name to the actual service name. Please run the https://github.com/101digital/spring-boot-template/blob/master/change-service-name.sh in git bash terminal.
* steps to run the shell script. Assume the new service name is example-demo-service
* run : sh change-service-name.sh example-demo (excluding the -service)
* this will replace the service_name (file name, folder name, file contents) with actual service name
* resolve any compilation errors (if your new service has multiple worlds with - , this will give compilation errors as - not allowed in the package name)
* run rm -f .git/index

Follow the best practices [See Here](https://github.com/101digital/spring-boot-template/wiki)


## Metrics

| Metric | Value |
| --- | --- |
|  Security Vulnerabilities(count of vulnerabilities) |  |
| Code Coverage (%value) | |
| Lines Of Code|  |
