package slice;

import util.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SliceManager implements SliceEvent {

    private final Map<String, Slice> slices;
    private final Logger logger;
    private int counter = 1;

    public SliceManager(final Logger logger) {
        slices = new HashMap<>();
        this.logger = logger;
    }

    public double getOpenQty() {

        return slices.values()
                .stream()
                .mapToDouble(Slice::getOpenQuantity)
                .sum();
    }

    public double getFilledQty() {

        return slices.values()
                .stream()
                .mapToDouble(Slice::getFilledQuantity)
                .sum();
    }

    public void entry(final double quantity, final double price, final String sliceRef) {

        final String effectiveSliceRef = sliceRef + " " + String.valueOf(counter++);

        final Slice slice = new Slice() {
            @Override
            public double getSlicePrice() {
                return price;
            }

            @Override
            public double getSliceQuantity() {
                return quantity;
            }

            @Override
            public double getOpenQuantity() {
                return quantity;
            }

            @Override
            public double getFilledQuantity() {
                return 0;
            }

            @Override
            public String getSliceRef() {
                return effectiveSliceRef;
            }
        };

        if (Double.compare(quantity, 0.0) > 0) {

            logger.debug("Send " + effectiveSliceRef + " : " + quantity + "@" + slice.getSlicePrice());
            slices.put(effectiveSliceRef, slice);
        }
    }

    public void printInfo() {

        logger.debug("Slice total open Qty = " + getOpenQty() + ", Slice total filled Qty = " + getFilledQty());
    }

    public List<Slice> getOpenSlice() {
        return slices.values()
                .stream()
                .filter(slice -> Double.compare(slice.getOpenQuantity(), 0.0) > 0)
                .collect(Collectors.toList());
    }

    public void amend(Slice slice, double newPrice) {

        slices.remove(slice);

        slices.put(slice.getSliceRef(), new Slice() {
            @Override
            public double getSlicePrice() {
                return newPrice;
            }

            @Override
            public double getSliceQuantity() {
                return slice.getSliceQuantity();
            }

            @Override
            public double getOpenQuantity() {
                return slice.getOpenQuantity();
            }

            @Override
            public double getFilledQuantity() {
                return slice.getFilledQuantity();
            }

            @Override
            public String getSliceRef() {
                return slice.getSliceRef();
            }
        });

        logger.debug("Amend " + slice.getSliceRef() + " : " + slice.getSliceQuantity() + "@" + newPrice);
    }

    public double getAvgFilledPrice() {

        double avgFilledPrice = 0.0;
        double totalFilledQty = 0.0;

        for (Slice slice : slices.values()) {

            final double sliceFilledQty = slice.getFilledQuantity();
            final double sliceFilledPrice = slice.getSlicePrice();

            avgFilledPrice = (avgFilledPrice * totalFilledQty + sliceFilledPrice * sliceFilledQty)
                    / (totalFilledQty + sliceFilledQty);
            totalFilledQty += sliceFilledQty;
        }
        return avgFilledPrice;
    }


    public void fill(final Slice slice, final double filledQuantity, final double filledPrice) {

        slices.remove(slice);

        slices.put(slice.getSliceRef(), new Slice() {

            @Override
            public double getSlicePrice() {
                return filledPrice;
            }

            @Override
            public double getSliceQuantity() {
                return slice.getSliceQuantity();
            }

            @Override
            public double getOpenQuantity() {
                return slice.getSliceQuantity() - filledQuantity;
            }

            @Override
            public double getFilledQuantity() {
                return filledQuantity;
            }

            @Override
            public String getSliceRef() {
                return slice.getSliceRef();
            }
        });

        logger.debug(slice.getSliceRef() + " is filled with " + filledQuantity + "@" + filledPrice);
    }

    @Override
    public void onSliceFill() {
        //todo : invoke fill() based on fill event
    }
}