package student.inti.com.emcart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShipmentDetailViewHolder extends RecyclerView.ViewHolder {
    public TextView mtvShippingDetailItemName, mtvShippingDetailItemPrice, mtvShippingDetailItemQuantity, mtvShippingDetailItemTotalPrice;

    public ShipmentDetailViewHolder(@NonNull View itemView) {
        super(itemView);

        mtvShippingDetailItemName = (TextView)itemView.findViewById(R.id.tvShippingDetailItemName);
        mtvShippingDetailItemPrice = (TextView)itemView.findViewById(R.id.tvShippingDetailItemPrice);
        mtvShippingDetailItemQuantity = (TextView)itemView.findViewById(R.id.tvShippingDetailItemQuantity);
        mtvShippingDetailItemTotalPrice = (TextView)itemView.findViewById(R.id.tvShippingDetailItemTotalPrice);
    }
}
