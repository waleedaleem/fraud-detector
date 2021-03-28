package com.walid.detector.controller;

import java.io.File;
import java.math.BigDecimal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import com.walid.detector.model.CreditTransaction;
import com.walid.detector.service.TransactionAggregator;
import com.walid.detector.view.FraudReporter;

public class AggregateRouteBuilder extends RouteBuilder {

    private static final String ROUTE_ID = "fraud-detector";

    private final File transactionFile;
    private final TransactionAggregator aggregator;
    private final FraudReporter fraudReporter;

    public AggregateRouteBuilder(BigDecimal priceThreshold, String transactionFile) {
        super();
        this.aggregator = new TransactionAggregator(priceThreshold);
        this.fraudReporter = new FraudReporter(priceThreshold);
        this.transactionFile = new File(transactionFile);
    }

    @Override
    public void configure() {
        String directoryName = transactionFile.getAbsoluteFile().getParent();
        String fileName = transactionFile.getName();

        // @formatter:off
        fromF("file://%s?fileName=%s", directoryName, fileName)
            .routeId(ROUTE_ID)
            .split(body().tokenize("\n"))
            .streaming()
            .unmarshal()
            .bindy(BindyType.Csv, CreditTransaction.class)
            .aggregate(simple("${body?.cardHash}"), aggregator)
            .bean(fraudReporter);
        // @formatter:on
    }
}
