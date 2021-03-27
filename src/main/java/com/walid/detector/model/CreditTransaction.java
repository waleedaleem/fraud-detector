package com.walid.detector.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class CreditTransaction {

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final int TWENTY_FOUR_HOURS = 24 * 3600_000;

    private final List<CreditTransaction> forerunners = new ArrayList<>();

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

    public List<CreditTransaction> getForerunners() {
        return forerunners;
    }

    public BigDecimal getCreditTotal() {
        return forerunners.stream().map(CreditTransaction::getAmount).reduce(
                getAmount(), BigDecimal::add);
    }

    public void aggregate(CreditTransaction forerunner) {
        List<CreditTransaction> candidates = new ArrayList<>(forerunner.getForerunners());
        candidates.add(forerunner);

        // Filters out expired transactions
        Predicate<CreditTransaction> stillFresh = fr -> timestamp.getTime()
                - fr.getTimestamp().getTime() < TWENTY_FOUR_HOURS;

        candidates.stream().filter(stillFresh).forEach(forerunners::add);
    }

    @Override
    public String toString() {
        return "CreditTransaction{cardHash='" + cardHash + '\'' + ", timestamp=" + timestamp
                + ", amount=" + amount + listForerunners() + "}";
    }

    private String listForerunners() {
        StringBuilder sb = new StringBuilder();
        // @formatter:off
        sb.append(", creditTotal=").append(getCreditTotal())
            .append(", forerunners = [");
        getForerunners().forEach(fr -> sb.append("(")
            .append(fr.getTimestamp())
            .append(", ").append(fr.getAmount())
            .append("), ")
        );
        sb.append("]");
        // @formatter:on
        return sb.toString();
    }
}
