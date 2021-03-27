package com.walid.routes;

import java.io.File;
import java.math.BigDecimal;

import com.walid.model.CreditTransaction;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregateRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AggregateRouteBuilder.class);

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
            .split(body().tokenize("\n"))
            .streaming()
            .process(msg -> {
                String line = msg.getIn().getBody(String.class);
                logger.debug("line: {}", line);
                });
        // @formatter:on
    }
}
