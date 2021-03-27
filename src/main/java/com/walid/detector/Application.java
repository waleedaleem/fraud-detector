package com.walid.detector;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.detector.route.AggregateRouteBuilder;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final String[] HELP_ALIASES = {
        "-h", "-help", "--help"
    };

    public static void main(String[] args) throws Exception {

        if (args.length != 2 || Arrays.asList(HELP_ALIASES).contains(args[0])) {
            logger.warn("usage: java -jar fraud-detector-x.y.z.jar <price threshold> <input file>");
            System.exit(0);
        }
        BigDecimal priceThreshold = new BigDecimal(args[0]);
        String transactionFile = args[1];
        startAggregateRoute(priceThreshold, transactionFile);
    }

    private static void startAggregateRoute(BigDecimal priceThreshold, String transactionFile)
            throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(
                new AggregateRouteBuilder(priceThreshold, transactionFile));

        // now keep the application running until the JVM is terminated
        main.run();
    }
}
