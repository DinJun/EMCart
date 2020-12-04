package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBtnProceed;
    private TextView mtvCartTotalPrice;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    public static double cartTotalPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_cart);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBtnProceed = (Button)findViewById(R.id.btnProceed);
        mtvCartTotalPrice = (TextView)findViewById(R.id.tvCartTotalPrice);

        mBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, ConfirmCartActivity.class);
                intent.putExtra("confirmTotalPrice", mtvCartTotalPrice.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        final DatabaseReference cartListDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Cart");

        cartListDatabaseRef.child(userID).child("ItemList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double itemsCartTotalPrice = 0.0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    itemsCartTotalPrice = itemsCartTotalPrice + Double.parseDouble(String.valueOf(dataSnapshot.child("totalPrice").getValue()));
                }
                mtvCartTotalPrice.setText(String.format("%.2f", itemsCartTotalPrice));
                if(itemsCartTotalPrice == 0.0){
                    mBtnProceed.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Cart> cartList = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListDatabaseRef.child(userID).child("ItemList"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(cartList) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                double eachItemPriceCart = Double.parseDouble(model.getPrice());
                double eachItemTotalPriceCart = Double.parseDouble(model.getTotalPrice());

                holder.mtvCartItemName.setText(model.getItemName());
                holder.mtvCartItemPrice.setText(String.format("%.2f", eachItemPriceCart));
                holder.mtvCartItemQuantity.setText(model.getQuantity());
                holder.mtvCartItemTotalPrice.setText(String.format("%.2f", eachItemTotalPriceCart));

                holder.mBtnRemoveCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartListDatabaseRef.child(userID).child("ItemList").child(model.getItemID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(CartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(CartActivity.this, "Item removal error, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartActivity.this, ItemDetailActivity.class);
                        intent.putExtra("itemID", model.getItemID());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}