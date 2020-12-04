package student.inti.com.emcart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShipmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mtvShippingName, mtvShippingPhone, mtvShippingDateTime, mtvShippingAddress, mtvShippingTotalPrice, mtvShippingStatus;
    public ItemOnClickListener mItemOnClickListener;

    public ShipmentViewHolder(@NonNull View itemView) {
        super(itemView);

        mtvShippingName = (TextView)itemView.findViewById(R.id.tvShippingName);
        mtvShippingPhone = (TextView)itemView.findViewById(R.id.tvShippingPhone);
        mtvShippingDateTime = (TextView)itemView.findViewById(R.id.tvShippingDateTime);
        mtvShippingAddress = (TextView)itemView.findViewById(R.id.tvShippingAddress);
        mtvShippingTotalPrice = (TextView)itemView.findViewById(R.id.tvShippingTotalPrice);
        mtvShippingStatus = (TextView)itemView.findViewById(R.id.tvShippingStatus);
    }

    @Override
    public void onClick(View view) {
        mItemOnClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemOnClickListener(ItemOnClickListener listener){
        this.mItemOnClickListener = listener;
    }
}
