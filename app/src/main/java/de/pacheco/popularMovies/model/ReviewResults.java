package de.pacheco.popularMovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResults {
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;
    @SerializedName("results")
    @Expose
    public List<Review> results = null;
}