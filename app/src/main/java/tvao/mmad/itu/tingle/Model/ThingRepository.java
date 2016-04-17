package tvao.mmad.itu.tingle.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import tvao.mmad.itu.tingle.Database.DatabaseManager;
import tvao.mmad.itu.tingle.Database.ThingBaseHelper;
import tvao.mmad.itu.tingle.Database.ThingCursorWrapper;
import tvao.mmad.itu.tingle.Database.ThingDbSchema.ThingTable;

/**
 * ThingDB is a SQLite database implemented using the singleton pattern and is used to hold a list of things.
 */
public class ThingRepository implements IRepository {

    private static ThingRepository sThingRepository;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // Public access modifier
    public static ThingRepository get(Context context)
    {
        if (sThingRepository == null)
        {
            sThingRepository = new ThingRepository(context);
        }
        return sThingRepository;
    }

    // Private constructor to uphold Singleton pattern
    private ThingRepository(Context context)
    {
        mContext = context;

        // Get thread safe database
        DatabaseManager.initializeInstance(new ThingBaseHelper(mContext));
        mDatabase = DatabaseManager.getInstance().openDatabase();
    }

    // Private method used to shuttle Things
    private static ContentValues getContentValues(Thing thing)
    {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());
        values.put(ThingTable.Cols.BARCODE, thing.getBarcode());
        values.put(ThingTable.Cols.DATE, getDateTimeString(thing.getDate()));

        return values;
    }

    public void addThing(Thing thing)
    {
        ContentValues values = getContentValues(thing);
        mDatabase.insert(ThingTable.NAME, null, values);
    }

    /**
     * Returns a list of all things by using a cursor pointing on items in query.
     * @return list of all things from database
     */
    public List<Thing> getThings()
    {
        List<Thing> things = new ArrayList<>();

        ThingCursorWrapper cursor = queryThings(null, null);

        cursor.moveToFirst(); // Move to first element
        while (!cursor.isAfterLast()) // Pointer off end of data set
        {
            things.add(cursor.getThing());
            cursor.moveToNext(); // Advance to next item
        }
        cursor.close();

        return things;
    }

    public Thing getThing(UUID id)
    {
        ThingCursorWrapper cursor = queryThings(

                ThingTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try
        {
            if (cursor.getCount() == 0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getThing();
        }
        finally
        {
            cursor.close();
        }
    }

    /**
     * This method is used to find out where photos should live.
     * The method does not create any files on the file system.
     * It returns File objects that point to the right locations.
     * @param thing - thing of which photo file location should be found.
     * @return File object with correct photo file location
     */
    public File getPhotoFile(Thing thing)
    {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Verify that external storage exist for saving pictures
        if(externalFilesDir == null)
        {
            return null;
        }

        return new File(externalFilesDir, thing.getPhotoFilename());
    }

    // Delete a particular thing based on unique identifier
    public boolean removeThing(UUID id)
    {
        return mDatabase.delete(ThingTable.NAME,
                        ThingTable.Cols.UUID + " = ?",
                        new String[] { id.toString() }
                ) > 0;
        // return mDatabase.delete(ThingTable.NAME, ThingTable.Cols.UUID + "=" + id, null) > 0;
    }

    public void updateThing(Thing thing)
    {
        String uuidString = thing.getId().toString();

        ContentValues values = getContentValues(thing);

        mDatabase.update(ThingTable.NAME, values,
                ThingTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    // Method used to read data from SQLite database
    private ThingCursorWrapper queryThings(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(

                ThingTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ThingCursorWrapper(cursor);
    }

    // Returns datetime value used to set content value as string in SQLite
    public static String getDateTimeString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public int size()
    {
        return getThings().size();
    }

}
