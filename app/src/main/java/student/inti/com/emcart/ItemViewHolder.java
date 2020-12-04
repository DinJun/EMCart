package student.inti.com.emcart;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView mivProductImage;
    public TextView mtvProductName, mtvProductPrice;

    public ItemOnClickListener mItemOnClickListener;


    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        mivProductImage = (ImageView)itemView.findViewById(R.id.ivProductImage);
        mtvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
        mtvProductPrice = (TextView)itemView.findViewById(R.id.tvProductPrice);
    }

    public void setItemOnClickListener(ItemOnClickListener listener){
        this.mItemOnClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        mItemOnClickListener.onClick(view, getAdapterPosition(), false);
    }
}
