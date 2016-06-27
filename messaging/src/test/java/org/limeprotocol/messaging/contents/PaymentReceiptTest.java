package org.limeprotocol.messaging.contents;

import org.junit.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PaymentReceiptTest {
    final String currency = "BRL";
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Test
    public void toString_WithTotal_ReturnsCorrectValue() throws URISyntaxException {
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setCurrency(currency);
        receipt.setTotal(BigDecimal.valueOf(10.10));

        assertEquals("R$ 10,10", receipt.toString());
    }

    @Test
    public void toString_WithTotalAndPaidOn_ReturnsCorrectValue() throws URISyntaxException, ParseException {
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setCurrency(currency);
        receipt.setTotal(BigDecimal.valueOf(100.18));
        receipt.setPaidOn(dateFormat.parse("2016-07-20 18:10"));

        assertEquals("R$ 100,18\n20/07/2016 18:10", receipt.toString());
    }

    @Test
    public void toString_WithTotalPaidOnAndMethod_ReturnsCorrectValue() throws URISyntaxException, ParseException {
        PaymentMethod method = new PaymentMethod();
        method.setName("Pop");

        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setCurrency(currency);
        receipt.setTotal(BigDecimal.valueOf(5.91));
        receipt.setPaidOn(dateFormat.parse("2016-07-01 09:10"));
        receipt.setMethod(method);

        assertEquals("R$ 5,91\n01/07/2016 09:10 (Pop)", receipt.toString());
    }

    @Test
    public void toString_WithItemsTotalPaidOnAndMethod_ReturnsCorrectValue() throws URISyntaxException, ParseException {
        BigDecimal total = BigDecimal.valueOf(5.91);
        PaymentMethod method = new PaymentMethod();
        method.setName("Pop");

        InvoiceItem[] items = new InvoiceItem[1];
        InvoiceItem item1 = new InvoiceItem();
        item1.setDescription("Item 1");
        item1.setTotal(total);
        items[0] = item1;

        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setCurrency(currency);
        receipt.setTotal(total);
        receipt.setPaidOn(dateFormat.parse("2016-07-02 09:10"));
        receipt.setMethod(method);
        receipt.setItems(items);

        assertEquals("1. Item 1 (R$ 5,91)\nR$ 5,91\n02/07/2016 09:10 (Pop)", receipt.toString());
    }
}
