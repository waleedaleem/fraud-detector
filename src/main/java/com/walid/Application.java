package com.walid;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static final String[] HELP_ALIASES = { "-h", "-help", "--help"
    };

    public static void main(String[] args) throws IOException {
        if (System.getProperty("java.util.logging.config.file") == null) {
            LogManager.getLogManager()
                .readConfiguration(Application.class.getResourceAsStream("/logging.properties"));
        }

        if (args.length != 2 || Arrays.asList(HELP_ALIASES).contains(args[0])) {
            logger.warning(
                "usage: java -jar fraud-detector-x.y.z.jar <price threshold> <input file>");
            System.exit(0);
        }
    }
}
