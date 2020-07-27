package de.pacheco.popularMovies.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import de.pacheco.popularMovies.ApiKey;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MovieResults;
import de.pacheco.popularMovies.model.RelatedVideo;
import de.pacheco.popularMovies.model.Review;
import de.pacheco.popularMovies.model.ReviewResults;
import de.pacheco.popularMovies.model.TrailerResults;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

//TODO implement CALL macht die Klassse
public class MoviesUtil {
    public static final String FAVOURITES = "favourites";
    public static final String W1280 = "w1280";
    public static final String W780 = "w780";
    private static final String TAG = MoviesUtil.class.getSimpleName();
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    private static final String TMDB_MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String DEFAULT_SIZE = "w300";
    /**
     * TODO Please provide your personal API key
     */
    private static final String API_KEY_VALUE = ApiKey.API_KEY_VALUE;
    public static final String RELATED_VIDEOS = "/videos";
    public static final String REVIEWS = "/reviews";
    public static final GetMoviesAPI SERVICE = getMovieService();

    public static void addAllMovies(List<Movie> movies, String criteria) {
        Integer maxPage = SERVICE.getMovies(criteria, API_KEY_VALUE, 1).totalPages;
        if (maxPage < 0) {
            return;
        }
        int pageCounter = 2;
        while (pageCounter <= maxPage) {
            MovieResults movieResults = SERVICE.getMovies(criteria, API_KEY_VALUE, pageCounter++);
            movies.addAll(movieResults.results);
        }
    }

    public interface GetMoviesAPI {
        @GET("/{criteria}")
        MovieResults getMovies(@Path("criteria") String criteria,
                               @Query("api_key") String apiKey, @Query("page") int page);

        @GET("/{movieId}/reviews")
        ReviewResults getReviews(@Path("movieId") String movieId, @Query("api_key") String apiKey);

        @GET("/{movieId}/videos")
        TrailerResults getTrailer(@Path("movieId") String movieId, @Query("api_key") String apiKey);
    }

    public static List<Movie> getFirstMovies(String criteria) {
        MovieResults movieResults = SERVICE.getMovies(criteria, API_KEY_VALUE, 1);
        return movieResults.results;
    }

    private static GetMoviesAPI getMovieService() {
        Gson gson = new GsonBuilder().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TMDB_MOVIES_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter.create(GetMoviesAPI.class);
    }

    public static List<Review> getReviews(String movieId) {
        ReviewResults reviewResults = SERVICE.getReviews(movieId, API_KEY_VALUE);
        return reviewResults.results;
    }

    public static List<RelatedVideo> getTrailer(String movieId) {
        TrailerResults trailerResults = SERVICE.getTrailer(movieId, API_KEY_VALUE);
        return trailerResults.results;
    }

    public static List<Movie> getFirstMovies() {
        return getFirstMovies(POPULAR);
    }
}