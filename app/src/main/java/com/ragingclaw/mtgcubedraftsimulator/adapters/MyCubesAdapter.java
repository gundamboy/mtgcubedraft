package com.ragingclaw.mtgcubedraftsimulator.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCubesAdapter extends RecyclerView.Adapter<MyCubesAdapter.CubeHolder> {
    private List<Cube> cubes = new ArrayList<>();

    @NonNull
    @Override
    public CubeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_cubes_list_item, parent, false);

        return new CubeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CubeHolder holder, int position) {
        Cube currentCube = cubes.get(position);
        String name = currentCube.getCube_name();
        int count = currentCube.getTotal_cards();
        int id = currentCube.getCubeId();

        holder.cubeName.setText(name);
        holder.cardCount.setText(String.valueOf(count));
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
        @BindView (R.id.card_count) TextView cardCount;
        @BindView (R.id.cube_id) TextView cubeId;

        public CubeHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
