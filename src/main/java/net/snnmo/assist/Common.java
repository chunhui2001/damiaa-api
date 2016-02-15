package net.snnmo.assist;

import java.util.UUID;

/**
 * Created by cc on 16/2/14.
 */
public class Common {
    public static String randomNumbers(int numberCount) {

        if (numberCount > 10 ) numberCount = 10;

        String rancomNumbers = null;

        do {

            rancomNumbers  = UUID.randomUUID().toString().replaceAll("[^0-9]+", "");

            if (rancomNumbers.length() >= numberCount) {
                rancomNumbers = rancomNumbers.substring(0, numberCount);
            }

        } while (rancomNumbers.length() < numberCount);

        return rancomNumbers;
    }
}
