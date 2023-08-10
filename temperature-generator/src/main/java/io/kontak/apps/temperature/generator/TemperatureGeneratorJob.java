package io.kontak.apps.temperature.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TemperatureGeneratorJob {

    private final TemperatureGenerator generator;
    private final TemperatureStreamPublisher publisher;

    public TemperatureGeneratorJob(@Qualifier("temperatureGeneratorImpl") TemperatureGenerator generator, TemperatureStreamPublisher publisher) {
        this.generator = generator;
        this.publisher = publisher;
    }

    @Scheduled(fixedRateString = "${temperature-generator.rate.seconds}", timeUnit = TimeUnit.SECONDS)
    public void generateDataAndSend() {
        generator.generate().forEach(publisher::publish);
    }

}
