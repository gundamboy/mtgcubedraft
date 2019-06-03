package com.ragingclaw.mtgcubedraftsimulator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DraftCardsAdapter extends RecyclerView.Adapter<DraftCardsAdapter.CardHolder> {
    private List<MagicCard> cards = new ArrayList<>();
    private DraftCardsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, int cardId, View view, String url);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_grid_item, parent, false);

        return new CardHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        MagicCard currentCard = cards.get(position);
        String url = currentCard.getImageUrl();
        holder.cardUrl.setText(url);

        Picasso.get().load(url).placeholder(R.drawable.mtg_card_back).into(holder.mtgCardImage);

        holder.cardId.setText(String.valueOf(currentCard.getMultiverseid()));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<MagicCard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    class CardHolder extends RecyclerView.ViewHolder {
        @BindView (R.id.mtg_card) ImageView mtgCardImage;
        @BindView (R.id.cardId) TextView cardId;
        @BindView (R.id.cardUrl) TextView cardUrl;

        public CardHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mtgCardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, Integer.parseInt(cardId.getText().toString()), mtgCardImage, cardUrl.getText().toString());
                    }
                }
            });
        }
    }
}
