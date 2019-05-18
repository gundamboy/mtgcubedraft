package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.CubeRepository;

import java.util.List;

public class CubeViewModel extends AndroidViewModel {
    private CubeRepository mCubeRepository;
    private LiveData<List<Cube>> mAllCubes;
    private LiveData<List<Cube>> mAllUsersCubes;
    private LiveData<Cube> mUserCube;

    public CubeViewModel(@NonNull Application application) {
        super(application);
        mCubeRepository = new CubeRepository(application);
        mAllCubes = mCubeRepository.mGetAllCubes();
//        mAllUsersCubes = mCubeRepository.getUserCubes(userID);
//        mUserCube = mCubeRepository.getUserCube(userID, cubeId);

    }

    public void insertCube(Cube cube) {
        mCubeRepository.insertCube(cube);
    }

    public void updateCube(Cube cube) {
        mCubeRepository.updateCube(cube);
    }

    public void deleteAllCubes() {
        mCubeRepository.deleteAllCubes();
    }

    public void deleteCube(Cube cube) {
        mCubeRepository.deleteCube(cube);
    }

    public LiveData<List<Cube>> getmAllCubes() {
        return mAllCubes;
    }

    public LiveData<List<Cube>> getmAllUsersCubes(String s) {
       return  mCubeRepository.getUserCubes(s);
    }

    public LiveData<Cube> getmUserCube(String s, Integer i) {
        return  mCubeRepository.getUserCube(s, i);
    }
}
