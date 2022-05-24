package com.bedessee.salesca.reportsmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import com.bedessee.salesca.R;
//import com.bedessee.salesca.main.MainActivity2;
import com.bedessee.salesca.product.brand.BrandFragment;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.DividerItemDecoration;
import com.bedessee.salesca.utilities.ReportsUtilities;
import com.bedessee.salesca.utilities.SpacesItemDecoration;
import com.bedessee.salesca.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ReportFragment extends Fragment {


RecyclerView recyclerView;
ReportAdapter reportAdapter;
    public final static String TAG = "ReportList";
    private static ReportFragment instance;
    public static ReportFragment getInstance() {
        if (instance == null) {
            instance = new ReportFragment();
        }
        return instance;
    }

    public ReportFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_report, container, false);
        SharedPreferences sh = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String orient= sh.getString("orientation","landscape");
        if(orient.equals("landscape")){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        initReportsMenu();
        return view;
    }
    private void initReportsMenu() {
        final List<ReportsMenu> reportsMenus = new ArrayList<>();
        final List<String> reportsMenuTitles = new ArrayList<>();
        final Cursor reportsCursor = getActivity().getContentResolver().query(Contract.ReportsMenu.CONTENT_URI, null, null, null, null);

//        reportsMenus.add(null);
//        reportsMenuTitles.add("Select Report");

      //  SpinnerAdapter spinnerAdapter = null;
        if (reportsCursor != null && reportsCursor.moveToFirst()) {
            for (int i = 0; i < reportsCursor.getCount(); i++) {
                final ReportsMenu sideMenu = ProviderUtils.cursorToReportsMenu(reportsCursor);
                reportsMenus.add(sideMenu);

                reportsCursor.moveToNext();
            }
            // WTF, first item is null
            reportsMenus.remove(0);
            // This will sort by natural order, the menus should come with number as first char.
            Collections.sort(reportsMenus, new Comparator<ReportsMenu>() {
                @Override
                public int compare(ReportsMenu o1, ReportsMenu o2) {
                    int o1Number = Character.getNumericValue(o1.getSideMenuDisplay().charAt(0));
                    int o2Number = Character.getNumericValue(o2.getSideMenuDisplay().charAt(0));
                    return o1Number - o2Number;
                }
            });
            List<String> menuList = new ArrayList<>();

            for (ReportsMenu menu : reportsMenus) {
                menuList.add(menu.getSideMenuDisplay());
            }
            reportsMenuTitles.addAll(menuList);
            reportAdapter = new ReportAdapter(getContext(), reportsMenuTitles) {
                @Override
                protected void onClickView(int pos) {
                        final Store store = StoreManager.getCurrentStore();
                        if (store != null && store.getStatementUrl() != null) {

                            ReportsMenu reportsMenu = reportsMenus.get(pos);
                            ReportsUtilities.Companion.openReportMenu(getContext(), reportsMenu, store);

                        } else {
                            Utilities.shortToast(getContext(), "Please select a store first");

                        }
                    }
            };
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(reportAdapter);
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.default_margin);
            recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels,true));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        }


    }
}