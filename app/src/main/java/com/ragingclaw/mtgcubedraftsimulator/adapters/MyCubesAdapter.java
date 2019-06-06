package com.ragingclaw.mtgcubedraftsimulator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCubesAdapter extends RecyclerView.Adapter<MyCubesAdapter.CubeHolder> {
    private List<Cube> cubes = new ArrayList<>();
    private MyCubesAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, int cubeId, String cubeName);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CubeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_my_cubes, parent, false);

        return new CubeHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CubeHolder holder, int position) {
        Cube currentCube = cubes.get(position);
        String name = currentCube.getCube_name();
        int id = currentCube.getCubeId();

        holder.cubeName.setText(name);
        holder.cubeId.setText(String.valueOf(id));
    }

    @Override
    public int getItemCount() {
        return cubes.size();
    }

    public void setCubes(List<Cube> cubes) {
        this.cubes = cubes;
        notifyDataSetChanged();
    }

    public List<Cube> getItems() {
        return cubes;
    }

    class CubeHolder extends RecyclerView.ViewHolder {
        @BindView (R.id.cube_name) TextView cubeName;
        @BindView (R.id.cube_id) TextView cubeId;
        @BindView (R.id.imageButton) ImageButton imageButton;

        public CubeHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, Integer.parseInt(cubeId.getText().toString()), cubeName.getText().toString());
                    }
                }
            });
        }
    }
}
