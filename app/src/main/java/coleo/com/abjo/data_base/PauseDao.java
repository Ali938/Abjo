package coleo.com.abjo.data_base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PauseDao {

    @Query("SELECT * FROM `Action`")
    List<Action> getAll();

    @Query("DELETE FROM `Action`")
    void nukeTable();

    @Insert
    void insertAll(Action... users);

    @Delete
    void delete(Action user);

}
