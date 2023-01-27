package org.example;

import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Quotes {

    private final Random random = new Random();

    private final List<String> symbols = Arrays.asList("TSLA", " IBM", "NOW", " SHOP", "LRCX", " CVX", "LEVI", " RIVN", "", "ULN", " WBD", "LCID", " NEE");

    private final AtomicInteger counter = new AtomicInteger();

    public Quote getQuote(){

        var quote = new Quote();

        quote.setTimestamp(counter.getAndIncrement());
        quote.setPrice(random.nextDouble(100));

        // every other should fail
        //
        if(quote.getPrice() % 2 == 0) {
            quote.setSymbol(symbols.get(random.nextInt(symbols.size() - 1)));
        }

        return quote;

    }

}
