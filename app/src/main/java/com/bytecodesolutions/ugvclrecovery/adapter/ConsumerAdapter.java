package com.bytecodesolutions.ugvclrecovery.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bytecodesolutions.ugvclrecovery.R;
import com.bytecodesolutions.ugvclrecovery.activities.ShowConsumerActivity;
import com.bytecodesolutions.ugvclrecovery.model.Consumer;

import java.util.List;

public class ConsumerAdapter extends RecyclerView.Adapter<ConsumerAdapter.ViewHolder>{
   public interface OnListItemClickListener{
       public void onListItemClick(int id);
   }
    List<Consumer> consumerList;
    Context context;
    OnListItemClickListener onListItemClickListener;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {



        TextView num,name,tarif,amount;
        View view;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            this.view=view;
            num=view.findViewById(R.id.list_con_num);
            name=view.findViewById(R.id.list_con_name);
            tarif=view.findViewById(R.id.list_con_tarif);
            amount=view.findViewById(R.id.list_con_amount);

        }
        public TextView getNum() {
            return num;
        }

        public TextView getName() {
            return name;
        }

        public TextView getTarif() {
            return tarif;
        }

        public TextView getAmount() {
            return amount;
        }
        public View getView(){
            return view;
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param consumerList String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public ConsumerAdapter(List<Consumer> consumerList) {
        this.consumerList=consumerList;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        this.context=viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_consumer, viewGroup, false);

        return new ViewHolder(view);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        int pos=position;
        viewHolder.getNum().setText(consumerList.get(position).getNum());
        viewHolder.getName().setText(consumerList.get(position).getName());
        viewHolder.getTarif().setText(consumerList.get(position).getTarif());
        viewHolder.getAmount().setText("RS. "+consumerList.get(position).getAmount());
        viewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               onListItemClickListener.onListItemClick(consumerList.get(pos).getId());
            }
        });
        Log.d("adapter",consumerList.get(position).getAddress1());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
       // return localDataSet.length;
        return consumerList.size();
    }
    public void setConsumerList(List<Consumer> consumerList){
        this.consumerList=consumerList;
        notifyDataSetChanged();
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener){
        this.onListItemClickListener=onListItemClickListener;
    }
}
