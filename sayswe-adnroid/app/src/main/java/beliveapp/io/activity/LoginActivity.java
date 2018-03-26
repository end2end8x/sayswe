package beliveapp.io.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import beliveapp.io.MainActivity;
import beliveapp.io.R;
import beliveapp.io.api.CloudFunctions;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends BaseActivity {

    @BindView(R.id.smsLogin)
    Button smsLogin;

    private static final String TAG = "FacebookLogin";

    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    public static int ACCOUNT_KIT_REQUEST_CODE = 99;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private CloudFunctions mCloudFunctions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "User signed in: " + user.getUid(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    final com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
                    if (accessToken != null) {
                        getCustomToken(accessToken);
                    } else {
//                        Toast.makeText(LoginActivity.this, "User NOT signed in AccountKit: ",
//                                Toast.LENGTH_SHORT).show();
//                        phoneLogin(smsLogin);
                    }
                }
            }
        };

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CloudFunctions.getCustomTokenURL)
                .build();
        mCloudFunctions = retrofit.create(CloudFunctions.class);

        smsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLogin(smsLogin);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == ACCOUNT_KIT_REQUEST_CODE) {
            handleAccountKitLoginResult(resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    hideProgressDialog();
                }
            });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            this.finish();
        } else {

        }
    }

    public void phoneLogin(View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setDefaultCountryCode("VN");
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, ACCOUNT_KIT_REQUEST_CODE);
    }

    private void handleAccountKitLoginResult(final int resultCode, final Intent data) {
        Log.d(TAG, "handleAccountKitLoginResult: " + resultCode);

        final AccountKitLoginResult loginResult =
                data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

        if (loginResult.getError() != null) {
            final String toastMessage = loginResult.getError().getErrorType().getMessage();
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        } else if (loginResult.wasCancelled() || resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Login cancelled");
            finish();
        } else {
            if (loginResult.getAccessToken() != null) {
                Log.d(TAG, "We have logged with FB Account Kit. ID: " +
                        loginResult.getAccessToken().getAccountId());
//                Toast.makeText(this, "We have logged with FB Account Kit. ID: " +
//                        loginResult.getAccessToken().getAccountId(), Toast.LENGTH_LONG).show();
                getCustomToken(loginResult.getAccessToken());
            } else {
                Log.wtf(TAG, "It should not have been happened");
            }
        }
    }

    private void getCustomToken(final com.facebook.accountkit.AccessToken accessToken) {
        Log.d(TAG, "Getting custom token for Account Kit access token: " + accessToken.getToken());
        mCloudFunctions.getCustomToken(accessToken.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        final String customToken = response.body().string();
                        Log.d(TAG, "Custom token: " + customToken);
                        signInWithAccountKitToken(customToken);
                    } else {
                        Log.e(TAG, response.errorBody().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable e) {
                Log.e(TAG, "Request getCustomToken failed", e);
            }
        });
    }

    private void signInWithAccountKitToken(String customToken) {
        mAuth.signInWithCustomToken(customToken)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "getCustomToken:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getCustomToken", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "Authentication success " + user.getUid(),
                                    Toast.LENGTH_LONG).show();

                            updateUI(user);

                        }

                    }
                });
    }
}
