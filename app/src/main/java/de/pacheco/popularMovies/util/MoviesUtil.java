package de.pacheco.popularMovies.util;

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

import de.pacheco.popularMovies.ApiKey;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.RelatedVideo;
import de.pacheco.popularMovies.model.Review;

public class MoviesUtil {
    private static final String TAG = MoviesUtil.class.getSimpleName();
    public static final String POPULAR = "popular";
    public static final String RATED = "top_rated";
    private static final String TMDB_MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    public static final String DEFAULT_SIZE = "w300";
    private static final String API_KEY_PARAM = "api_key";
    /**
     * TODO Please provide your personal API key
     */
    private static final String API_KEY_VALUE = ApiKey.API_KEY_VALUE;
    public static final String PAGE_PARAM = "page";
    public static final String RELATED_VIDEOS = "/videos";
    public static final String REVIEWS = "/reviews";

    /**
     * All movies apart from result page two will be added to the given list
     *
     * @param movies   the list which is filled
     * @param criteria the sort criteria
     */
    public static void addAllMovies(List<Movie> movies, String criteria) {
        int maxPage = getMaxPage(criteria);
        if (maxPage < 0) {
            return;
        }
        maxPage = 2; //TODO delete line
        int pageCounter = 2;
        while (pageCounter <= maxPage) {
            String response = getMoviesAsJson(String.valueOf(pageCounter++), criteria);
            if (response.isEmpty()) {
                return;
            }
            movies.addAll(createMoviesFromJson(response));
        }
    }

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
            List<Movie> movies = parseResponse(jsonString, Movie.class);
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

    private static <T> List<T> parseResponse(String jsonString, Class<T> clazz) throws JSONException {
        List<T> list = new LinkedList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        Gson gson = new Gson();
        JSONArray results = jsonObject.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            String movieAsJson = results.get(i).toString();
            T parsedObject = gson.fromJson(movieAsJson, clazz);
            list.add(parsedObject);
        }
        return list;
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
        return getUrl(builder);
    }

    private static URL getUrl(Uri.Builder builder) {
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

    public static <T> List<T> getMovieInformation(String whichInfo, String movieId) {
        Uri.Builder builder = Uri.parse(TMDB_MOVIES_URL + movieId + whichInfo).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        Class<?> type = REVIEWS.equals(whichInfo) ? Review.class :
                RELATED_VIDEOS.equals(whichInfo) ?
                RelatedVideo.class : null;
        try {
            //noinspection unchecked
            return (List<T>) parseResponse(NetworkUtils.getResponseFromHttpUrl(getUrl(builder)),
                    type);
        } catch (IOException | JSONException | NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "Error " + e.getMessage());
        }
        return Collections.emptyList();
    }
}