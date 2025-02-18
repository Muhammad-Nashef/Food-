package com.example.foodplus;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IngredientAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mIngredients;

    public IngredientAdapter(Context context, String[] ingredients) {
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public int getCount() {
        return mIngredients.length;
    }

    @Override
    public Object getItem(int position) {
        return mIngredients[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.imageView);
            holder.ingredientNameTextView = view.findViewById(R.id.ingredientNameTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set ingredient name
        holder.ingredientNameTextView.setText(mIngredients[position]);

        // Set image dynamically based on the ingredient name (similar to before)
        String imageName = mIngredients[position].toLowerCase(); // Assuming image names match ingredient names
        int resId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        holder.imageView.setImageResource(resId);

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView ingredientNameTextView;
    }
}