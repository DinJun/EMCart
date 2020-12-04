package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ItemDetailActivity extends AppCompatActivity {
    private TextView mtvProductNameDetail, mtvProductDescDetail, mtvProductPriceDetail, mtvProductQuantityDetail, mtvProductQuantityDetailCount;
    private CardView mMinusBtn, mPlusBtn;
    private ImageView mivProductImageDetail;
    private String itemid = "";
    private int amount = 1;
    private Button mBtnAddCart;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        itemid = getIntent().getStringExtra("itemID");
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        mivProductImageDetail = (ImageView)findViewById(R.id.ivProductImageDetail);
        mtvProductNameDetail = (TextView)findViewById(R.id.tvProductNameDetail);
        mtvProductDescDetail = (TextView)findViewById(R.id.tvProductDescDetail);
        mtvProductPriceDetail = (TextView)findViewById(R.id.tvProductPriceDetail);
        mtvProductQuantityDetail = (TextView)findViewById(R.id.tvProductQuantityDetail);
        mtvProductQuantityDetailCount = (TextView)findViewById(R.id.tvProductQuantityDetailCount);
        mMinusBtn = (CardView)findViewById(R.id.minusBtn);
        mPlusBtn = (CardView)findViewById(R.id.plusBtn);
        mBtnAddCart = (Button)findViewById(R.id.btnAddCart);

        getItemDetail(itemid);

        mMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mtvProductQuantityDetailCount.getText().toString());
                if(amount == 1){
                    Toast.makeText(ItemDetailActivity.this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
                else{
                    amount = amount - 1;
                    mtvProductQuantityDetailCount.setText(amount + "");
                }
            }
        });

        mPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mtvProductQuantityDetailCount.getText().toString());
                if(amount == Integer.parseInt(mtvProductQuantityDetail.getText().toString())){
                    Toast.makeText(ItemDetailActivity.this, "Quantity at limit", Toast.LENGTH_SHORT).show();
                }
                else{
                    amount = amount + 1;
                    mtvProductQuantityDetailCount.setText(amount + "");
                }
            }
        });

        mBtnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference shopCart = FirebaseDatabase.getInstance().getReference().child("Cart");
                String cartItemName = mtvProductNameDetail.getText().toString();
                String cartItemQuantity = mtvProductQuantityDetailCount.getText().toString();
                String cartItemPrice = mtvProductPriceDetail.getText().toString().substring(2);
                double cartItemTotalPrice = Double.parseDouble(cartItemPrice) * Integer.parseInt(cartItemQuantity);
                String sCartItemTotalPrice = cartItemTotalPrice + "";
                HashMap<String, Object> cartHashMap = new HashMap<>();
                cartHashMap.put("itemID", itemid);
                cartHashMap.put("itemName", cartItemName);
                cartHashMap.put("quantity", cartItemQuantity);
                cartHashMap.put("price", cartItemPrice);
                cartHashMap.put("totalPrice", sCartItemTotalPrice);

                shopCart.child(userID).child("ItemList").child(itemid).updateChildren(cartHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ItemDetailActivity.this, "Item added to shopping cart", Toast.LENGTH_SHORT).show();
                            finish();
                            //Intent intent = new Intent(ItemDetailActivity.this, MainActivity.class);
                            //startActivity(intent);
                        }
                    }
                });
            }
        });
        
    }

    private void getItemDetail(String itemId){
        DatabaseReference itemDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Items");

        itemDatabaseRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Item items = snapshot.getValue(Item.class);
                    double itemPriceDetail = Double.parseDouble(items.getPrice());
                    /*mStorageReference = FirebaseStorage.getInstance().getReference().child("image/" + items.getItemImage());
                    if(!ItemDetailActivity.this.isFinishing()){
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(ItemDetailActivity.this).load(uri.toString()).into(mivProductImageDetail);
                            }
                        });
                    }*/
                    if(!ItemDetailActivity.this.isFinishing()){
                        Glide.with(ItemDetailActivity.this).load(items.getItemImage()).into(mivProductImageDetail);
                    }
                    mtvProductNameDetail.setText(items.getItemName());
                    mtvProductDescDetail.setText(items.getDescription());
                    mtvProductPriceDetail.setText(String.format("RM%.2f", itemPriceDetail));
                    mtvProductQuantityDetail.setText(items.getQuantity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}