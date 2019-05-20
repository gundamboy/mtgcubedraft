package com.ragingclaw.mtgcubedraftsimulator.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragingclaw.mtgcubedraftsimulator.database.ApplicationRepository;
import com.ragingclaw.mtgcubedraftsimulator.database.User;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private ApplicationRepository mApplicationRepository;
    private User mUserID;
    private LiveData<List<User>> mAllUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mApplicationRepository = new ApplicationRepository(application);
    }

    public LiveData<List<User>> getmAllUsers() {
        return mApplicationRepository.getAllUsers();
    }

    public User getmUserID(String email, String password) {
        return mApplicationRepository.getUserId(email, password);
    }

    public void insertUser(User user) {
         mApplicationRepository.insertUser(user);
    }

    public void deleteUser(User user) {
        mApplicationRepository.deleteUser(user);
    }
}
