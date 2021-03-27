package com.walid.detector.route;

import java.io.File;
import java.math.BigDecimal;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import com.walid.detector.model.CreditTransaction;
import com.walid.detector.service.TransactionAggregator;

public class AggregateRouteBuilder extends RouteBuilder {

    private static final String ROUTE_ID = "fraud-detector";

    private final BigDecimal priceThreshold;
    private final File transactionFile;
    private final TransactionAggregator aggregator;

    public AggregateRouteBuilder(BigDecimal priceThreshold, String transactionFile) {
        super();
        this.priceThreshold = priceThreshold;
        this.aggregator = new TransactionAggregator(priceThreshold);
        this.transactionFile = new File(transactionFile);
    }

    @Override
    public void configure() {
        String directoryName = transactionFile.getAbsoluteFile().getParent();
        String fileName = transactionFile.getName();

        // @formatter:off
        fromF("file://%s?fileName=%s&noop=true", directoryName, fileName)
            .routeId(ROUTE_ID)
            .split(body().tokenize("\n"))
            .streaming()
            .unmarshal()
            .bindy(BindyType.Csv, CreditTransaction.class)
            .aggregate(simple("${body?.cardHash}"), aggregator)
            .log(LoggingLevel.WARN, "C.C ${body?.cardHash} exceeded the $" + priceThreshold +" limit.");
    }
}
