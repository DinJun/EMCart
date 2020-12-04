package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.FirebaseRecyclerViewAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mtvNavBalance, mtvNavName, mtvNavEmail;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    DatabaseReference mDatabase;
    RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mtvNavName = (TextView)header.findViewById(R.id.tvNavName);
        mtvNavEmail = (TextView)header.findViewById(R.id.tvNavEmail);
        mtvNavBalance = (TextView)header.findViewById(R.id.tvNavBalance);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_home);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart(){
        super.onStart();

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(mFirebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("User");
        reff.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User users = snapshot.getValue(User.class);
                mtvNavBalance.setText(String.format("RM%.2f", Double.parseDouble(users.getBalance())));
                mtvNavName.setText(users.getFullname());
                mtvNavEmail.setText(users.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Item> items = new FirebaseRecyclerOptions.Builder<Item>().setQuery(mDatabase.child("Items"), Item.class).build();

        FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(items) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Item model) {
                double itemPriceDetail = Double.parseDouble(model.getPrice());
                String mainActivityItemName = model.getItemName();

                Glide.with(MainActivity.this).load(model.getItemImage()).into(holder.mivProductImage);
                if(mainActivityItemName.length() > 12){
                    holder.mtvProductName.setText(mainActivityItemName.substring(0,12) + "...");
                }
                else{
                    holder.mtvProductName.setText(mainActivityItemName);
                }
                holder.mtvProductPrice.setText(String.format("RM%.2f", itemPriceDetail));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                        intent.putExtra("itemID", model.getItemID());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                ItemViewHolder holder = new ItemViewHolder(view);
                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int itemIDMenu = item.getItemId();

        if(itemIDMenu == R.id.menu_home){
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
        else if(itemIDMenu == R.id.menu_profile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        else if(itemIDMenu == R.id.menu_reload){
            startActivity(new Intent(MainActivity.this, ReloadActivity.class));
        }
        else if(itemIDMenu == R.id.menu_shopping_cart){
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        else if(itemIDMenu == R.id.menu_shipping){
            startActivity(new Intent(MainActivity.this, ShipmentActivity.class));
        }
        else if(itemIDMenu == R.id.menu_additem){
            startActivity(new Intent(MainActivity.this, UploadItemActivity.class));
        }
        else if(itemIDMenu == R.id.menu_logout){
            mFirebaseAuth.signOut();
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        return true;
    }


}