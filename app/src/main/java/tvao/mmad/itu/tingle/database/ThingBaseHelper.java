package tvao.mmad.itu.tingle.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import tvao.mmad.itu.tingle.Database.ThingDbSchema.ThingTable;


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

    public static synchronized ThingBaseHelper get(Context context)
    {
        if (sThingBaseHelper == null)
            sThingBaseHelper = new ThingBaseHelper(context);

        return sThingBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + ThingTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        ThingTable.Cols.UUID + ", " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE + ", " +
                        ThingTable.Cols.BARCODE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

}
