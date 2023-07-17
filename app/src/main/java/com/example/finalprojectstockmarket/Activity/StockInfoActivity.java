package com.example.finalprojectstockmarket.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.finalprojectstockmarket.Interface.ApiCall;
import com.example.finalprojectstockmarket.Model.Company;
import com.example.finalprojectstockmarket.Adapter.NewsAdapter;
import com.example.finalprojectstockmarket.Model.News;
import com.example.finalprojectstockmarket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockInfoActivity extends AppCompatActivity {

    public static final String PRICE_URL = "https://stock-api-git-master-abd22655-gmailcom.vercel.app/api/price/";
    public static final String DETAIL_URL = "https://stock-api-git-master-abd22655-gmailcom.vercel.app/api/detail/";
    public static final String NEWS_URL = "https://stock-api-git-master-abd22655-gmailcom.vercel.app/api/news/";
    public static final int TOT_API_CALLS = 3;

    private boolean isFavourites;
    private String ticker;
    private String name;
    private double lastPrice;
    private Context context;

    private double sharesInputed = 0;
    private double cashUsed;
    private RecyclerView recyclerViewNews;

    private NestedScrollView nestedScrollView;
    private int numApiCalls;
    private boolean isApiFailed;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private String cashInHand;

    DatabaseReference dbShares, dbFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getFirebaseDb();

        numApiCalls = 0;
        isApiFailed = false;
        initToolBar();
        setNews();

        makeApiCallPrice(ticker);
        makeApiCallSummary(ticker);
        makeApiCallNews(ticker);
    }



    private void initToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        MenuItem item = menu.findItem(R.id.toggle_star);
        if (MainActivity.myFavourites.containsKey(ticker)){
            item.setIcon(R.drawable.ic_baseline_star_24);
            isFavourites = true;
        } else{
            item.setIcon(R.drawable.ic_baseline_star_border_24);
            isFavourites = false;
        }
        return true;
    }

    void getFirebaseDb(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbFavorites = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        dbShares = FirebaseDatabase.getInstance().getReference("SharesOwned").child(userId);
        context = this;
        cashInHand = MainActivity.creditAmount;
        isFavourites = false;
        Intent intent = getIntent();
        ticker = intent.getStringExtra(MainActivity.EXTRA_TICKER);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toggle_star:

                if (isFavourites){
                    isFavourites = false;
                    item.setIcon(R.drawable.ic_baseline_star_border_24);
                    MainActivity.myFavourites.remove(ticker);
                    Toast toast = Toast.makeText(this, ticker + " was removed from favourites", Toast.LENGTH_SHORT);
                    toast.show();
                } else{
                    isFavourites = true;
                    item.setIcon(R.drawable.ic_baseline_star_24);
                    MainActivity.myFavourites.put(ticker, name);
                    Toast toast = Toast.makeText(this, ticker + " was added to favourites", Toast.LENGTH_SHORT);
                    toast.show();
                }
                Log.i("added-removed", "favouroites......" +MainActivity. myFavourites);
                dbFavorites.setValue(MainActivity.myFavourites);

                return true;

            default:
                return true;

        }
    }


    private void setNews() {
        nestedScrollView = findViewById(R.id.details_screen);
        recyclerViewNews = findViewById(R.id.rvNews);
        recyclerViewNews.setHasFixedSize(true);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));

        newsList = new ArrayList<>();
        NewsAdapter.OnItemClickListener listener = new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(News newsItem, int position) {
                openChrome(newsItem.url);
                Log.e("CLICK_NEWS", "news item clicked at " + position);
            }

        };
        newsAdapter = new NewsAdapter(newsList, this, listener);
        recyclerViewNews.setAdapter(newsAdapter);
    }
    private void makeApiCallPrice(String ticker) {

        ApiCall.make(this, ticker, PRICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    Log.i("length:", "len: " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        String last = (row.getString("last") != "null") ? row.getString("last") : row.getString("tngoLast");;
                        String prevClose = (row.getString("prevClose") != "null") ? row.getString("prevClose"): "0.0";

                        TextView tvTicker = (TextView) findViewById(R.id.detail_ticker);
                        TextView tvLast = (TextView) findViewById(R.id.detail_last);
                        TextView tvChange = (TextView) findViewById(R.id.detail_change);
                        TextView tvMarketValue = findViewById(R.id.tvMarket_value);
                        TextView tvShares = findViewById(R.id.tvDetails_shares);

                        Locale locale = new Locale("en", "US");
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                        Company company = new Company(name, ticker,"", last, prevClose, "", context);
                        lastPrice = Double.parseDouble(company.last);

                        company.last = fmt.format(Double.parseDouble(company.last));

                        tvTicker.setText(company.ticker);
                        tvChange.setText(fmt.format(company.change));
                        tvChange.setTextColor(company.changeColor);
                        tvLast.setText(company.last);
                        tvShares.setText("You have 0 shares of " + ticker);

                        if (MainActivity.myPortfolio.containsKey(ticker)){

                            String shares = MainActivity.myPortfolio.get(ticker);
                            double val = Double.parseDouble(shares) * Double.parseDouble(last);
                            tvShares.setText("Shares owned: " + shares);
                            tvMarketValue.setText("Market Value: " + fmt.format(val));
                        }else {
                            tvMarketValue.setText("Start Trading|");
                        }

                    }
                } catch (Exception e) {
                    isApiFailed = true;
                }

                numApiCalls++;

                if (isApiFailed){
                    numApiCalls = 0;
                    // display an error message or do some error handling

                } else if (numApiCalls == TOT_API_CALLS){
                    numApiCalls = 0;
                    nestedScrollView.setVisibility(View.VISIBLE);
                }

            }
        }, (Response.ErrorListener) error -> {
            isApiFailed = true;
            Log.i("error", "error in search http " + error);
        });
    }

    private void makeApiCallSummary(String ticker) {

        ApiCall.make(this, ticker, DETAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    Log.i("length:", "len: " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        String description = row.getString("description");
                        name = row.getString("name");;

                        TextView tvCompanyName = (TextView) findViewById(R.id.detail_company_name);
                        TextView tvDescription = (TextView) findViewById(R.id.tvDetails_desc);
                        tvCompanyName.setText(name);
                        tvDescription.setText(description);

                    }
                } catch (Exception e) {
                }

                numApiCalls++;

                if (isApiFailed){
                    numApiCalls = 0;
                    // display an error message or do some error handling

                } else if (numApiCalls == TOT_API_CALLS){
                    numApiCalls = 0;
                    nestedScrollView.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isApiFailed = true;
                Log.i("error", "error in search http " + error);
            }
        });
    }

    private void openChrome(String url){
        Uri uri = Uri.parse(url); // missing 'http://' will cause crash
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }



    public void openSuccessDialog(double sharesTraded, String type) {

        TextView tvMarketValue = findViewById(R.id.tvMarket_value);
        TextView tvShares = findViewById(R.id.tvDetails_shares);
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.success_trade_dialog);

        TextView tvTradedMsg = (TextView) dialog.findViewById(R.id.tv_shares_traded_msg);
        tvTradedMsg.setText("You have successfully " + type + " " + sharesTraded + " of " + ticker);
        Button btnDone = (Button) dialog.findViewById(R.id.btn_done);

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            numApiCalls = TOT_API_CALLS - 1;

        });

        tvShares.setText("You have 0 shares of " + ticker);

        if (MainActivity.myPortfolio.containsKey(ticker)){
            String shares = MainActivity.myPortfolio.get(ticker);
            double val = Double.parseDouble(shares) * lastPrice;
            tvShares.setText("Shares owned: " + shares+".");
            tvMarketValue.setText("Market Value: $" + fmt.format(val)+".");
        }else {
            tvMarketValue.setText("Start Trading|");
        }

        makeApiCallPrice(ticker);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public void buyStock(Dialog dialog){
        Button btnBuy =  dialog.findViewById(R.id.btn_buy);
        EditText etShareInput =  dialog.findViewById(R.id.et_dialog_input_shares);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double sharesInputed = 0;
                double cashUsed;

                if (etShareInput.getText().length() != 0) {
                    try {
                        sharesInputed = Double.parseDouble(String.valueOf(etShareInput.getText()));
                    } catch (NumberFormatException e) {
                        sharesInputed = 0;
                        Toast toast = Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                }

                if (sharesInputed <= 0) {
                    Toast toast = Toast.makeText(context, "Cannot buy less than 0 shares!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if (lastPrice * sharesInputed > Double.parseDouble(cashInHand)) {
                    Toast toast = Toast.makeText(context, "Not enough money in wallet to buy!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if (MainActivity.myPortfolio.containsKey(ticker)) {
                    double totShares = Double.parseDouble(MainActivity.myPortfolio.get(ticker)) + sharesInputed;
                    MainActivity.myPortfolio.put(ticker, String.valueOf(totShares));
                } else {
                    MainActivity.myPortfolio.put(ticker, String.valueOf(sharesInputed));
                }

                dbShares.setValue(MainActivity.myPortfolio);
                cashUsed = (lastPrice * sharesInputed);
                DecimalFormat df = new DecimalFormat("####0.00");
                cashInHand = df.format(Double.parseDouble(cashInHand) - cashUsed);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("CreditAmount");
                dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(cashInHand);
                dialog.dismiss();

                openSuccessDialog(sharesInputed, "bought");
            }
        });


    }

    void sellStock(Dialog dialog){
        Button btnSell =  dialog.findViewById(R.id.btn_sell);

        EditText etShareInput =  dialog.findViewById(R.id.et_dialog_input_shares);

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double sharesInputed = 0;
                double cashReceived;

                if (etShareInput.getText().length() != 0) {
                    try {
                        sharesInputed = Double.parseDouble(String.valueOf(etShareInput.getText()));
                    } catch (NumberFormatException e) {
                        Toast toast = Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                }

                if (sharesInputed <= 0) {
                    Toast toast = Toast.makeText(context, "Cannot sell less than 0 shares!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if (!MainActivity.myPortfolio.containsKey(ticker) || Double.parseDouble(MainActivity.myPortfolio.get(ticker)) < sharesInputed) {
                    Toast toast = Toast.makeText(context, "Not enough shares to sell!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                double totShares = Double.parseDouble(MainActivity.myPortfolio.get(ticker)) - sharesInputed;
                DecimalFormat df = new DecimalFormat("####0.00");

                Log.e("outside", "out: " + MainActivity.myPortfolio.get(ticker) + " " + etShareInput.getText());

                if (totShares <= 0.000001) {
                    Log.e("here", "here");
                    MainActivity.myPortfolio.remove(ticker);
                } else {
                    MainActivity.myPortfolio.put(ticker, String.valueOf(df.format(totShares)));
                }

                dbShares.setValue(MainActivity.myPortfolio);
                cashReceived = (lastPrice * sharesInputed);

                cashInHand = df.format(Double.parseDouble(cashInHand) + cashReceived);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("CreditAmount");
                dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(cashInHand);

                dialog.dismiss();
                openSuccessDialog(sharesInputed, "sold");
            }
        });

    }

    public void openTradeDialog(View view) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.trading_dialog);

        TextView tvTitle =  dialog.findViewById(R.id.tv_trading_title);
        TextView tvTotVal =  dialog.findViewById(R.id.tv_trading_tot_share_val);
        TextView tvCashInHand =  dialog.findViewById(R.id.tv_trading_cash);
        EditText etShareInput =  dialog.findViewById(R.id.et_dialog_input_shares);
        tvTitle.setText("Trade: " + name + " shares");
        tvTotVal.setText("0 x " + lastPrice + "/share = " + "$0.00");
        tvCashInHand.setText("($" + cashInHand + " available to buy " + ticker+" shares)");

        buyStock(dialog);
        sellStock(dialog);

        etShareInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                double shares = 0;
                double totVal;
                String totValTxt;
                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                String lastStr;
                String totValStr;

                if (s.length() == 0){
                    s = "0";
                }else{

                    try {
                        shares = Double.parseDouble(s.toString());
                    } catch(NumberFormatException e){
                        shares = 0;
                    }
                }

                totVal = lastPrice * shares;
                lastStr = fmt.format(lastPrice);
                totValStr = fmt.format(totVal);

                totValTxt = s + " x " + lastStr + "/share = " + totValStr;
                tvTotVal.setText(totValTxt);

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }
        public void toggleDescription(View view){

        TextView tvDescription = findViewById(R.id.tvDetails_desc);
        Button toggleDescBtn = findViewById(R.id.btn_desc);

        CharSequence btnDescTxt = toggleDescBtn.getText();

        if (btnDescTxt.equals("Show more...")){
            toggleDescBtn.setText("Show less");
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvDescription.setEllipsize(null);

        }else{
            toggleDescBtn.setText("Show more...");
            tvDescription.setMaxLines(2);
            tvDescription.setEllipsize(TextUtils.TruncateAt.END);
        }
    }


    private void makeApiCallNews(String ticker) {

        ApiCall.make(this, ticker, NEWS_URL, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                List<News> tempNewsList = new ArrayList<>();

                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    Log.i("length:", "len: " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        String url = row.getString("url");
                        String title = row.getString("title");
                        String src = row.getString("source");
                        String urlToImg = row.getString("urlToImage");
                        String timestamp = row.getString("publishedAt");

                        if (i == 10){
                            Log.i("NEWS", "urlToImg: " + urlToImg + "\ntitle: " + title);

                        }
                        News newsItem = new News(src, urlToImg, title, timestamp, url);
                        tempNewsList.add(newsItem);
                    }
                } catch (Exception e) {
                    isApiFailed = true;
                    Toast.makeText(StockInfoActivity.this, "Catch Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                newsList.clear();
                newsList.addAll(tempNewsList);


                Toast.makeText(StockInfoActivity.this, "news list size: "+newsList.size(), Toast.LENGTH_SHORT).show();

                numApiCalls++;

                if (isApiFailed){
                    numApiCalls = 0;
                } else if (numApiCalls == TOT_API_CALLS){
                    numApiCalls = 0;
                    nestedScrollView.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isApiFailed = true;
                Log.i("errorrr", "error in search http " + error);

                Toast.makeText(StockInfoActivity.this, "error in search http: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}