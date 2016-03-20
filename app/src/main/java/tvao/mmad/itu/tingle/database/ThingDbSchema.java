package tvao.mmad.itu.tingle.Database;

/**
 * Schema used to represent Thing model in database.
 */
public class ThingDbSchema {

    // Name of table in db
    public static final class ThingTable {

        public static final String NAME = "things";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String WHAT = "what";
            public static final String WHERE = "location";
            public static final String BARCODE = "barcode";
        }

    }

}


