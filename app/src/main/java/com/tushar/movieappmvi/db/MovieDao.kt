package com.tushar.movieappmvi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tushar.movieappmvi.models.KeywordModel
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.models.SimilarMoviesModel
import com.tushar.movieappmvi.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie : Movie) : Long

    @Query("""
        SELECT * from movie_table
        WHERE original_title LIKE '%' || :query || '%'
        OR overview LIKE '%' || :query || '%'
        ORDER BY datetime(timestamp_in_millis) ASC
        LIMIT(:pageNo * :pageSize)
    """)
    fun getMovies(
        query: String,
        pageNo: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ) : LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(keywordModel: KeywordModel) : Long

    @Query("SELECT * from keyword_table WHERE id= :id")
    fun getKeyword(id: Int) : LiveData<KeywordModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimilarMovies(similarMoviesModel: SimilarMoviesModel) : Long

    @Query("SELECT * from similar_movies_table WHERE id= :id")
    fun getSimilarMovies(id: Int) : LiveData<SimilarMoviesModel?>

}