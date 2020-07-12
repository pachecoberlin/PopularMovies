package de.pacheco.popularMovies.model;

import com.google.gson.annotations.SerializedName;

//@Entity(tableName = "relatedVideos")
public class RelatedVideo {
//    @PrimaryKey
    @SerializedName("id")
    public String relatedVideoID;
    @SerializedName("iso_639_1")
    public String iso_639_1;
    @SerializedName("iso_3166_1")
    public String iso_3166_1;
    @SerializedName("key")
    public String key;
    @SerializedName("name")
    public String name;
    @SerializedName("site")
    public String site;
    @SerializedName("size")
    public int size;
    @SerializedName("type")
    public String type;
}
