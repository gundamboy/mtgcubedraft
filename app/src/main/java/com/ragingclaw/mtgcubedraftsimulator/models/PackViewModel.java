package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;
import com.ragingclaw.mtgcubedraftsimulator.database.User;

import java.util.List;

public class PackViewModel extends AndroidViewModel {
    private ApplicationRepository mApplicationRepository;

    public PackViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Pack>> getAllPacks() {
        return mApplicationRepository.getAllPacks();
    }

    public void insertPack(Pack pack) {
        mApplicationRepository.insertPack(pack);
    }

    public void updatePack(Pack pack) {
        mApplicationRepository.updatePack(pack);
    }

    public void deletePack(Pack pack) {
        mApplicationRepository.deletePack(pack);
    }

    public void deleteAllPacks() {
        mApplicationRepository.deleteAllPacks();
    }
}
