package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.database.User;

import java.util.List;

public class DraftViewModel extends AndroidViewModel {
    private ApplicationRepository mApplicationRepository;

    public DraftViewModel(@NonNull Application application) {
        super(application);
        mApplicationRepository = new ApplicationRepository(application);
    }

    public void insertDraft(Draft draft) {
        mApplicationRepository.insertDraft(draft);
    }

    public void updateDraft(Draft draft) {
        mApplicationRepository.updateDraft(draft);
    }

    public void deleteDraft(Draft draft) {
        mApplicationRepository.deleteDraft(draft);
    }

    public void deleteAllDrafts() {
        mApplicationRepository.deleteAllDrafts();
    }

    public LiveData<List<Draft>> getUserDrafts(String userId) {
        return mApplicationRepository.getUserDrafts(userId);
    }

    public LiveData<Draft> getSingleDraft(Integer draftID) {
        return mApplicationRepository.getSingleDraft(draftID);
    }

    public LiveData<List<Draft>> getAllDrafts() {
        return mApplicationRepository.getAllDrafts();
    }

}
