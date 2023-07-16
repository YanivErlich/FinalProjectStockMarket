package com.example.finalprojectstockmarket.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.finalprojectstockmarket.Adapter.AutoSearchAdapter;
import com.example.finalprojectstockmarket.Interface.ApiCall;
import com.example.finalprojectstockmarket.Model.Company;
import com.example.finalprojectstockmarket.R;
import com.example.finalprojectstockmarket.Section.FavoriteSection;
import com.example.finalprojectstockmarket.Section.PortfolioSection;
import com.example.finalprojectstockmarket.Utilities.MySP2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    public static final String EXTRA_TICKER = "TICKER";

    public static final String SEARCH_URL = "https://stock-api-git-master-abd22655-gmailcom.vercel.app/api/search/";
    public static final String PRICE_URL = "https://stock-api-git-master-abd22655-gmailcom.vercel.app/api/price/";
    public static final int TOT_API_CALLS = 2;

    private PortfolioSection.ClickListener listener;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private FavoriteSection favoriteSection;
    private PortfolioSection portFolioSection;
    private AutoSearchAdapter autoSearchAdapter;
    private Handler handler;

    private FavoriteSection.ClickListener clickListener;
    private List<Company> favouriteList;
    private List<Company> portfolioList;
    TextView tvCreditAmount, tvUsername;
    private RecyclerView recyclerView;

    ImageView imgAdd, imgUser;
    private NestedScrollView homeViewContainer;
    private Context context;
    private int numApiCalls;
    private boolean isApiFailed;
    private boolean isSelectedFromList;

    public static Map<String, String> myFavourites = new HashMap<>();
    public static Map<String, String> myPortfolio = new HashMap<>();
    public static String creditAmount="";
    DatabaseReference dbFavorites, dbShares, dbRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFirebaseDb();
        isSelectedFromList = false;
        context = this;
        initViews();
        setButtonsClickListeners();
        init();

    }

    private void getFirebaseDb() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbFavorites = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        dbShares = FirebaseDatabase.getInstance().getReference("SharesOwned").child(userId);
        dbRef = FirebaseDatabase.getInstance().getReference("CreditAmount").child(userId);
        tvUsername = findViewById(R.id.tvUsername);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tvUsername.setText(firebaseUser.getDisplayName());
    }

    private void setButtonsClickListeners() {
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
    }

    private void initViews() {
        homeViewContainer = findViewById(R.id.container_home);
        imgAdd = findViewById(R.id.imgAdd);
        imgUser = findViewById(R.id.imgUser);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRestart() {
        super.onRestart();
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        numApiCalls = 0;
        isApiFailed = false;
        tvCreditAmount = findViewById(R.id.tvCreditAmount);
        sectionedAdapter = new SectionedRecyclerViewAdapter();
        this.favouriteList = new ArrayList<>();
        this.portfolioList = new ArrayList<>();

    }

//This is the search where u search for a stock you want to buy or sell.
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        searchEditText.setTextColor(getResources().getColor(R.color.white));

        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search");

        SearchView.SearchAutoComplete mSearchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        autoSearchAdapter = new AutoSearchAdapter(this,
                android.R.layout.simple_dropdown_item_1line);

        mSearchAutoComplete.setAdapter(autoSearchAdapter);
        mSearchAutoComplete.setDropDownHeight(1300);
        mSearchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchAutoComplete.setText(autoSearchAdapter.getObject(position));
                isSelectedFromList = true;
            }
        });


        //This auto completes
        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                isSelectedFromList = false;
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //When click sends you to the stock information page
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!isSelectedFromList){
                    return false;
                }
                String[] input = query.split("-");
                redirectToStockInfo(input[0]);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSelectedFromList = false;
                return false;
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    String input = mSearchAutoComplete.getText().toString();

                    if (!TextUtils.isEmpty(mSearchAutoComplete.getText()) && input.length() >= 3) {
                        makeApiCall(input);
                    }
                }
                return false;
            }
        });

        return true;
    }

    private void makeApiCall(String text) {
        ApiCall.make(this, text, SEARCH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        stringList.add(row.getString("ticker") + "-" + row.getString("name"));
                    }
                } catch (Exception e) {
                }
                autoSearchAdapter.setData(stringList);
                autoSearchAdapter.notifyDataSetChanged();
            }
        }, error -> Log.i("error", "error in search http " + error));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeApiCallPrice(String key, String tickers) {

        String[] tickersArray = tickers.split(",");
        Map<String, Integer> tickerToPosition = new HashMap<>();
        List<Company> companiesList = new ArrayList<>();

        for (int i = 0; i < tickersArray.length; i++){
            tickerToPosition.put(tickersArray[i], i);
            companiesList.add(new Company());
        }

        if (tickers.length() == 0){
            numApiCalls++;
            if (isApiFailed || numApiCalls == TOT_API_CALLS){

                numApiCalls = 0;
                isApiFailed = false;

            }
            return;
        }
        ApiCall.make(this, tickers, PRICE_URL, response -> {

            double stockValue = 0;

            try {
                JSONObject responseObject = new JSONObject(response);
                JSONArray array = responseObject.getJSONArray("results");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject row = array.getJSONObject(i);
                    String ticker = row.getString("ticker");
                    String last = (row.getString("last") != "null") ? row.getString("last") : row.getString("tngoLast");
                    String prevClose = row.getString("prevClose");
                    String name = "";
                    String shares = "";
                    String name_or_shares = "";

                    if (key.equals(MySP2.FAVOURITES)){
                        name = myFavourites.get(ticker);
                        name_or_shares = myFavourites.get(ticker);

                        if (myPortfolio.containsKey(ticker)){
                            name_or_shares = myPortfolio.get(ticker) + " shares";
                        }

                    }else{
                        shares = myPortfolio.get(ticker);
                        name_or_shares = myPortfolio.get(ticker);
                        stockValue += Double.parseDouble(last) * Double.parseDouble(shares);
                    }

                    DecimalFormat df = new DecimalFormat("####0.00");
                    Company newCompany = new Company(name, ticker, shares, last, prevClose, name_or_shares, context);

                    newCompany.last = df.format(Double.parseDouble(newCompany.last));
                    newCompany.change = Double.parseDouble(df.format(newCompany.change));

                    companiesList.set(tickerToPosition.get(ticker), newCompany);

                }
            } catch (Exception e) {
                isApiFailed = true;
            }

            if (!isApiFailed){
                if (key.equals(MySP2.FAVOURITES)){

                    favouriteList.clear();
                    favouriteList.addAll(companiesList);
                    sectionedAdapter.getAdapterForSection(favoriteSection).notifyAllItemsChanged();
                }else{
                    portfolioList.clear();
                    portfolioList.addAll(companiesList);
                    DecimalFormat df = new DecimalFormat("####0.00");
                    sectionedAdapter.getAdapterForSection(portFolioSection).notifyAllItemsChanged();
                }
            }

            numApiCalls++;

            if (isApiFailed || numApiCalls == TOT_API_CALLS){
                numApiCalls = 0;
                isApiFailed = false;
            }

        }, error -> {
            isApiFailed = true;
            Log.i("error", "error in search http " + error);
            Toast.makeText(context, "\"error in search http \" + error", Toast.LENGTH_SHORT).show();
        });
    }

    public void redirectToStockInfo(String ticker) {
        Intent intent = new Intent(this, StockInfoActivity.class);
        intent.putExtra(EXTRA_TICKER, ticker);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                creditAmount = dataSnapshot.getValue(String.class);
                tvCreditAmount.setText("$"+creditAmount);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving data: " + databaseError.getMessage());
            }
        });

        dbShares.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myPortfolio = (Map<String, String>) dataSnapshot.getValue();
                    for (Map.Entry<String, String> entry : myPortfolio.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Company newCompany = new Company("nul", key, value, "0", "0", "", context);
                        portfolioList.add(newCompany);
                    }
                    portFolioSection = new PortfolioSection("Shares Owned", portfolioList, context,
                            (company, itemAdapterPosition) -> redirectToStockInfo(company.ticker));
                    sectionedAdapter.addSection(portFolioSection);
                    getFavorite();
                }else {
                    getFavorite();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Error retrieving data: " + databaseError.getMessage());
            }
        });
    }

    private void getFavorite() {
        dbFavorites.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the data as a Map<String, String>
                    myFavourites = (Map<String, String>) dataSnapshot.getValue();
                    for (Map.Entry<String, String> entry : myFavourites.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Company newCompany = new Company("nul", key, value, "0", "0", "Loading", context);

                        favouriteList.add(newCompany);
                    }
                    favoriteSection = new FavoriteSection("FAVORITES", favouriteList, context,
                            (company, itemAdapterPosition) -> redirectToStockInfo(company.ticker));

                    sectionedAdapter.addSection(favoriteSection);
                    recyclerView = (RecyclerView) findViewById(R.id.rvHome);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(sectionedAdapter);

                    homeViewContainer.setVisibility(View.VISIBLE);

                    String favoriteTickers = String.join(",", myFavourites.keySet());
                    String portfolioTickers = String.join(",", myPortfolio.keySet());

                    makeApiCallPrice(MySP2.FAVOURITES, favoriteTickers);
                    makeApiCallPrice(MySP2.PORTFOLIO, portfolioTickers);
                }else {
                    String favoriteTickers = String.join(",", myFavourites.keySet());
                    String portfolioTickers = String.join(",", myPortfolio.keySet());

                    makeApiCallPrice(MySP2.FAVOURITES, favoriteTickers);
                    makeApiCallPrice(MySP2.PORTFOLIO, portfolioTickers);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Error retrieving data: " + databaseError.getMessage());
            }
        });

    }


    public void openAddDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_dialog);

        TextView tvCashInHand = (TextView) dialog.findViewById(R.id.tv_trading_cash);
        EditText etShareInput = (EditText) dialog.findViewById(R.id.et_dialog_input_shares);
        Button btnBuy = (Button) dialog.findViewById(R.id.btn_buy);

        tvCashInHand.setText("Amount in waller($" + creditAmount);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String credit = etShareInput.getText().toString().trim();
                if(credit.isEmpty()){
                    etShareInput.setError("Required!");
                    etShareInput.requestFocus();
                    return;
                }
                double amount = Double.parseDouble(credit);
                if (amount <= 0.1){
                    etShareInput.setError("Invalid amount!");
                    etShareInput.requestFocus();
                    return;
                }

                double extCash = Double.parseDouble(creditAmount);
                double newAmt = extCash+amount;

                creditAmount = String.valueOf(newAmt);
                tvCreditAmount.setText("$"+creditAmount);
                dbRef.setValue(String.valueOf(newAmt));

                Toast.makeText(context, "Amount added in wallet successfully", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }
}