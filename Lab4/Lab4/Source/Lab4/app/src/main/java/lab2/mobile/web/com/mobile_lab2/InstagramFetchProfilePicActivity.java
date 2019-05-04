package lab2.mobile.web.com.mobile_lab2;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gangi on 7/27/2018.
 */

public class InstagramFetchProfilePicActivity extends AppCompatActivity {

    // Fetching from Layout
    EditText instaText;
    ImageView instaImageView;
    TextView userNameText;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_instagram);

        // To Override running API's in Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        instaText = findViewById(R.id.usernameText);
        instaImageView = findViewById(R.id.instaPic);
        userNameText = findViewById(R.id.instaUserName);
    }

    // On Click of Button
    public void fetchInstaPicture(View view) {

        // Making Fields Empty First
        userNameText.setText("");
        instaImageView.setImageBitmap(null);

        String userText = instaText.getText().toString();
        // Checking If username is present or not
        if (StringUtils.isEmpty(userText)) {
            userNameText.setText("Username text is Mandatory !!");
            // Throw a Toast saying username is mandatory
            Toast.makeText(InstagramFetchProfilePicActivity.this,
                    "Username text is Mandatory !!", Toast.LENGTH_LONG).show();
        } else {
            // Appending Username to API URL
            String API_URL = "https://apinsta.herokuapp.com/u/"+userText;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                // Fetching Response from API URL
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // If ResponseCode is NOT 200, throw a TOAST & text saying User not present.
                if(urlConnection.getResponseCode() != 200){
                    userNameText.setText("No such user '" + userText + "' exists to get Profile Pic!!!");
                    Toast.makeText(InstagramFetchProfilePicActivity.this,
                            "No such user '" + userText + "' exists to get Profile Pic!!!",
                            Toast.LENGTH_LONG).show();
                }else {
                    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        // Appending the Line to StringBuilder
                        stringBuilder.append(line);
                    }
                    final JSONObject jsonResult;
                    final String result = stringBuilder.toString();
                    System.out.println(result);
                    if (StringUtils.isNotEmpty(result) && !result.equals("{}")) {
                        try {
                            jsonResult = new JSONObject(result);
                            // Fetching Profile Pic Hd from JSON
                            JSONObject jsonObject = jsonResult.getJSONObject("graphql").getJSONObject("user");
                            String imageHdURL = jsonObject.getString("profile_pic_url_hd");
                            System.out.println(imageHdURL);
                            // Gliding Hd URL into Image View
                            Glide.with(getApplicationContext()).load(imageHdURL).into(instaImageView);
                            // Fetching Name & Displaying it in UI
                            String userDetText = "Name : "+jsonObject.getString("full_name")
                                    +"<br/> Followers Count : "+jsonObject.getJSONObject("edge_followed_by").getInt("count")
                                    +"<br/> Following Count : "+jsonObject.getJSONObject("edge_follow").getInt("count");
                            userNameText.setText(Html.fromHtml(userDetText));
                        } catch (JSONException e) {
                            Log.e("Error : ", e.getMessage());
                            Toast.makeText(InstagramFetchProfilePicActivity.this,
                                    "JSONException Occured !!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        userNameText.setText("No such user '" + userText + "' exists to get Profile Pic!!!");
                        Toast.makeText(InstagramFetchProfilePicActivity.this,
                                "No such user '" + userText + "' exists to get Profile Pic!!!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(InstagramFetchProfilePicActivity.this,
                        "Exception Occured while fetching Insta pic : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
