public class App {

    public static void main(String[] args) {

//        LocalDateTime startTimeLDT = LocalDate.now().atTime(9,0);
//        LocalDateTime endTimeLDT = LocalDate.now().atTime(16,0);
//        Instant startTime = startTimeLDT.atZone(ZoneId.systemDefault()).toInstant();
//        Instant endTime = endTimeLDT.atZone(ZoneId.systemDefault()).toInstant();
//
//        Order order = new Order(startTime, endTime, 1000.0, 10.0);
//        LinearProfile volumeProfile = new LinearProfile(startTime, endTime);
//        Vwap vwap = new Vwap(volumeProfile, order);
//
//        Timer time = new Timer();
//        time.schedule(vwap, 0, TimeUnit.SECONDS.toMillis());
//
//        OrderBook orderBook = new OrderBook(10.0, 10.01);
//        Slice slice = new Slice();
//
//        Instant t = startTime;
//        int cycleLenInSec = 60;
//        Instant cycleDueTime = t.plusSeconds(cycleLenInSec);
//        int cycle = 1;
//
//
//
//        while (!cycleDueTime.isAfter(endTime)) {
//
//            double sliceVolume = Math.round(volumeProfile.getVolume(cycleDueTime) * order.getQuantity());
//            System.out.println("Cycle:" + cycle + ", due time: " + cycleDueTime.toString() + ", slice volume:" + sliceVolume);
//
//            // next cycle
//            cycle++;
//            cycleDueTime = cycleDueTime.plusSeconds(cycleLenInSec);
//        }

    }
}
