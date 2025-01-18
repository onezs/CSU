package com.example.didi.ui.edit;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didi.R;
import com.example.didi.beans.PathBean;

import java.util.List;

public class LocationEditAdapter extends RecyclerView.Adapter<LocationEditAdapter.MyViewHolder> {
    private final List<PathBean> mList;

    public LocationEditAdapter(List<PathBean> list) {
        mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_edit_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PathBean pathBean = mList.get(position);
        holder.locationEt.setText(pathBean.getLocation());
        holder.destinationEt.setText(pathBean.getDestination());
        holder.carriageEt.setText(pathBean.getCarriage() > 0 ? String.valueOf(pathBean.getCarriage()) : "");

        // Add button logic
        holder.addBtn.setOnClickListener(view -> {
            mList.add(position + 1, new PathBean());
            notifyItemInserted(position + 1);
        });

        // Remove button logic
        holder.removeBtn.setOnClickListener(view -> {
            if (mList.size() > 1) {
                mList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText locationEt;
        EditText destinationEt;
        EditText carriageEt;
        ImageButton addBtn;
        ImageButton removeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            locationEt = itemView.findViewById(R.id.et_location);
            destinationEt = itemView.findViewById(R.id.et_destination);
            carriageEt = itemView.findViewById(R.id.et_carriage);
            addBtn = itemView.findViewById(R.id.imgBtn_add);
            removeBtn = itemView.findViewById(R.id.imgBtn_remove);

            // Attach TextWatchers
            locationEt.addTextChangedListener(new LocationTextWatcher());
            destinationEt.addTextChangedListener(new DestinationTextWatcher());
            carriageEt.addTextChangedListener(new CarriageTextWatcher());
        }

        private class LocationTextWatcher implements TextWatcher {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mList.get(getAdapterPosition()).setLocation(s.toString());
            }

            // Other required methods are omitted for brevity
        }

        private class DestinationTextWatcher implements TextWatcher {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mList.get(getAdapterPosition()).setDestination(s.toString());
            }

            // Other required methods are omitted for brevity
        }

        private class CarriageTextWatcher implements TextWatcher {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float carriage = TextUtils.isEmpty(s.toString()) ? 0.0f : Float.parseFloat(s.toString());
                    mList.get(getAdapterPosition()).setCarriage(carriage);
                } catch (NumberFormatException e) {
                    Log.e("CarriageTextWatcher", "Invalid carriage input");
                }
            }

            // Other required methods are omitted for brevity
        }
    }
}
