package tvao.mmad.itu.tingle.Controller.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import tvao.mmad.itu.tingle.Model.Thing;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.view.View.OnClickListener;
import static android.view.View.OnLongClickListener;

/**
 * This Adapter is used to communicate with the RecyclerView,
 * when a ViewHolder needs to be created or connected with a Thing object.
 * RecyclerView does not know about Thing object but Thing Adapter knows about Thing model.
 */
public class ThingAdapter extends Adapter<ThingHolder> implements OnClickListener, OnLongClickListener {

    private List<Thing> mThings;
    private static Context sContext;
    //private int selectedPosition;
    //private ThingHolder mThingHolder;

    public ThingAdapter(List<Thing> things, Context context)
    {
        mThings = things;
        sContext = context;
        //selectedPosition = 0;
    }

    /**
     * Create view and wrap it in a view holder (for single list item).
     * Inflates the row layout and initializes the View Holder.
     * Once the View Holder is initialized it manages the findViewById() methods,
     * finding the views once and recycling them to avoid repeated calls
     * @param parent - host activity
     * @param viewType - type of view
     * @return view holder for item
     */
    @Override
    public ThingHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

//        ThingHolder holder = new ThingHolder(view);
//        holder.itemView.setOnClickListener(ThingAdapter.this);
//        holder.itemView.setOnLongClickListener(ThingAdapter.this);
//        holder.itemView.setTag(holder);
//        mThingHolder = new ThingHolder(view);
//        return mThingHolder;

        return new ThingHolder(view, mThings);
    }

    /**
     * Bind ViewHolder view to model object (Thing data to view holder with text view on screen)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ThingHolder holder, int position)
    {
        Thing thing = mThings.get(position);
        holder.bindThing(thing);
        //holder.itemView.setSelected(selectedPosition == position); // Select item at position
    }

    /**
     * Get total amount of items to display
     * @return amount of items
     */
    @Override
    public int getItemCount()
    {
        return mThings.size();
    }

    /**
     * Remove Thing from row in RecyclerView
     * @param position - location of item in list
     */
    public void removeAt(int position)
    {
        mThings.remove(position);
        notifyItemRemoved(position); // Refresh items
        notifyItemRangeChanged(position, getItemCount()); // Adjust all views below deleted item
    }

    @Override
    public void onClick(View view) {
//            notifyItemChanged(selectedPosition);
//            selectedPosition = mThingHolder.getLayoutPosition();
//            notifyItemChanged(selectedPosition);
    }

    @Override
    public boolean onLongClick(View view) {
        ThingHolder holder = (ThingHolder) view.getTag();
        Thing t = holder.getThing();


        //if (view.getId() == holder.itemView.getId()) {
            // mThings.remove(holder.itemView.getId()); // Todo fix id from list, should be between 0 and 4

            mThings.remove(t.getId());

            // Mark selected item
            if(holder.itemView.getBackground() == null
                    || holder.itemView.getBackground().equals(Color.TRANSPARENT)) {
                holder.itemView.setBackgroundColor(Color.CYAN);
            }
            else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            notifyDataSetChanged();

            // Confirm which item was removed 
            //Toast.makeText(sContext, "Item " + holder.itemView.getTag().toString() + " has been removed from list",
             //       Toast.LENGTH_SHORT).show();
            Toast.makeText(sContext, "Item " + t.getWhat() + " has been removed from list",
                Toast.LENGTH_SHORT).show();
        //}
        return false;
    }


    public void setThings(List<Thing> things) {
        mThings = things;
    }
}
