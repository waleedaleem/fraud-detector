package com.walid.detector.view;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.detector.model.CreditTransaction;

public class FraudReporter {

    private static final Logger logger = LoggerFactory.getLogger(FraudReporter.class);
    private final BigDecimal priceThreshold;

    public FraudReporter(BigDecimal priceThreshold) {
        this.priceThreshold = priceThreshold;
    }

    public void reportFraud(CreditTransaction transaction) {
        int transactionCount = transaction.getForerunners().size() + 1;
        BigDecimal overLimit = transaction.getCreditTotal().subtract(priceThreshold);
        logger.warn(
                "Card {} exceeded the ${} threshold by ${} across {} transaction(s) between {} and {}",
                transaction.getCardHash(), priceThreshold, overLimit, transactionCount,
                transaction.getEarliestPurchaseTime(), transaction.getTimestamp());
    }
}
