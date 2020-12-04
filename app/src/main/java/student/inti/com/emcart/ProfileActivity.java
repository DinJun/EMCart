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
import android.widget.TextView;
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

public class ProfileActivity extends AppCompatActivity {
    private Button mBtnProfileChangePassword;
    private EditText metProfileFullname;
    private TextView mProfileCloseBtnToolbar, mProfileUpdateBtnToolbar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        metProfileFullname = (EditText)findViewById(R.id.etProfileFullname);
        mBtnProfileChangePassword = (Button)findViewById(R.id.btnProfileChangePassword);
        mProfileCloseBtnToolbar = (TextView)findViewById(R.id.profileCloseBtnToolbar);
        mProfileUpdateBtnToolbar = (TextView)findViewById(R.id.profileUpdateBtnToolbar);

        DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);

        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String profileFullName = snapshot.child("fullname").getValue().toString();
                    metProfileFullname.setText(profileFullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mProfileCloseBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProfileUpdateBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(metProfileFullname.getText().toString())){
                    metProfileFullname.setError("Field is empty!");
                    metProfileFullname.requestFocus();
                }
                else{
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");

                    HashMap<String, Object> usersMap = new HashMap<>();
                    usersMap.put("fullname", metProfileFullname.getText().toString());
                    userRef.child(userID).updateChildren(usersMap);

                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    //finish();
                }

            }
        });

        mBtnProfileChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });
    }
}