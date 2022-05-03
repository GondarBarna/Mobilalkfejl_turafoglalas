package hu.mobilalk.turafoglalas.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.MessageFormat;
import java.util.ArrayList;

import hu.mobilalk.turafoglalas.R;
import hu.mobilalk.turafoglalas.activity.TourMenuActivity;
import hu.mobilalk.turafoglalas.model.Tour;

public class TourElementAdapter extends RecyclerView.Adapter<TourElementAdapter.ViewHolder>
{
    private final ArrayList<Tour> tours;
    private final Context context;
    private int lastPosition = -1;

    public TourElementAdapter(Context context, ArrayList<Tour> tours)
    {
        this.tours = tours;
        this.context = context;
    }

    @Override
    public TourElementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tour, parent, false));
    }

    @Override
    public void onBindViewHolder(TourElementAdapter.ViewHolder viewHolder, int position)
    {
        viewHolder.bindTo(tours.get(position));

        if (viewHolder.getAdapterPosition() > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop);
            viewHolder.itemView.startAnimation(animation);
            lastPosition = viewHolder.getAdapterPosition();
        }

        viewHolder.cardView.setOnLongClickListener(view -> {
            viewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom));
            return true;
        });
    }

    @Override
    public int getItemCount()
    {
        return tours.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView name;
        private final TextView location;
        private final TextView length;
        private final ImageView coverImage;

        private final FirebaseUser user;

        public CardView cardView;

        ViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.tour_name);
            location = itemView.findViewById(R.id.tour_location);
            length = itemView.findViewById(R.id.tour_length);
            coverImage = itemView.findViewById(R.id.tour_image);

            user = FirebaseAuth.getInstance().getCurrentUser();

            cardView = itemView.findViewById(R.id.card);
        }

        void bindTo(Tour tour)
        {
            name.setText(tour.getName());
            location.setText(tour.getLocation());
            length.setText(MessageFormat.format("Túra hossza: {0} km", tour.getLength()));

            Glide.with(context).load(tour.getImageResource()).into(coverImage);

            itemView.findViewById(R.id.apply).setOnClickListener(view -> ((TourMenuActivity) context).apply(tour));
            itemView.findViewById(R.id.take_picture).setOnClickListener(view -> ((TourMenuActivity) context).takePicture());
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((TourMenuActivity) context).delete(tour));

            if(!tour.isAvailable())
                itemView.findViewById(R.id.apply).setVisibility(View.GONE);

            if(user == null || !"turaapp_admin@admin.com".equals(user.getEmail()))
                itemView.findViewById(R.id.delete).setVisibility(View.GONE);
        }
    }
}
