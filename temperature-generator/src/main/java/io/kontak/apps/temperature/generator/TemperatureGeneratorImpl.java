/*
 * Copyright (C) Motorola Solutions, INC.
 * All Rights Reserved.
 */

package io.kontak.apps.temperature.generator;

import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Component
public class TemperatureGeneratorImpl implements TemperatureGenerator {
    private static final List<String> ROOMS = List.of(
            "room-1",
            "room-2",
            "room-3",
            "room-4"
    );
    private static final List<String> THERMOMETERS = List.of(
            "-thermometer-1",
            "-thermometer-2",
            "-thermometer-3",
            "-thermometer-4",
            "-thermometer-5"
    );
    private final Random random = new Random();

    @Override
    public List<TemperatureReading> generate() {
        return List.of(generateSingleReading());
    }

    private TemperatureReading generateSingleReading() {
        final String room = getRandomRoom();
        return new TemperatureReading(
                random.nextDouble(10d, 30d),
                room,
                room + getRandomThermometer(),
                Instant.now()
        );
    }

    private String getRandomRoom() {
        return ROOMS.get(random.nextInt(ROOMS.size()));
    }

    private String getRandomThermometer() {
        return THERMOMETERS.get(random.nextInt(THERMOMETERS.size()));
    }
}
