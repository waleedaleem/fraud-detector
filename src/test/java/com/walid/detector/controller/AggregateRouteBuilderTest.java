package com.walid.detector.controller;

import static com.walid.detector.controller.AggregateRouteBuilder.FRAUD_REPORTER_ID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.walid.detector.model.CreditTransaction;
import com.walid.detector.view.FraudReporter;

public class AggregateRouteBuilderTest extends CamelTestSupport {

    private static final String TEST_FILE = "src/test/resources/testTransactions.csv";
    private AggregateRouteBuilder aggregateRoute;
    private FraudReporter fraudReporter;
    private BigDecimal priceThreshold = new BigDecimal("150.00");

    @Override
    protected RoutesBuilder createRouteBuilder() {
        aggregateRoute = new AggregateRouteBuilder(priceThreshold, TEST_FILE, false);
        return aggregateRoute;
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        fraudReporter = new FraudReporter(priceThreshold);
        JndiRegistry jndi = super.createRegistry();
        jndi.bind(FRAUD_REPORTER_ID, fraudReporter);
        return jndi;
    }

    @Test
    public void testAggregateRoute_$150_Threshold() throws Exception {
        List<String> expectedSuspects = Arrays.asList(
                "86e7b63091a64b24b4fe8ecd3f77e43b", "d5914a725e3548618634495eff9064f2",
                "d7ec1f1b48794338bc08ed4fc4e0472d");

        List<String> suspectCards = findSuspectCards();

        assertEquals(expectedSuspects, suspectCards);
    }

    @Test
    public void testAggregateRoute_$1500_Threshold() throws Exception {
        List<String> expectedSuspects = Collections.singletonList(
                "d7ec1f1b48794338bc08ed4fc4e0472d");

        BigDecimal priceThreshold = new BigDecimal("1500.00");
        fraudReporter.setPriceThreshold(priceThreshold);
        aggregateRoute.setPriceThreshold(priceThreshold);

        List<String> suspectCards = findSuspectCards();

        assertEquals(expectedSuspects, suspectCards);
    }

    public List<String> findSuspectCards() throws Exception {
        List<CreditTransaction> suspectTransactions = new ArrayList<>();
        RouteDefinition route = context.getRouteDefinitions().get(0);
        route.adviceWith(context, new RouteBuilder() {

            @Override
            public void configure() {
                interceptSendToEndpoint("bean:" + FRAUD_REPORTER_ID).process(
                        msg -> suspectTransactions.add(
                                msg.getIn().getBody(CreditTransaction.class)));
            }
        });

        Thread.sleep(2000);
        return suspectTransactions.stream().map(CreditTransaction::getCardHash).collect(
                Collectors.toList());
    }
}
