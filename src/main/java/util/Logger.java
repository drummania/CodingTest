package util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Logger {

    private final TimerService timerService;

    public Logger(TimerService timerService) {
        this.timerService = timerService;
    }

    public void debug(String log) {

        System.out.println(Formatter.formatInstant(timerService.now()) +" : " + log);
    }
}
