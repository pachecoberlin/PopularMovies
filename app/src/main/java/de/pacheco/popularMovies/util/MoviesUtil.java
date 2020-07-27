package de.pacheco.popularMovies.util;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.pacheco.popularMovies.ApiKey;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MovieResults;
import de.pacheco.popularMovies.model.ReviewResults;
import de.pacheco.popularMovies.model.TrailerResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesUtil {
    public static final String FAVOURITES = "favourites";
    public static final String W1280 = "w1280";
    public static final String W780 = "w780";
    private static final String TAG = MoviesUtil.class.getSimpleName();
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    private static final String TMDB_MOVIES_URL = "https://api.themoviedb.org";
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String DEFAULT_SIZE = "w300";
    /**
     * TODO Please provide your personal API key
     */
    private static final String API_KEY_VALUE = ApiKey.API_KEY_VALUE;
    public static final String RELATED_VIDEOS = "/videos";
    public static final String REVIEWS = "/reviews";
    public static final GetMoviesAPI SERVICE = getMovieService();


    public interface GetMoviesAPI {
        @GET("/3/movie/{criteria}")
        Call<MovieResults> getMovies(@Path("criteria") String criteria,
                                     @Query("api_key") String apiKey, @Query("page") int page);

        @GET("/3/movie/{movieId}/reviews")
        Call<ReviewResults> getReviews(@Path("movieId") int movieId,
                                       @Query("api_key") String apiKey);

        @GET("/3/movie/{movieId}/videos")
        Call<TrailerResults> getTrailer(@Path("movieId") int movieId,
                                        @Query("api_key") String apiKey);
    }

    public static void getMovies(String criteria, MutableLiveData<List<Movie>> liveData) {
        SERVICE.getMovies(criteria, API_KEY_VALUE, 1).enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                MovieResults body = response.body();
                List<Movie> movies = body.results;
                liveData.setValue(movies);
                addAllMovies(criteria, movies, body.totalPages);
            }
            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public static void addAllMovies(String criteria, List<Movie> movies, Integer maxPages) {
        if (maxPages < 0) {
            return;
        }
        int pageCounter = 2;
        while (pageCounter <= maxPages) {
            SERVICE.getMovies(criteria, API_KEY_VALUE, pageCounter++).enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                    MovieResults body = response.body();
                    movies.addAll(body.results);
                }
                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private static GetMoviesAPI getMovieService() {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(TMDB_MOVIES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return restAdapter.create(GetMoviesAPI.class);
    }

    public static Call<ReviewResults> getReviews(int movieId) {
        return SERVICE.getReviews(movieId, API_KEY_VALUE);
    }

    public static Call<TrailerResults> getTrailer(int movieId) {
        return SERVICE.getTrailer(movieId, API_KEY_VALUE);
    }
}