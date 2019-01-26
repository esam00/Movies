package com.example.android.movies.database;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {
    // when menu item selected is my favorite
    @Query("select * from movie order by id")
    LiveData<List<MovieEntry>> loadAllMovies();

    @Query("select * from movie where mMovieId = :movieId")
    MovieEntry loadMovieById (int movieId);

    //when like button pressed
    @Insert
    void insertMovie (MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    //when like button pressed on an already liked movie
    @Delete ()
    void deleteMovie(MovieEntry movieEntry);
}
