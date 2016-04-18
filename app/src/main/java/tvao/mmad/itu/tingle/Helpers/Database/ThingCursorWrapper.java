package tvao.mmad.itu.tingle.Helpers.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import tvao.mmad.itu.tingle.Helpers.Database.ThingDbSchema.ThingTable;
import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This class is used to wrap a Cursor (raw columns from query) from another place and add new methods on top of it.
 * The class creates a wrapper around a Cursor with the same methods as Cursor allowing us to extend methods further on raw columns.
 */
public class ThingCursorWrapper extends CursorWrapper {

    public ThingCursorWrapper(Cursor cursor)
    {
    super(cursor);
    }

    public Thing getThing()
    {
        // Get columns
        String uuidString = getString(getColumnIndex(ThingTable.Cols.UUID));
        String what = getString(getColumnIndex(ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(ThingTable.Cols.WHERE));
        String barcode = getString(getColumnIndex(ThingTable.Cols.BARCODE));
        String date = getString(getColumnIndex(ThingTable.Cols.DATE));

        // Find item and set data
        Thing thing = new Thing(UUID.fromString(uuidString));
        thing.setWhat(what);
        thing.setWhere(where);
        thing.setBarcode(barcode);
        thing.setDate(getDateFromString(date));

        return thing;
    }

    private Date getDateFromString(String dateText)
    {
        Date dateFromText = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try
        {
            dateFromText =  dateFormat.parse(dateText);
        }
        catch (ParseException ex)
        {
            ex.printStackTrace();
        }
        return dateFromText;
    }

}