package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShipmentDetailActivity extends AppCompatActivity {
    private TextView mtvshipmentDetailName, mtvshipmentDetailPhone, mtvshipmentDetailDate, mtvshipmentDetailTime, mtvshipmentDetailTotalPrice, mtvshipmentDetailAddress, mtvshipmentDetailStatus;
    private Button mBtnConfirmReceived;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_detail);

        mtvshipmentDetailName = (TextView)findViewById(R.id.tvshipmentDetailName);
        mtvshipmentDetailPhone = (TextView)findViewById(R.id.tvshipmentDetailPhone);
        mtvshipmentDetailDate = (TextView)findViewById(R.id.tvshipmentDetailDate);
        mtvshipmentDetailTime = (TextView)findViewById(R.id.tvshipmentDetailTime);
        mtvshipmentDetailTotalPrice = (TextView)findViewById(R.id.tvshipmentDetailTotalPrice);
        mtvshipmentDetailAddress = (TextView)findViewById(R.id.tvshipmentDetailAddress);
        mtvshipmentDetailStatus = (TextView)findViewById(R.id.tvshipmentDetailStatus);
        mBtnConfirmReceived = (Button)findViewById(R.id.btnConfirmReceived);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_shipping_items);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String dtIntent = getIntent().getStringExtra("dateTimeID");
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        final DatabaseReference orderReceivedRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID).child(dtIntent);

        orderReceivedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("status").getValue().toString().equals("received")){
                    mBtnConfirmReceived.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getShippingDetail(dtIntent, userID);

        mBtnConfirmReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirmReceivedAlert = new AlertDialog.Builder(ShipmentDetailActivity.this);
                confirmReceivedAlert.setTitle("Order received?");
                confirmReceivedAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> itemStatusMap = new HashMap<>();
                        itemStatusMap.put("status", "received");
                        orderReceivedRef.updateChildren(itemStatusMap);

                        startActivity(new Intent(ShipmentDetailActivity.this, MainActivity.class));
                        Toast.makeText(ShipmentDetailActivity.this, "Order received", Toast.LENGTH_SHORT).show();
                    }
                });
                confirmReceivedAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                confirmReceivedAlert.show();
            }
        });

        final DatabaseReference shipmentDetailRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID).child(dtIntent).child("itemProducts");
        FirebaseRecyclerOptions<Cart> shippingDetailItemList = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(shipmentDetailRef, Cart.class).build();

        FirebaseRecyclerAdapter<Cart, ShipmentDetailViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, ShipmentDetailViewHolder>(shippingDetailItemList) {
            @Override
            protected void onBindViewHolder(@NonNull ShipmentDetailViewHolder holder, int position, @NonNull Cart model) {
                double eachShippingItemPriceCart = Double.parseDouble(model.getPrice());
                double eachShippingItemTotalPriceCart = Double.parseDouble(model.getTotalPrice());

                holder.mtvShippingDetailItemName.setText(model.getItemName());
                holder.mtvShippingDetailItemPrice.setText(String.format("%.2f", eachShippingItemPriceCart));
                holder.mtvShippingDetailItemQuantity.setText(model.getQuantity());
                holder.mtvShippingDetailItemTotalPrice.setText(String.format("%.2f", eachShippingItemTotalPriceCart));
            }

            @NonNull
            @Override
            public ShipmentDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipment_detail_item, parent, false);
                ShipmentDetailViewHolder holder = new ShipmentDetailViewHolder(view);
                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void getShippingDetail(String dateTimeID, String userid){
        DatabaseReference ordersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userid);

        ordersDatabaseRef.child(dateTimeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Shipping ship = snapshot.getValue(Shipping.class);
                    mtvshipmentDetailName.setText(ship.getFullname());
                    mtvshipmentDetailPhone.setText(ship.getPhonenumber());
                    mtvshipmentDetailDate.setText(ship.getDateOrdered());
                    mtvshipmentDetailTime.setText(ship.getTimeOrdered());
                    mtvshipmentDetailTotalPrice.setText(String.format("%.2f", Double.parseDouble(ship.getOrderTotalPrice())));
                    mtvshipmentDetailAddress.setText(ship.getAddress());
                    mtvshipmentDetailStatus.setText(ship.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}