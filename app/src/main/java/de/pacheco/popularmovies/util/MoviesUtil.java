package de.pacheco.popularmovies.util;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.pacheco.popularmovies.model.Movie;

public class MoviesUtil {
    private static final String TAG = MoviesUtil.class.getSimpleName();
    public static final String POPULAR = "popular";
    public static final String RATED = "top_rated";
    private static final String TMDB_MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_SIZE = "w185";
    private static final String API_KEY_PARAM = "api_key";
    /**
     * TODO Please provide your personal API key
     */
    private static final String API_KEY_VALUE = "";
    public static final String PAGE_PARAM = "page";

    /**
     * All movies apart from result page two will be added to the given list
     *
     * @param movies   the list which is filled
     * @param criteria the sort criteria
     */
    public static void addAllMovies(List<Movie> movies, String criteria) {
        //TODO i seem to have memory issues, when scrolling to fast. Does have something to do
        // with Picasso library. GC is trying to clean up, but seems to not be fast enough. Please
        // help. As a workaround and because it was not a requirement i only use 200 movies.
//        int maxPage = getMaxPage();
//        if (maxPage < 0) {
//            return;
//        }
        int maxPage = 10;
        int pageCounter = 2;
        while (pageCounter <= maxPage) {
            String response = getMoviesAsJson(String.valueOf(pageCounter++), criteria);
            if (response.isEmpty()) {
                return;
            }
            movies.addAll(createMoviesFromJson(response));
        }
    }

    @SuppressWarnings("unused")
    private static int getMaxPage(String criteria) {
        String response = getMoviesAsJson(null, criteria);
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Movie> getFirstMovies() {
        return getFirstMovies(POPULAR);
    }

    public static List<Movie> getFirstMovies(String criteria) {
        String response = getMoviesAsJson(null, criteria);
        return createMoviesFromJson(response);
    }

    private static List<Movie> createMoviesFromJson(String jsonString) {
        try {
            List<Movie> movies = parseResponse(jsonString);
            String size = String.valueOf(movies.size());
            Log.v(TAG, "Movie list size " + size);
            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static String getMoviesAsJson(@Nullable String page, String criteria) {
        URL url = buildUrl(page, criteria);
        String response = "";
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error " + e.getMessage());
        }
        return response;
    }

    private static List<Movie> parseResponse(String jsonString) throws JSONException {
        List<Movie> movies = new LinkedList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        Gson gson = new Gson();
        JSONArray results = jsonObject.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            String movieAsJson = results.get(i).toString();
            Movie movie = gson.fromJson(movieAsJson, Movie.class);
            if (movie.posterPath == null) {
                Log.d(TAG, "Movie without poster ignored: " + movie.title);
                continue;
            }
            movie.setFullPosterPath(BASE_POSTER_URL + DEFAULT_SIZE + movie.posterPath);
            movies.add(movie);
        }
        return movies;
    }

    /**
     * Builds the URL used to talk to the tmbd server
     *
     * @param page,    which page of the popular movies results shall be loaded
     * @param criteria the sort criteria
     * @return The URL to use to query the tmbd server.
     */
    private static URL buildUrl(@Nullable String page, String criteria) {
        Uri.Builder builder = Uri.parse(TMDB_MOVIES_URL + criteria).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        if (page != null) {
            builder.appendQueryParameter(PAGE_PARAM, page);
        }
        Uri builtUri = builder.build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }
}