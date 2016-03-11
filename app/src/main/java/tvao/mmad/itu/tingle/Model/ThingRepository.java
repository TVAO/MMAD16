package tvao.mmad.itu.tingle.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import tvao.mmad.itu.tingle.Database.ThingBaseHelper;

/**
 * ThingDB is an in-memory database implemented using the singleton pattern and is used to hold a list of things.
 */
public class ThingRepository implements IRepository {
    private static ThingRepository sThingRepository;

    // Fake database
    private List<Thing> mThingsDB;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // Private constructor to uphold Singleton pattern
    private ThingRepository(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new ThingBaseHelper(mContext)
                .getWritableDatabase();

        mThingsDB = new ArrayList<Thing>();

        fillThings(); // Test data
    }

    // Public access modifier
    public static ThingRepository get(Context context)
    {
        if (sThingRepository == null)
        {
            sThingRepository = new ThingRepository(context);
        }
        return sThingRepository;
    }

    public Iterator<Thing> getAll()
    {
        return mThingsDB.iterator();
    }

    public List<Thing> getThings() { return mThingsDB; }

    public void addThing(Thing thing) { mThingsDB.add(thing);}

    public void removeThing(int position) { mThingsDB.remove(position); }

    public int size() { return mThingsDB.size(); }

    public Thing get(int i) {return mThingsDB.get(i);}

    public Thing getThing(UUID id)
    {
        for (Thing thing : mThingsDB)
        {
            if(thing.getId().equals(id))
            {
                return thing;
            }
        }
        return null;
    }

    // Used to generate test data 
    private void fillThings()
    {
        addThing(new Thing("Android Phone", "Desk"));
        addThing(new Thing("Keys", "Kitchen"));
        addThing(new Thing("Book", "Bag"));
        addThing(new Thing("Jacket", "Closet"));
        addThing(new Thing("Notes", "Office"));
    }

}
