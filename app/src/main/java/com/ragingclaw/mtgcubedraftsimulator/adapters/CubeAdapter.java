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
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CubeAdapter extends RecyclerView.Adapter<CubeAdapter.CardHolder> {
    private List<MagicCard> cards = new ArrayList<>();

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cube_list_item, parent, false);

        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        MagicCard currentCard = cards.get(position);

        String cmc = currentCard.getManaCost();
        String text = currentCard.getText();

        // fix the stupid formatting in the json. its dumb, and i hate it.
        // it looks like {4}{B}{B} and that's just stupid.
        cmc = cmc.replace("}{"," ");
        cmc = cmc.replace("{","");
        cmc = cmc.replace("}","");

        text = text.replace("}{"," ");
        text = text.replace("{","");
        text = text.replace("}","");

        holder.cardName.setText(currentCard.getName());
        holder.rarity.setText(currentCard.getRarity());
        holder.manaCost.setText(cmc);
        holder.creature_type.setText(currentCard.getType());
        holder.setName.setText(currentCard.getSetName());
        holder.cardText.setText(text);
        holder.flavorText.setText(currentCard.getFlavor());

        if (TextUtils.isEmpty(currentCard.getSetName()) || currentCard.getSetName() == null) {
            holder.setName.setText(currentCard.getSet());
        }

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<MagicCard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public List<MagicCard> getItems() {
        return cards;
    }

    class CardHolder extends RecyclerView.ViewHolder {
        @BindView (R.id.card_name) TextView cardName;
        @BindView (R.id.rarity) TextView rarity;
        @BindView (R.id.mana_cost) TextView manaCost;
        @BindView (R.id.creature_type) TextView creature_type;
        @BindView (R.id.set_name) TextView setName;
        @BindView (R.id.card_text) TextView cardText;
        @BindView (R.id.flavor_text)TextView flavorText;
        @BindView (R.id.mana_cost_framelayout) FrameLayout mana_cost_framelayout;

        public CardHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
