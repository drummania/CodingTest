package util;

import java.time.Instant;

public class TimerImpl implements TimerService {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
