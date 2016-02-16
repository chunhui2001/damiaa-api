package net.snnmo.assist;

import com.google.common.base.CharMatcher;

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

            if (rancomNumbers.indexOf('0') == 0)
                rancomNumbers = CharMatcher.is('0').trimFrom(rancomNumbers);

            if (rancomNumbers.length() >= numberCount) {
                rancomNumbers = rancomNumbers.substring(0, numberCount);
            }

        } while (rancomNumbers.length() < numberCount);

        return rancomNumbers;
    }
}
