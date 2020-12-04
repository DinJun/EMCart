package student.inti.com.emcart;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mtvCartItemName, mtvCartItemPrice, mtvCartItemQuantity, mtvCartItemTotalPrice;
    public ItemOnClickListener mItemOnClickListener;
    public Button mBtnRemoveCart;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        mtvCartItemName = (TextView)itemView.findViewById(R.id.tvCartItemName);
        mtvCartItemPrice = (TextView)itemView.findViewById(R.id.tvCartItemPrice);
        mtvCartItemQuantity = (TextView)itemView.findViewById(R.id.tvCartItemQuantity);
        mtvCartItemTotalPrice = (TextView)itemView.findViewById(R.id.tvCartItemTotalPrice);
        mBtnRemoveCart = (Button)itemView.findViewById(R.id.btnRemoveCart);
    }

    @Override
    public void onClick(View view) {
        mItemOnClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemOnClickListener(ItemOnClickListener listener){
        this.mItemOnClickListener = listener;
    }
}
