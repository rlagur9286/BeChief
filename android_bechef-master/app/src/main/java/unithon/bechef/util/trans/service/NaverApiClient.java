package unithon.bechef.util.trans.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface NaverApiClient {

    @FormUrlEncoded
    @POST("/v1/voice/tts.bin")
    Call<ResponseBody> getVoiceData(
            @Field("speaker") String speaker,
            @Field("speed") int speed,
            @Field(value = "text", encoded = true) String text);
}
