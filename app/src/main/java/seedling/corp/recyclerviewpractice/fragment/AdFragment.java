package seedling.corp.recyclerviewpractice.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import seedling.corp.recyclerviewpractice.R;

/**
 * Created by Ankur Nigam on 28-01-2016.
 */
public class AdFragment extends Fragment {

    public AdFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ad_layout, container, false);
        loadBannerAd(view);
        return view;
    }

        private void loadBannerAd(View view){
        AdView adView = (AdView) view.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("BE0B283D9DB35079AECC55005262BED2")
                .build();

        adView.loadAd(adRequest);
    }
}
