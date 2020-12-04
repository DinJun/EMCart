package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText metForgotPasswordEmail;
    private CardView mBtnForgotPassword;
    private ProgressBar mforgotPasswordProgressBar;
    private FirebaseAuth forgotFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        metForgotPasswordEmail = (EditText)findViewById(R.id.etForgotPasswordEmail);
        mBtnForgotPassword = (CardView)findViewById(R.id.btnForgotPassword);
        mforgotPasswordProgressBar = (ProgressBar)findViewById(R.id.forgotPasswordProgressBar);

        forgotFirebaseAuth = FirebaseAuth.getInstance();

        mBtnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(metForgotPasswordEmail.getText().toString())){
                    metForgotPasswordEmail.setError("Field is required!");
                }
                else{
                    mforgotPasswordProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    forgotFirebaseAuth.sendPasswordResetEmail(metForgotPasswordEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mforgotPasswordProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset sent to your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            }
                            else{
                                Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}