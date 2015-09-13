package br.com.qpainformatica.materialbeer;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.qpainformatica.materialbeer.entity.Beer;

public class DetailActivity extends AppCompatActivity {

    private static final int SCALE_DELAY = 30;

    private LinearLayout mRowContainer;
    private CoordinatorLayout mCoordinatorLayout;
    private ImageView bigImage;
    Dialog settingsDialog;

    private Beer mBeer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.container);
        mRowContainer = (LinearLayout) findViewById(R.id.row_container);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.onBackPressed();
            }
        });

        // Fab Button
//        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_normal);
//        floatingActionButton.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_file_upload).color(Color.WHITE).actionBar());
//        floatingActionButton.setOnClickListener(fabClickListener);

        //getWindow().getEnterTransition().removeListener(this);

        for (int i = 1; i < mRowContainer.getChildCount(); i++) {
            View rowView = mRowContainer.getChildAt(i);
            rowView.animate().setStartDelay(100 + i * SCALE_DELAY).scaleX(1).scaleY(1);
        }


        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            mBeer = new Beer();
            mBeer.setNome(intent.getStringExtra("nome"));
            mBeer.setEstilo(intent.getStringExtra("estilo"));
            mBeer.setCor(intent.getStringExtra("cor"));
            mBeer.setPais(intent.getStringExtra("pais"));
            mBeer.setImagem(intent.getStringExtra("imagem"));
            mBeer.setPreco(intent.getDoubleExtra("preco",0.0));


        }



        if (mBeer != null) {
            toolbar.setLogo(mBeer.getIcon());
            toolbar.setTitle(mBeer.getNome());

            View view = mRowContainer.findViewById(R.id.row_name);
            fillRow(view, "Nome", mBeer.getNome());
            // comentado pois vai carregar do cache
            //Picasso.with(this).load(mBeer.getImagem()).into((ImageView) view.findViewById(R.id.appIcon));

            ImageView beerImageView = (ImageView)view.findViewById(R.id.appIcon);
            beerImageView.setImageDrawable(new BitmapDrawable(getResources(), loadBitmap(mBeer.getImagem().substring(40,mBeer.getImagem().length()))));


            beerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    settingsDialog = new Dialog(DetailActivity.this);
                    settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.detail_image
                            , null));
                    bigImage = (ImageView) settingsDialog.findViewById(R.id.image_big);
                    bigImage.setImageDrawable(new BitmapDrawable(getResources(), loadBitmap(mBeer.getImagem().substring(40, mBeer.getImagem().length()))));
                    settingsDialog.show();

                }
            });

            view = mRowContainer.findViewById(R.id.row_package_name);
            fillRow(view, "Estilo", mBeer.getEstilo());

            view = mRowContainer.findViewById(R.id.row_activity);
            fillRow(view, "Cor", mBeer.getCor());

            view = mRowContainer.findViewById(R.id.row_component_info);
            fillRow(view, "Pais", mBeer.getPais());

            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            view = mRowContainer.findViewById(R.id.row_version);
            try {
                fillRow(view, "Preço", currency.format(mBeer.getPreco()));
            }catch (NumberFormatException e){
                Log.e("CONVERSAO",e.getMessage());
            }

        }
    }

    public void dismissListener(View view){
        if(settingsDialog !=null){
            settingsDialog.dismiss();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable("beer", mBeer.getNome());
        super.onSaveInstanceState(outState);
    }

    /**
     * a sample onClickListener for the upload view
     */
//    View.OnClickListener fabClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            UploadHelper.getInstance(DetailActivity.this, null).upload(mBeer);
//        }
//    };

    /**
     * fill the rows with some information
     *
     * @param view
     * @param title
     * @param description
     */
    public void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setText(description);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Beer", description);
                clipboard.setPrimaryClip(clip);

                Snackbar.make(mCoordinatorLayout, "Copiado " + title, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * animate the views if we close the activity
     */
    @Override
    public void onBackPressed() {
        for (int i = mRowContainer.getChildCount() - 1; i > 0; i--) {
            View rowView = mRowContainer.getChildAt(i);
            ViewPropertyAnimator propertyAnimator = rowView.animate().setStartDelay((mRowContainer.getChildCount() - 1 - i) * SCALE_DELAY)
                    .scaleX(0).scaleY(0);

            propertyAnimator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }
}
