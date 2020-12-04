package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private CardView mLoginBtn;
    private TextView mTVRegister, mTVForgotPassword;
    private ProgressBar mLoginProgressBar;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        mLoginProgressBar = (ProgressBar)findViewById(R.id.loginProgressBar);
        mLoginEmail = (EditText)findViewById(R.id.login_email);
        mLoginPassword = (EditText)findViewById(R.id.login_password);
        mLoginBtn = (CardView)findViewById(R.id.loginBtn);
        mTVRegister = (TextView)findViewById(R.id.tVRegister);
        mTVForgotPassword = (TextView)findViewById(R.id.tVForgotPassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                String loginEmail = mLoginEmail.getText().toString();
                String loginPassword = mLoginPassword.getText().toString();

                if(loginEmail.isEmpty()){
                    mLoginEmail.setError("Please enter your email!");
                    mLoginEmail.requestFocus();
                }
                else if(loginPassword.isEmpty()){
                    mLoginPassword.setError("Please enter your password!");
                    mLoginPassword.requestFocus();
                }
                else{
                    mFirebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                mLoginProgressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Login failed, please try again!", Toast.LENGTH_SHORT).show();
                                mLoginProgressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
            }
        });

        mTVRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mTVForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}