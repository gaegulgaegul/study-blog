package me.gaegul.ch17.obverser;

import io.reactivex.Observable;
import me.gaegul.ch17.TempInfo;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class TempObservable {

    public static Observable<TempInfo> getTemperature(String town) {
        return Observable.create(emitter ->
                Observable.interval(1, TimeUnit.SECONDS)
                    .subscribe(i -> {
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        } else {
                            try {
                                emitter.onNext(TempInfo.fetch(town));
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                    }));
    }

    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town).map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }

    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getTemperature(town).filter(temp -> temp.getTemp() < 0);
    }

    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                                    .map(TempObservable::getCelsiusTemperature)
                                    .collect(toList()));
    }
}
