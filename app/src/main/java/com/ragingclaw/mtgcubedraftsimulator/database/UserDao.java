package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * From users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT users.userID From users WHERE users.email = :email AND users.password = :password")
    User getUserId(String email, String password);

    @Query("DELETE From users")
    void deleteAllUsers();

    @Query("DELETE From users WHERE users.userID = :userId")
    void deleteUserById(String userId);

    @Delete
    void deleteUser(User user);
}
