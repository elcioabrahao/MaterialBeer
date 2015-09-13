package br.com.qpainformatica.materialbeer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.qpainformatica.materialbeer.adapter.BeerAdapter;
import br.com.qpainformatica.materialbeer.entity.Beer;
import br.com.qpainformatica.materialbeer.itemanimator.CustomItemAnimator;


public class MainActivity extends AppCompatActivity {
    private static final int DRAWER_ITEM_SWITCH = 1;
    private static final int DRAWER_ITEM_USJT = 10;
    private static final int DRAWER_ITEM_SOBRE = 20;

    private List<Beer> beerList = new ArrayList<Beer>();

    private Drawer drawer;

    private BeerAdapter mAdapter;
    private FloatingActionButton mFabButton;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout mCoordinatorLayout;


    private String estilo="";
    private String cor = "";
    private String pais="";
    boolean todasSelecionadas = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbar);

        mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordlayout1);



        drawer = new DrawerBuilder(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_usjt)
                                .withIdentifier(DRAWER_ITEM_USJT)
                                .withIcon(FontAwesome.Icon.faw_github)
                                .withCheckable(false)
                ).addDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_sobre)
                                .withIdentifier(DRAWER_ITEM_SOBRE)
                                .withIcon(FontAwesome.Icon.faw_university)
                                .withCheckable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == DRAWER_ITEM_USJT) {
                            new LibsBuilder()
                                    .withFields(R.string.class.getFields())
                                    .withVersionShown(true)
                                    .withLicenseShown(true)
                                    .withActivityTitle(getString(R.string.drawer_usjt))
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                    .start(MainActivity.this);
                        }else  if (drawerItem.getIdentifier() == DRAWER_ITEM_SOBRE) {

                            Intent intent = new Intent(getApplicationContext(),SobreActivity.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                })
                .withSelectedItem(-1)
                .withSavedInstance(savedInstanceState)
                .build();

        // Handle ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Fab Button
        mFabButton = (FloatingActionButton) findViewById(R.id.fab_normal);
        mFabButton.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.WHITE).actionBar());
        mFabButton.setOnClickListener(fabClickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        //mRecyclerView.setItemAnimator(new ReboundItemAnimator());

        mAdapter = new BeerAdapter(new ArrayList<Beer>(), R.layout.row_application, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);


        new InitializeApplicationsTask().execute();


        //show progress
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * onDestroy make sure we stop the upload
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * sample onClickListener with an AsyncTask as action
     */
    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(view.getContext(), BeerChooserActivity.class);

            startActivityForResult(intent, 1);

        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if (resultCode == 1) {
                    Bundle res = data.getExtras();
                    estilo = res.getString("estilo");
                    cor = res.getString("cor");
                    pais = res.getString("pais");
                    String todos = estilo+cor+pais;
                    if(!todos.equals("")) {
                        new InitializeApplicationsTask().execute();
                        //mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }else if(!todasSelecionadas){
                        new InitializeApplicationsTask().execute();
                        //mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                }
                break;
        }
    }

    /**
     * helper class to start the new detailActivity animated
     *

     */
    public void animateActivity(Beer beer, View appIcon) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("nome", beer.getNome());
        i.putExtra("estilo", beer.getEstilo());
        i.putExtra("cor", beer.getCor());
        i.putExtra("pais", beer.getPais());
        i.putExtra("imagem", beer.getImagem());
        i.putExtra("preco", beer.getPreco());

        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create((View) mFabButton, "fab"), Pair.create(appIcon, "appIcon"));
        startActivity(i, transitionActivityOptions.toBundle());
    }


    private void saveBitmap(Bitmap b, String imageName){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/beer_images");
        myDir.mkdirs();

        String fname = imageName;
        File file= new File(myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG,100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            Log.e("ERRO",e.getMessage());
        }


    }

    private Bitmap loadBitmap(String imageName){

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/beer_images");
        Bitmap bitmap=null;
        File f= new File(myDir, imageName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        }

      return bitmap;


    }

    private boolean isCached(String imageName){

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/beer_images");
        File file= new File(myDir, imageName);
        return file.exists();
    }


    /**
     * A simple AsyncTask to load the list of applications and display them
     */
    private class InitializeApplicationsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mAdapter.clearBeers();
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            beerList.clear();

            //Query the applications
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            OkHttpClient client = new OkHttpClient();

            String url2="http://jbossews-cerveja.rhcloud.com/selecao.json?estilo=" + estilo + "&pais=" + pais + "&cor=" + cor;
            Log.e("OKHTTP",url2);

            String result="";
            try {
                Request request = new Request.Builder()
                        .url(url2)
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("OKHTTP",result);
            }catch(Exception e){
                Log.e("OKHTTP",e.getMessage());
            }

            try {


                JSONArray objs = new JSONArray(result);
                Beer beer;
                for(int i=0;i<objs.length();i++){
                    JSONObject o = objs.getJSONObject(i);
                    beer = new Beer();
                    beer.setNome(o.getString("nome"));
                    beer.setPais(o.getString("pais"));
                    beer.setCor(o.getString("cor"));
                    beer.setEstilo(o.getString("estilo"));
                    beer.setPreco(o.getDouble("preco"));
                    beer.setImagem(o.getString("imagem"));
                    beerList.add(beer);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String todas=estilo+cor+pais;
            todasSelecionadas = todas.equals("");


            Collections.sort(beerList);

            try
            {
            Bitmap b;
                String name = "";

            for (Beer beer : beerList) {

             name = beer.getImagem().substring(40,beer.getImagem().length());
             if(!isCached(name) ){
                 URL url = new URL(beer.getImagem());
                 InputStream is = new BufferedInputStream(url.openStream());
                 b = BitmapFactory.decodeStream(is);
                 saveBitmap(b,name);
             }else{
                 Log.d("BEER","Cached -> True !");
                 b = loadBitmap(name);
             }
                beer.setIcon(new BitmapDrawable(getResources(), b));
            }
            } catch(Exception e){
                Log.e("BITMAP",e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute (Void result) {
            //handle visibility
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);


            if(beerList.size()==0){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Beer", "Nenhum Item Localizado");
                clipboard.setPrimaryClip(clip);

                Snackbar.make(mCoordinatorLayout, "Nenhum item localizado", Snackbar.LENGTH_LONG).show();
            }

            //set data for list
            mAdapter.addBeers(beerList);
            //mSwipeRefreshLayout.setRefreshing(false);

            super.onPostExecute(result);
        }
    }
}
