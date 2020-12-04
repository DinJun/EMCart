package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    private Button mBtnChangePassword;
    private EditText metCurrentPassword, metNewPassword, metConfirmPassword;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mBtnChangePassword = (Button)findViewById(R.id.btnChangePassword);
        metCurrentPassword = (EditText)findViewById(R.id.etCurrentPassword);
        metNewPassword = (EditText)findViewById(R.id.etNewPassword);
        metConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();
        final String userEmail = user.getEmail();

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changePasswordCurrentPassword = metCurrentPassword.getText().toString();
                String changePasswordNewPassword = metNewPassword.getText().toString();
                String changePasswordNewConfirmPassword = metConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(changePasswordCurrentPassword)){
                    metCurrentPassword.setError("Field is required!");
                }
                else if(TextUtils.isEmpty(changePasswordNewPassword)){
                    metNewPassword.setError("Field is required!");
                }
                else if(TextUtils.isEmpty(changePasswordNewConfirmPassword)){
                    metConfirmPassword.setError("Field is required!");
                }
                else if(changePasswordNewPassword.length() < 6){
                    metNewPassword.setError("Password required to be 6 or more characters!");
                }
                else if(!changePasswordNewPassword.equals(changePasswordNewConfirmPassword)){
                    metConfirmPassword.setError("New passwords do not match!");
                }
                else{
                    final String passwordToBeChanged = changePasswordNewConfirmPassword;
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, changePasswordCurrentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(passwordToBeChanged).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password change failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ChangePasswordActivity.this, "Wrong current password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}