package trac.portableexpensesdiary.maps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.BaseActivity;
import trac.portableexpensesdiary.model.Category;
import trac.portableexpensesdiary.model.ExpenseTrackingDetails;
import trac.portableexpensesdiary.utils.Constants;
import trac.portableexpensesdiary.utils.RoundUtils;

public class ExpenseLocationActivity extends BaseActivity {

    private ExpenseTrackingDetails currentExpenseTrackingDetail;
    private GoogleMap googleMap;
    private MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense_location);

        getSupportActionBar().hide();

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    private void initViews() {
        mapView =
                (MapView) findViewById(R.id.mapView);

        setMapRequirements();
    }

    private void setMapRequirements() {
        final String currentDetailId =
                getIntent().getStringExtra(Constants.EXPENSE_TRACKING_DETAIL_TAG);
        currentExpenseTrackingDetail = ExpenseTrackingDetails.getExpenseDetail(
                UUID.fromString(currentDetailId)
        );

        mapView.onCreate(null);
        mapView.onResume();

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "Error occurred when trying to load google maps!", Toast.LENGTH_SHORT).show();
        }

        mapView.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        LatLng registeredAtLocation = new LatLng(
                                currentExpenseTrackingDetail.getLatitude(),
                                currentExpenseTrackingDetail.getLongitude()
                        );
                        googleMap.addMarker(
                                new MarkerOptions()
                                        .position(registeredAtLocation)
                                        .title(Category.getCategoryById(
                                                currentExpenseTrackingDetail
                                                        .getExpenseCategoryId()
                                                ).getCategoryName()
                                        )
                                        .snippet(
                                                String.format(
                                                        "%s%s%s",
                                                        getString(R.string.manual_reg_expense_amount),
                                                        " ",
                                                        RoundUtils.floatToStringWithCurrency(
                                                                currentExpenseTrackingDetail
                                                                        .getExpensePrice()
                                                        )
                                                )
                                        )
                        );

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                registeredAtLocation,
                                googleMap.getMaxZoomLevel() - 4
                        );

                        googleMap.animateCamera(
                                cameraUpdate);


                    }
                }
        );
    }
}
