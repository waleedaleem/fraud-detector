package com.walid.detector.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.walid.detector.model.CreditTransaction;

/**
 * @author wmoustaf
 */
public class TransactionAggregatorTest {

    private static final String CARD_HASH = "abcd";

    private TransactionAggregator aggregator = new TransactionAggregator(new BigDecimal("150.00"));

    @Test
    public void testAggregateNonExpiredOlderToNewer() {
        Date now = new Date();
        CreditTransaction current = getTransactionInstance(
                CARD_HASH, BigDecimal.TEN, new Date(now.getTime() - 10_000));

        CreditTransaction next = getTransactionInstance(CARD_HASH, BigDecimal.ONE, now);

        CreditTransaction aggregate = aggregator.aggregate(current, next);

        assertNotNull(aggregate);
        assertEquals(CARD_HASH, aggregate.getCardHash());
        assertEquals(next.getAmount(), aggregate.getAmount());
        assertEquals(current.getAmount().add(next.getAmount()), aggregate.getCreditTotal());
        assertEquals(1, aggregate.getForerunners().size());
    }

    @Test
    public void testAggregateExpiredOlderToNewer() {
        Date now = new Date();
        CreditTransaction current = getTransactionInstance(
                CARD_HASH, BigDecimal.TEN,
                new Date(now.getTime() - CreditTransaction.TWENTY_FOUR_HOURS));

        CreditTransaction next = getTransactionInstance(CARD_HASH, BigDecimal.ONE, now);

        CreditTransaction aggregate = aggregator.aggregate(current, next);

        assertNotNull(aggregate);
        assertEquals(CARD_HASH, aggregate.getCardHash());
        assertEquals(next.getAmount(), aggregate.getAmount());
        assertEquals(next.getAmount(), aggregate.getCreditTotal());
        assertEquals(0, aggregate.getForerunners().size());
    }

    private CreditTransaction getTransactionInstance(String cardHash,
                                                     BigDecimal amount,
                                                     Date timeStamp) {
        CreditTransaction current = new CreditTransaction();
        current.setCardHash(cardHash);
        current.setAmount(amount);
        current.setTimestamp(timeStamp);
        return current;
    }

    @Test
    public void testAggregateOlderToNull() {
        CreditTransaction current = new CreditTransaction();
        CreditTransaction next = null;

        final CreditTransaction aggregate = aggregator.aggregate(current, next);

        assertEquals(current, aggregate);
    }

    @Test
    public void testAggregateNullToNewer() {
        CreditTransaction current = null;
        CreditTransaction next = new CreditTransaction();

        CreditTransaction aggregate = aggregator.aggregate(current, next);

        assertEquals(next, aggregate);
    }
}
