package volume;

import java.time.Instant;

public interface VolumeProfile {

    double getVolume(final Instant time);
}
