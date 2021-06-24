package me.gaegul.ch09.observer;

public class Main {
    public static void main(String[] args) {
        getObserverFeed();
        getObserverFeedWithLambda();
    }

    private static void getObserverFeed() {
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObserver("The queen said get favourite book Modern Java In Action!");
    }

    private static void getObserverFeedWithLambda() {
        Feed f = new Feed();
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news In NY! " + tweet);
            }
        });
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more new from Londun... " + tweet);
            }
        });
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        });
        f.notifyObserver("The queen said get favourite book Modern Java In Action!");
    }
}
