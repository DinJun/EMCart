package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class ReloadActivity extends AppCompatActivity {
    private EditText metReloadPassword, metReloadAmount;
    private Button mBtnReload;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();
        final String userEmail = user.getEmail();

        metReloadAmount = (EditText)findViewById(R.id.etReloadAmount);
        metReloadPassword = (EditText)findViewById(R.id.etReloadPassword);
        mBtnReload = (Button)findViewById(R.id.btnReload);

        final DatabaseReference userReloadRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);

        mBtnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String reloadAmountET = metReloadAmount.getText().toString();
                String reloadPasswordET = metReloadPassword.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, reloadPasswordET);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            userReloadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String acctBalance = snapshot.child("balance").getValue().toString();
                                        double dAcctBalance = Double.parseDouble(acctBalance);
                                        dAcctBalance = dAcctBalance + Double.parseDouble(reloadAmountET);

                                        HashMap<String, Object> userReload = new HashMap<>();
                                        userReload.put("balance", String.valueOf(dAcctBalance));
                                        userReloadRef.updateChildren(userReload);

                                        Toast.makeText(ReloadActivity.this, "Reload successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ReloadActivity.this, MainActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(ReloadActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}