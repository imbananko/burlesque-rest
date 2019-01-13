package com.imbananko.config;

public class Path {

    private static final String BASE_PATH = "/burlesque/api/";
    public static final String BASE_WILDCARD_PATH = BASE_PATH + "*";

    public static class ACCOUNTS {
        private static final String BASE_PATH = Path.BASE_PATH + "accounts";

        public static final String ALL = BASE_PATH;
        public static final String BY_ID = BASE_PATH + "/:id";
        public static final String CREATE = BASE_PATH + "/create";
    }

    public static class TRANSACTIONS {
        private static final String BASE_PATH = Path.BASE_PATH + "transactions";

        public static final String ALL = BASE_PATH;
        public static final String BY_ID = BASE_PATH + "/:id";
        public static final String BY_TRADING_ACCOUNT_ID = BASE_PATH + "/trading-account/:id";
        public static final String BY_CONTRA_ACCOUNT_ID = BASE_PATH + "/contra-account/:id";
    }

    public static class TRANSFER {
        public static final String BASE_PATH = Path.BASE_PATH + "transfer";
    }
}
