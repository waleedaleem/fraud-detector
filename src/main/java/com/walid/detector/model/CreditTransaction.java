package com.walid.detector.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class CreditTransaction {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @DataField(pos = 1)
    private String cardHash;

    @DataField(pos = 2, pattern = TIMESTAMP_PATTERN)
    private Date timestamp;

    @DataField(pos = 3, precision = 2)
    private BigDecimal amount;

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CreditTransaction{" + "cardHash='" + cardHash + '\'' + ", timestamp=" + timestamp
                + ", amount=" + amount + '}';
    }
}
