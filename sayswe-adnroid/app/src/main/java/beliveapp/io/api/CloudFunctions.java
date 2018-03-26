package beliveapp.io.api;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CloudFunctions {

    String getCustomTokenURL = "https://us-central1-belive-fab1f.cloudfunctions.net/getCustomToken/";

    @GET("getCustomToken")
    Call<ResponseBody> getCustomToken(@Query("access_token") String accessToken);
}
