package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;

import java.util.List;

public class MagicCardViewModel extends AndroidViewModel {
    private ApplicationRepository mApplicationRepository;
    private LiveData<List<MagicCard>> mAllCards;

    public MagicCardViewModel(@NonNull Application application) {
        super(application);
        mApplicationRepository = new ApplicationRepository(application);
        mAllCards = mApplicationRepository.mGetAllCards();
    }

    public void insertCard(MagicCard magicCard) {
        mApplicationRepository.insertCard(magicCard);
    }

    public void updateCard(MagicCard magicCard) {
        mApplicationRepository.updateCard(magicCard);
    }

    public void deleteAllcards() {
        mApplicationRepository.deleteAllCards();
    }

    public void deleteCard(MagicCard magicCard) {
        mApplicationRepository.deleteCard(magicCard);
    }

    public LiveData<List<MagicCard>> getmAllCards() {
        return mAllCards;
    }

    public LiveData<MagicCard> getmCard(Integer id) {
        return mApplicationRepository.mGetSingleCard(id);
    }
}
