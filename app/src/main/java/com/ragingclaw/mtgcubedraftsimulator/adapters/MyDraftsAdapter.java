package com.ragingclaw.mtgcubedraftsimulator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDraftsAdapter extends RecyclerView.Adapter<MyDraftsAdapter.DraftHolder> {
    private List<Draft> drafts = new ArrayList<>();
    private MyDraftsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, int draftId, String draftName, int cubeId);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public DraftHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_my_drafts, parent, false);

        return new DraftHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DraftHolder holder, int position) {
        Draft currentDraft = drafts.get(position);
        String name = currentDraft.getDraftName();
        int cubeId = currentDraft.getCubeId();
        int id = currentDraft.getDraftID();

        holder.draftName.setText(name);
        holder.draftId.setText(String.valueOf(id));
        holder.cubeId.setText(String.valueOf(cubeId));

    }

    @Override
    public int getItemCount() {
        return drafts.size();
    }

    public void setDrafts(List<Draft> drafts) {
        this.drafts = drafts;
        notifyDataSetChanged();
    }

    public List<Draft> getItems() {
        return drafts;
    }

    class DraftHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.draft_name) TextView draftName;
        @BindView(R.id.draft_id) TextView draftId;
        @BindView(R.id.cube_id) TextView cubeId;

        @BindView (R.id.imageButton) ImageButton imageButton;

        public DraftHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, Integer.parseInt(draftId.getText().toString()), draftName.getText().toString(), Integer.parseInt(cubeId.getText().toString()));
                    }
                }
            });
        }
    }
}
