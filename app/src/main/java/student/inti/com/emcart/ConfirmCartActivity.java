package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmCartActivity extends AppCompatActivity {
    private Button mBtnCheckOut;
    private EditText metCartConfirmFullName, metCartConfirmPhoneNumber, metCartConfirmAddress;
    private TextView mtvConfirmCartTotalPrice;
    private String intentTotalPrice;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    public boolean enoughBalance = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cart);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        intentTotalPrice = getIntent().getStringExtra("confirmTotalPrice");

        mBtnCheckOut = (Button)findViewById(R.id.btnCheckOut);
        mtvConfirmCartTotalPrice = (TextView)findViewById(R.id.tvConfirmCartTotalPrice);
        metCartConfirmFullName = (EditText)findViewById(R.id.etCartConfirmFullName);
        metCartConfirmPhoneNumber = (EditText)findViewById(R.id.etCartConfirmPhoneNumber);
        metCartConfirmAddress = (EditText)findViewById(R.id.etCartConfirmAddress);
        mtvConfirmCartTotalPrice.setText(String.format("%.2f", Double.parseDouble(intentTotalPrice)));

        final DatabaseReference userBalanceRef = FirebaseDatabase.getInstance().getReference().child("User").child(userID);

        userBalanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String walletBalance = snapshot.child("balance").getValue().toString();
                    if(Double.parseDouble(walletBalance) >= Double.parseDouble(intentTotalPrice)){
                        enoughBalance = true;
                    }
                    else{
                        enoughBalance = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mBtnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(metCartConfirmFullName.getText().toString())){
                    metCartConfirmFullName.setError("Field is required!");
                }
                else if(TextUtils.isEmpty(metCartConfirmPhoneNumber.getText().toString())){
                    metCartConfirmPhoneNumber.setError("Field is required!");
                }
                else if(TextUtils.isEmpty(metCartConfirmAddress.getText().toString())){
                    metCartConfirmAddress.setError("Field is required!");
                }
                else if(enoughBalance == false){
                    Toast.makeText(ConfirmCartActivity.this, "Not enough wallet balance, please reload", Toast.LENGTH_SHORT).show();
                }
                else{
                    final String todayDate, todayTime;

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                    todayDate = date.format(calendar.getTime());
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                    todayTime = time.format(calendar.getTime());
                    final String todayDateTime = todayDate + " " + todayTime;
                    DatabaseReference confirmOrderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);

                    HashMap<String, Object> orderMap = new HashMap<>();
                    orderMap.put("fullname", metCartConfirmFullName.getText().toString());
                    orderMap.put("phonenumber", metCartConfirmPhoneNumber.getText().toString());
                    orderMap.put("address", metCartConfirmAddress.getText().toString());
                    orderMap.put("orderTotalPrice", mtvConfirmCartTotalPrice.getText().toString());
                    orderMap.put("dateOrdered", todayDate);
                    orderMap.put("timeOrdered", todayTime);
                    orderMap.put("status", "shipping");
                    final double totalPriceNeeded =  Double.parseDouble(mtvConfirmCartTotalPrice.getText().toString());
                    confirmOrderRef.child(todayDateTime).updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                final DatabaseReference copyCartListRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userID).child("ItemList");
                                final DatabaseReference copyDestinationRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID).child(todayDateTime).child("itemProducts");

                                copyCartListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            String copyItemID = String.valueOf(dataSnapshot.child("itemID").getValue());
                                            HashMap<String, Object> copyHashMap = new HashMap<>();
                                            copyHashMap.put("itemID", copyItemID);
                                            copyHashMap.put("itemName", dataSnapshot.child("itemName").getValue());
                                            copyHashMap.put("quantity", dataSnapshot.child("quantity").getValue());
                                            copyHashMap.put("price", dataSnapshot.child("price").getValue());
                                            copyHashMap.put("totalPrice", dataSnapshot.child("totalPrice").getValue());

                                            final int finalQuantity = Integer.parseInt(dataSnapshot.child("quantity").getValue().toString());

                                            final DatabaseReference deductItemQuantityRef = FirebaseDatabase.getInstance().getReference().child("Items").child(copyItemID);
                                            deductItemQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int originalQuantity = Integer.parseInt(snapshot.child("quantity").getValue().toString());
                                                    int afterDQuantity = originalQuantity - finalQuantity;
                                                    HashMap<String, Object> quantityChangeHashMap = new HashMap<>();
                                                    quantityChangeHashMap.put("quantity", String.valueOf(afterDQuantity));
                                                    deductItemQuantityRef.updateChildren(quantityChangeHashMap);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            copyDestinationRef.child(copyItemID).updateChildren(copyHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                }
                                            });
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("Cart").child(userID).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                userBalanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String walletBalance = snapshot.child("balance").getValue().toString();
                                            double deductedBalance = Double.parseDouble(walletBalance) - totalPriceNeeded;
                                            HashMap<String, Object> usersBalanceMap = new HashMap<>();
                                            usersBalanceMap.put("balance", String.valueOf(deductedBalance));
                                            userBalanceRef.updateChildren(usersBalanceMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                Toast.makeText(ConfirmCartActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ConfirmCartActivity.this, MainActivity.class));
                            }
                        }
                    });
                }
            }
        });
    }
}