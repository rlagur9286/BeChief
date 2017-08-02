package unithon.bechef.util.trans.service;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import unithon.bechef.util.trans.object.BookList;

public interface GoogleApiClient {
    @GET("/books/v1/volumes")
    Call<BookList> getVolumeBookList(
            @Query("q") String q,
            @Query("maxResults") String maxResults);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
