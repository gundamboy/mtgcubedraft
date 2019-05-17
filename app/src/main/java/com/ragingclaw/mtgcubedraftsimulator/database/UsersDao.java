package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UsersEntity... usersEntities);

    @Update
    void updateUser(UsersEntity... usersEntities);

    @Query("SELECT users.userID From users WHERE users.email = :email AND users.password = :password")
    UsersEntity getUserId(String email, String password);

    @Query("DELETE From users")
    void deleteAllUsers();

    @Query("DELETE From users WHERE users.userID = :userId")
    void deleteUser(String userId);
}
