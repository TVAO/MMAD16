package tvao.mmad.itu.tingle.Helpers.Database;

/**
 * Schema used to represent Thing model in database.
 */
public class ThingDbSchema {

    // Database table
    public static final class ThingTable {

        // Name of table in db
        public static final String NAME = "things";

        // Columns in database
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String WHAT = "what";
            public static final String WHERE = "location";
            public static final String BARCODE = "barcode";
            public static final String DATE = "date";
        }

    }

}


