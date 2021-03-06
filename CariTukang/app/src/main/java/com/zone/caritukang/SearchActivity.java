package com.zone.caritukang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;
import static com.zone.caritukang.DataURL.ROOT_URL;

public class SearchActivity extends AppCompatActivity {

    ArrayList<String> worldlist;
    ArrayList<String> idlist;


    ArrayList<String> worldkotalist;
    ArrayList<String> idkotalist;

    ArrayList<String> worldsublist;
    ArrayList<String> idsublist;

    String kategori = "0";
    String namakategori = "0";

    String kota = "0";
    String namakota = "0";

    String sub = "0";
    String namasub = "0";


    private FirebaseDatabase mFirebaseDatabaseReference;
    private DatabaseReference mykotaDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        new JSONAsyncTask().execute(ROOT_URL+"/carijasa/kategori.php");
        new KOTAAsyncTask().execute(ROOT_URL+"/carijasa/kota.php");




        Spinner mySpinner = (Spinner) findViewById(R.id.kategori_spinner);

//        if(worldlist.isEmpty()){
//
//            mySpinner.setVisibility(View.GONE);
//
//
//        }else{
//
//            // Spinner adapter
//            mySpinner
//                    .setAdapter(new ArrayAdapter<String>(SearchActivity.this,
//                            android.R.layout.simple_spinner_dropdown_item,
//                            worldlist));
//
//        }

//        mySpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(SearchActivity.this, "KATEGORI " + kategori, Toast.LENGTH_LONG).show();
//            }
//        });



        // Spinner on item click listener
        mySpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        // Locate the textviews in activity_main.xml
                        System.out.println("ID KATEGORI"+idlist.get(position));

                        kategori = idlist.get(position);
                        namakategori = worldlist.get(position);

                        Toast.makeText(SearchActivity.this, "KATEGORI " + kategori, Toast.LENGTH_LONG).show();

                        new SUBAsyncTask().execute(ROOT_URL+"/carijasa/sub.php?id="+kategori);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        Toast.makeText(SearchActivity.this, "KATEGORI " + kategori, Toast.LENGTH_LONG).show();
                    }
                });



        Button buttonPencarian = (Button)findViewById(R.id.buttonPencarian);
        buttonPencarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edKeyword = (EditText)findViewById(R.id.edKeyword);
                String kata = edKeyword.getText().toString();


                String kat = kategori;
                String subkat = sub;
                String lokasi = kota;

                String kunci = "?keyword="+kata+"&sub="+subkat+"&kat="+kat+"&lokasi="+lokasi;

                try {
                    String valuek = new String(kunci.getBytes("UTF-8"));
                    Intent k = new Intent(SearchActivity.this, SearchResult.class);
                    k.putExtra("key",valuek);
                    startActivity(k);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        });


    }


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SearchActivity.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldlist = new ArrayList<String>();
                idlist = new ArrayList<String>();


                worldlist.add("SEMUA KATEGORI");
                idlist.add("0");


                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("kategori");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idlist.add(object.getString("id"));
                        worldlist.add(object.getString("nama"));
                        //idlist.add(object.getString("nama"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            kategori = idlist.get(0);
            namakategori = worldlist.get(0);
            Spinner mySpinner = (Spinner) findViewById(R.id.kategori_spinner);


            // Spinner adapter
            mySpinner
                    .setAdapter(new ArrayAdapter<String>(SearchActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            worldlist));



            // Spinner on item click listener
            mySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            // TODO Auto-generated method stub
                            // Locate the textviews in activity_main.xml
                            System.out.println("ID KATEGORI"+idlist.get(position));

                            kategori = idlist.get(position);
                            namakategori = worldlist.get(position);


                            Spinner subSpinner = (Spinner) findViewById(R.id.sub_spinner);
                            TextView txtSUB = (TextView)findViewById(R.id.txtSUB);



                            if(kategori.equals("0")){
                                subSpinner.setVisibility(View.GONE);
                                txtSUB.setVisibility(View.GONE);
                                Toast.makeText(SearchActivity.this, "MAENKAN"+ kategori, Toast.LENGTH_LONG).show();
                            }else{
                                subSpinner.setVisibility(View.VISIBLE);
                                txtSUB.setVisibility(View.VISIBLE);
                                Toast.makeText(SearchActivity.this, "MAENKAN"+ kategori, Toast.LENGTH_LONG).show();
                                new SUBAsyncTask().execute(ROOT_URL+"/carijasa/sub.php?id="+kategori);

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });



            if(result == false)
                Toast.makeText(SearchActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }


    class KOTAAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SearchActivity.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldkotalist = new ArrayList<String>();
                idkotalist = new ArrayList<String>();


//                worldkotalist.add("ALL");
//                idkotalist.add("0");


                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("kota");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idkotalist.add(object.getString("id"));
                        worldkotalist.add(object.getString("nama"));
                        //idlist.add(object.getString("nama"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            kota = idkotalist.get(0);
            namakota = worldkotalist.get(0);
            Spinner mySpinner = (Spinner) findViewById(R.id.kota_spinner);


            // Spinner adapter
            mySpinner
                    .setAdapter(new ArrayAdapter<String>(SearchActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            worldkotalist));



            // Spinner on item click listener
            mySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            // TODO Auto-generated method stub
                            // Locate the textviews in activity_main.xml
                            System.out.println("ID KATEGORI"+idkotalist.get(position));

                            kota = idkotalist.get(position);
                            namakota = worldkotalist.get(position);





                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });



            if(result == false)
                Toast.makeText(SearchActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }





    class SUBAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SearchActivity.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldsublist = new ArrayList<String>();
                idsublist = new ArrayList<String>();


//                worldsublist.add("ALL");
//                idsublist.add("0");


                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("sub");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idsublist.add(object.getString("id"));
                        worldsublist.add(object.getString("nama"));
                        //idlist.add(object.getString("nama"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            if(idsublist.isEmpty()){
                Toast.makeText(SearchActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();
                Spinner mySpinner = (Spinner) findViewById(R.id.sub_spinner);
                mySpinner.setVisibility(View.GONE);
            }else{

                sub = idsublist.get(0);
                namasub = worldsublist.get(0);
                Spinner mySpinner = (Spinner) findViewById(R.id.sub_spinner);

                // Spinner adapter
                mySpinner
                        .setAdapter(new ArrayAdapter<String>(SearchActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                worldsublist));



                // Spinner on item click listener
                mySpinner
                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int position, long arg3) {
                                // TODO Auto-generated method stub
                                // Locate the textviews in activity_main.xml
                                System.out.println("ID KATEGORI"+idsublist.get(position));

                                sub = idsublist.get(position);
                                namasub = worldsublist.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                            }
                        });

                mySpinner.setVisibility(View.VISIBLE);

            }





            if(result == false)
                Toast.makeText(SearchActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }
}
