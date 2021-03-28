package com.walid.detector.controller;

import java.io.File;
import java.math.BigDecimal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import com.walid.detector.model.CreditTransaction;
import com.walid.detector.service.TransactionAggregator;

/**
 * A Camel route to stream credit card transactions from file and hand over to a
 * {@link TransactionAggregator} and subsequently sending outcome to a
 * {@link com.walid.detector.view.FraudReporter}
 *
 * @author wmoustaf
 */
public class AggregateRouteBuilder extends RouteBuilder {

    public static final String FRAUD_REPORTER_ID = "fraudReporter";
    private static final String ROUTE_ID = "fraud-detector";

    private final File transactionFile;
    private final boolean moveProcessedFile;
    private final TransactionAggregator aggregator;

    public AggregateRouteBuilder(BigDecimal priceThreshold,
                                 String transactionFile,
                                 boolean moveProcessedFile) {
        super();
        this.aggregator = new TransactionAggregator(priceThreshold);
        this.transactionFile = new File(transactionFile);
        this.moveProcessedFile = moveProcessedFile;
    }

    public void setPriceThreshold(BigDecimal priceThreshold) {
        aggregator.setPriceThreshold(priceThreshold);
    }

    @Override
    public void configure() {
        String directoryName = transactionFile.getAbsoluteFile().getParent();
        String fileName = transactionFile.getName();

        // @formatter:off
        fromF("file://%s?fileName=%s&noop=%b", directoryName, fileName, !moveProcessedFile)
            .routeId(ROUTE_ID)
            .split(body().tokenize("\n"))
            .streaming()
            .unmarshal()
            .bindy(BindyType.Csv, CreditTransaction.class)
            .aggregate(simple("${body?.cardHash}"), aggregator)
            .to("bean:" + FRAUD_REPORTER_ID);
        // @formatter:on
    }
}
