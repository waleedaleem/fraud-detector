package com.walid.detector.route;

import java.io.File;
import java.math.BigDecimal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.detector.model.CreditTransaction;

public class AggregateRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AggregateRouteBuilder.class);
    private static final String ROUTE_ID = "fraud-detector";

    private File transactionFile;
    private BigDecimal priceThreshold;

    public AggregateRouteBuilder(BigDecimal priceThreshold, String transactionFile) {
        super();
        this.priceThreshold = priceThreshold;
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
            .process(msg -> {
                CreditTransaction transaction = msg.getIn().getBody(CreditTransaction.class);
                logger.debug("transaction: {}", transaction);
                });
        // @formatter:on
    }
}
