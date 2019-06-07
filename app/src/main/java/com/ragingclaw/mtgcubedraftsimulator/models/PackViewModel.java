package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;

import java.util.List;

public class PackViewModel extends AndroidViewModel {
    private final ApplicationRepository mApplicationRepository;
    private final LiveData<List<Pack>> mAllPacks;

    public PackViewModel(@NonNull Application application) {
        super(application);
        mApplicationRepository = new ApplicationRepository(application);
        mAllPacks = mApplicationRepository.getAllPacks();
    }

    public LiveData<List<Pack>> getAllPacks() {
        return mAllPacks;
    }

    public List<Pack> getAllPacksStatic() {return mApplicationRepository.getAllPacksStatic();}

    public List<Pack> getPlayerPacks(int seatNum) {
        return mApplicationRepository.getPlayerPacks(seatNum);
    }

    public Pack getPlayerPacksByNum(int seatNum, int boosterNum) {
        return mApplicationRepository.getPlayerPacksByNum(seatNum, boosterNum);
    }

    public LiveData<List<Pack>> getLivePlayerPacks(int seatNum) {
        return mApplicationRepository.getLivePlayerPacks(seatNum);
    }

    public long insertPack(Pack pack) {
        mApplicationRepository.insertPack(pack);
        return 0;
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
