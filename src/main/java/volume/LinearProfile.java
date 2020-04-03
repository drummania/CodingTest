package volume;

import java.time.Instant;

public class LinearProfile implements VolumeProfile{

    private final Instant startTime;
    private final Instant endTime;

    public LinearProfile(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public double getVolume(final Instant time) {

        if (!time.isAfter(startTime)) {
            return 0.0;
        }

        if (!time.isBefore(endTime)) {
            return 1.0;
        }
        return (double)(time.toEpochMilli() - startTime.toEpochMilli()) / (endTime.toEpochMilli() - startTime.toEpochMilli());
    }
}
