package com.github.valdr.cli;

import java.util.ListIterator;
import lombok.Getter;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * The {@link BasicParser} throws an exception when a required option or arg is missing in the command line. This parser swallows such errors. However, once it's done you can call
 * {@link GracefulCliParser#isIncomplete ()} to find out if such a swallowed error did in fact occur.
 */
public class GracefulCliParser extends BasicParser {

    @Getter
    private boolean incomplete;

    @Override
    protected void checkRequiredOptions() {
        try {
            super.checkRequiredOptions();
        } catch (MissingOptionException e) {
            incomplete = true;
        }
    }

    @Override
    public void processArgs(final Option opt, final ListIterator iter) {
        try {
            super.processArgs(opt, iter);
        } catch (ParseException e) {
            incomplete = true;
        }
    }

    /**
     * Obtém o valor de incomplete.
     * @return incomplete
     */
    public boolean isIncomplete() {
        return incomplete;
    }

}