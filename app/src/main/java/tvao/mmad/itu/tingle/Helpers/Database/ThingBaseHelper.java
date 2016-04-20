package tvao.mmad.itu.tingle.Helpers.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import tvao.mmad.itu.tingle.Helpers.Database.ThingDbSchema.ThingTable;


/**
 * Class designed to get rid of the grunt work of opening a SQLiteDatabase
 * Used to get inside to create Thing database in ThingRepository.
 */
public class ThingBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "thingBase.db";

    private static ThingBaseHelper sThingBaseHelper;

    public ThingBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Call get to get an instance of the SqLiteOpenHelper instance that is synchronized (thread-safe).
     * @param context
     * @return
     */
    public static synchronized ThingBaseHelper get(Context context)
    {
        if (sThingBaseHelper == null)
            sThingBaseHelper = new ThingBaseHelper(context);

        return sThingBaseHelper;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + ThingTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        ThingTable.Cols.UUID + ", " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE + ", " +
                        ThingTable.Cols.BARCODE + ", " +
                        ThingTable.Cols.DATE +
                ")"
        );
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS" + ThingTable.NAME);
        this.onCreate(db);
    }

}
