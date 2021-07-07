package me.gaegul.ch16.v1;

import me.gaegul.ch16.ExchangeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;
import static me.gaegul.ch16.ExchangeService.*;

public class BestPriceFinder {

    private final List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll")
    );

    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public List<String> findPricesSequential(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public List<String> findPricesParallel(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public List<String> findPricesFuture(String product) {
        List<CompletableFuture<String>> priceFuture = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + "price is " + shop.getPrice(product)
                ))
                .collect(toList());

        return priceFuture.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public List<String> findPricesFutureWithDaemon(String product) {
        List<CompletableFuture<String>> priceFuture = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + "price is " + shop.getPrice(product), executor))
                .collect(toList());

        return priceFuture.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public List<String> findPricesInUSD(String product) {
        List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            CompletableFuture<Double> priceFutureInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(
                            CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD)),
                            (price, rate) -> price * rate
                    );
            priceFutures.add(priceFutureInUSD);
        }

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .map(price -> " price is " + price)
                .collect(toList());
    }

    public List<String> findPricesInUSDJava7(String product) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Double>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            final Future<Double> futureRate = executor.submit(new Callable<Double>() {
                @Override
                public Double call() throws Exception {
                    return getRate(Money.EUR, Money.USD);
                }
            });

            Future<Double> futureRateInUSD = executor.submit(new Callable<Double>() {
                @Override
                public Double call() throws Exception {
                    double priceInEUR = shop.getPrice(product);
                    return priceInEUR * futureRate.get();
                }
            });
            priceFutures.add(futureRateInUSD);
        }

        List<String> prices = new ArrayList<>();
        for (Future<Double> priceFuture : priceFutures) {
            try {
                prices.add(" price is " + priceFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return prices;
    }

    public List<String> findPricesInUSDWithTimeout(String product) {
        List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            CompletableFuture<Double> priceFutureInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(
                            CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD))
                                .completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS),
                            (price, rate) -> price * rate
                    )
                    .orTimeout(3, TimeUnit.SECONDS);
            priceFutures.add(priceFutureInUSD);
        }

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .map(price -> " price is " + price)
                .collect(toList());
    }

    public List<String> findPricesInUSD2(String product) {
        List<CompletableFuture<String>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            CompletableFuture<String> priceFutureInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(
                            CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD)),
                            (price, rate) -> price * rate
                    )
                    .thenApply(price -> shop.getName() + " price is " + price);
            priceFutures.add(priceFutureInUSD);
        }

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public List<String> findPricesInUSD3(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(
                                CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD)),
                                (price, rate) -> price * rate
                        )
                        .thenApply(price -> shop.getName() + " price is " + price)
                )
                .map(CompletableFuture::join)
                .collect(toList());
    }

}
