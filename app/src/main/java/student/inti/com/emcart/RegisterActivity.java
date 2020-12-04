package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText mRegisterFullname;
    private EditText mRegisterEmail;
    private EditText mRegisterPassword;
    private EditText mRegisterConfirmPassword;
    private CardView mRegisterBtn;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabase;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRegisterFullname = (EditText)findViewById(R.id.register_fullname);
        mRegisterEmail = (EditText)findViewById(R.id.register_email);
        mRegisterPassword = (EditText)findViewById(R.id.register_password);
        mRegisterConfirmPassword = (EditText)findViewById(R.id.register_confirmpassword);

        mDatabase = FirebaseDatabase.getInstance().getReference("User");


        mRegisterBtn = (CardView)findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = mRegisterFullname.getText().toString();
                String email = mRegisterEmail.getText().toString();
                String password = mRegisterPassword.getText().toString();
                String confirmPassword = mRegisterConfirmPassword.getText().toString();
                String balance = "0";
                user = new User(fullname, email, balance);

                if(TextUtils.isEmpty(fullname)){
                    mRegisterFullname.setError("Please enter your full name!");
                    mRegisterFullname.requestFocus();
                }
                else if(TextUtils.isEmpty(email)){
                    mRegisterEmail.setError("Please enter your email!");
                    mRegisterEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    mRegisterPassword.setError("Please enter your password!");
                    mRegisterPassword.requestFocus();
                }
                else if(password.length() < 6){
                    mRegisterPassword.setError("Password required to be 6 or more characters!");
                    mRegisterPassword.requestFocus();
                }
                else if(TextUtils.isEmpty(confirmPassword)){
                    mRegisterConfirmPassword.setError("Please enter your password again!");
                    mRegisterConfirmPassword.requestFocus();
                }
                else if(!password.equals(confirmPassword)){
                    mRegisterConfirmPassword.setError("Passwords entered do not match!");
                    mRegisterConfirmPassword.requestFocus();
                }
                else{
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String userID = mFirebaseAuth.getCurrentUser().getUid();
                                mDatabase.child(userID).setValue(user);
                                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Registration unsuccessful, please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}