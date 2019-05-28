package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;

import java.util.List;

public class CubeViewModel extends AndroidViewModel {
    private ApplicationRepository mApplicationRepository;
    private LiveData<List<Cube>> mAllCubes;

    public CubeViewModel(@NonNull Application application) {
        super(application);
        mApplicationRepository = new ApplicationRepository(application);
        mAllCubes = mApplicationRepository.mGetAllCubes();
    }

    public void insertCube(Cube cube) {
        mApplicationRepository.insertCube(cube);
    }

    public void updateCube(Cube cube) {
        mApplicationRepository.updateCube(cube);
    }

    public void deleteAllCubes() {
        mApplicationRepository.deleteAllCubes();
    }

    public void deleteCube(Cube cube) {
        mApplicationRepository.deleteCube(cube);
    }

    public LiveData<List<Cube>> getmAllCubes() {
        return mAllCubes;
    }

    public LiveData<List<Cube>> getmAllUsersCubes(String s) {
       return  mApplicationRepository.getUserCubes(s);
    }

    public Cube getmUserCube(String s, Integer i) {
        return  mApplicationRepository.getUserCube(s, i);
    }
}
