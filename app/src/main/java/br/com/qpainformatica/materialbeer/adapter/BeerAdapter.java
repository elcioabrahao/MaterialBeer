package br.com.qpainformatica.materialbeer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

import br.com.qpainformatica.materialbeer.MainActivity;
import br.com.qpainformatica.materialbeer.R;
import br.com.qpainformatica.materialbeer.entity.Beer;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ViewHolder> {

    private List<Beer> beers;
    private int rowLayout;
    private MainActivity mAct;

    public BeerAdapter(List<Beer> beers, int rowLayout, MainActivity act) {
        this.beers = beers;
        this.rowLayout = rowLayout;
        this.mAct = act;
    }


    public void clearBeers() {
        int size = this.beers.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                beers.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addBeers(List<Beer> beers) {
        this.beers.addAll(beers);
        this.notifyItemRangeInserted(0, beers.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Beer beer = beers.get(i);
        viewHolder.name.setText(beer.getNome());
        viewHolder.image.setImageDrawable(beer.getIcon());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.animateActivity(beer, viewHolder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beers == null ? 0 : beers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.countryName);
            image = (ImageView) itemView.findViewById(R.id.countryImage);
        }

    }
}
