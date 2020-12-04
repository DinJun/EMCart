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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShipmentActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_shipment);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        final String userID = user.getUid();

        final DatabaseReference shipmentRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);

        FirebaseRecyclerOptions<Shipping> shippingList = new FirebaseRecyclerOptions.Builder<Shipping>().setQuery(shipmentRef, Shipping.class).build();

        FirebaseRecyclerAdapter<Shipping, ShipmentViewHolder> adapter = new FirebaseRecyclerAdapter<Shipping, ShipmentViewHolder>(shippingList) {
            @Override
            protected void onBindViewHolder(@NonNull ShipmentViewHolder holder, int position, @NonNull Shipping model) {
                final String dateTimeIntent = model.getDateOrdered() + " " + model.getTimeOrdered();
                holder.mtvShippingName.setText(model.getFullname());
                holder.mtvShippingPhone.setText(model.getPhonenumber());
                holder.mtvShippingDateTime.setText(model.getDateOrdered() + " " + model.getTimeOrdered());
                holder.mtvShippingAddress.setText(model.getAddress());
                holder.mtvShippingStatus.setText(model.getStatus());
                holder.mtvShippingTotalPrice.setText(String.format("%.2f", Double.parseDouble(model.getOrderTotalPrice())));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShipmentActivity.this, ShipmentDetailActivity.class);
                        intent.putExtra("dateTimeID", dateTimeIntent);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ShipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipment_order, parent, false);
                ShipmentViewHolder holder = new ShipmentViewHolder(view);
                return holder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}