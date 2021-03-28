# Fraud Detector

## Overview
This is a java 8 standalone application accepting a stream of credit card transactions in a CSV file. The solution correlates and aggregates transaction from the credit card number and reports any card spending more than a specific threshold during a 24 hour sliding window as candidate fraud. The application is modularly designed as Mode-View-Controller. The processed file is moved to a `.camel` subdirectory as a sign of completion.

## Component View
- Multitiered design (Apache Camel route object as the controller, backed by an Aggregator object as a service layer, and a data tier consisting of a card transaction entity that is serialised using Bindy CSV  library. Suspect credit cards are reported through warning statements to the application log (stdout).

## Main Technology Choices
- Built as a standalone application for the sake of simplicity.
- Apache Camel was used as a suitable implementation of the Agrregator integration pattern.
- Gralde was used to manage dependencies and generate the uber JAR.
- Junit was used to create Unit/Integration tests.

## UML Class Diagram
![Class Diagram](classDiagram.png)

## Synopsis
To build (package) the uber JAR
```bash
$ ./gradlew build
```
To run the application
```bash
$ java -jar build/libs/fraud-detector-1.0.0.jar 150.00 transactions.csv

$
```

## Test Coverage Report
![JaCoCo Test Coverage Report](jaCoCoTestCoverageReport.png)