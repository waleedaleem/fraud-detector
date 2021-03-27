package com.walid.detector.service;

import java.math.BigDecimal;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.detector.model.CreditTransaction;

public class TransactionAggregator implements AggregationStrategy, Predicate {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAggregator.class);

    private final BigDecimal priceThreshold;

    public TransactionAggregator(BigDecimal priceThreshold) {
        this.priceThreshold = priceThreshold;
    }

    public CreditTransaction aggregate(CreditTransaction current, CreditTransaction next) {
        // capture next for logging before being changed
        String strNext = null;
        CreditTransaction aggregate;
        if (current != null && next != null) {
            strNext = next.toString();
            aggregate = next;
            aggregate.aggregate(current);
        } else {
            if (next != null)
                strNext = next.toString();
            aggregate = current != null ? current : next;
        }
        logger.debug("Aggregating \n{} to \n{} returned \n{}", current, strNext, aggregate);
        return aggregate;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange != null && newExchange != null) {
            CreditTransaction aggregate = aggregate(
                    oldExchange.getIn().getBody(CreditTransaction.class),
                    newExchange.getIn().getBody(CreditTransaction.class));

            newExchange.getIn().setBody(aggregate, CreditTransaction.class);
            return newExchange;
        } else {
            return oldExchange != null ? oldExchange : newExchange;
        }
    }

    @Override
    public boolean matches(Exchange exchange) {
        // @formatter:off
        return exchange.getIn()
            .getBody(CreditTransaction.class)
            .getCreditTotal()
            .subtract(priceThreshold)
            .compareTo(BigDecimal.ZERO) > 0;
        // @formatter:on
    }
}
