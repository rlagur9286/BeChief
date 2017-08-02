package unithon.bechef.util.trans.object;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@Data
public class VolumeInfo implements Serializable {
    private String title;
    private ArrayList<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private ArrayList<Map<String, String>> industryIdentifiers;
    private int pageCount;
    private String printType;
    private ArrayList<String> categories;
    private float averageRating;
    private int ratingsCount;
    private Map<String, String> imageLinks;
    private String language;
    private String previewLink;
    private String infoLink;
    private String canonicalVolumeLink;
}
