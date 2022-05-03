package hu.mobilalk.turafoglalas.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import hu.mobilalk.turafoglalas.R;
import hu.mobilalk.turafoglalas.model.Tour;
import hu.mobilalk.turafoglalas.notification.NotificationController;
import hu.mobilalk.turafoglalas.recyclerview.TourElementAdapter;

public class TourMenuActivity extends AppCompatActivity
{
    private static final String LOG_TAG = TourMenuActivity.class.getName();

    private ArrayList<Tour> tours;
    private TourElementAdapter tourElementAdapter;

    private CollectionReference collectionReference;

    private NotificationController notificationController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tour_menu);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            finish();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));

        tours = new ArrayList<>();

        tourElementAdapter = new TourElementAdapter(this, tours);
        recyclerView.setAdapter(tourElementAdapter);

        collectionReference = FirebaseFirestore.getInstance().collection("Tours");

        notificationController = new NotificationController(this);

        selectTours();
    }

    private void createTours(CollectionReference collectionReference)
    {
        this.collectionReference = collectionReference;
    }

    private void selectTours()
    {
        tours.clear();
        collectionReference.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots)
            {
                Tour tour = document.toObject(Tour.class);
                tour.setId(document.getId());
                tours.add(tour);
            }

            if (tours.size() == 0)
            {
                new CreateCollection().execute();
                selectTours();
            }

            tourElementAdapter.notifyDataSetChanged();
        });
    }

    public void apply(Tour tour)
    {
        new UpdateAvailability().execute(tour);
        notificationController.sendNotification(tour.getName() + " túrára a jelentkezés sikeresen megtörtént!");
    }

    public void takePicture()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void delete(Tour tour)
    {
        new DeleteTour().execute(tour);
        Toast.makeText(this, tour.getName() + " sikeresen kitörölve!", Toast.LENGTH_LONG).show();
    }

    public class CreateCollection extends AsyncTask<Void, Void, CollectionReference> {
        @Override
        protected CollectionReference doInBackground(Void... voids)
        {
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Tours");;

            String[] names = getResources().getStringArray(R.array.tour_names);
            String[] locations = getResources().getStringArray(R.array.tour_locations);
            int[] lengths = getResources().getIntArray(R.array.tour_lengths);
            TypedArray covers = getResources().obtainTypedArray(R.array.tour_covers);

            for (int i = 0; i < names.length; i++)
                collectionReference.add(new Tour(
                        names[i],
                        locations[i],
                        lengths[i],
                        covers.getResourceId(i, 0),
                        true));

            covers.recycle();
            return collectionReference;
        }

        @Override
        protected void onPostExecute(CollectionReference collectionReference)
        {
            createTours(collectionReference);
        }
    }

    public class UpdateAvailability extends AsyncTask<Tour, Void, Void> {
        @Override
        protected Void doInBackground(Tour... tours)
        {
            Tour tour = tours[0];
            collectionReference.document(tour._getId()).update("available", false)
                    .addOnFailureListener(fail -> Log.d(LOG_TAG, tour.getName() + " tour cannot be updated"));
            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            selectTours();
        }
    }

    public class DeleteTour extends AsyncTask<Tour, Void, Void> {
        @Override
        protected Void doInBackground(Tour... tours)
        {
            Tour tour = tours[0];

            DocumentReference ref = collectionReference.document(tour._getId());
            ref.delete().addOnFailureListener(fail -> Log.d(LOG_TAG, tour.getName() + " tour cannot be deleted"));

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            selectTours();
        }
    }
}
