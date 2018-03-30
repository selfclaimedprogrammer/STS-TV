package np.com.uashim.ststv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private DrawerLayout mDL;
    private ActionBarDrawerToggle mToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListner();

        TextView information_txt = findViewById(R.id.info_btn);
        information_txt.setText(Html.fromHtml(getString(R.string.info)));
        information_txt.setMovementMethod(LinkMovementMethod.getInstance());

        //Navigation Drawer Slider
        mDL =  findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDL,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDL.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button liveInd = findViewById(R.id.live_ind);
        liveInd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,LivePlayer.class);
                startActivity(i);

            }
        });

    }


    //Method to close or open drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.call: {
                String phone = "091417350";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;
            }
            case R.id.visit_website: {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.ststelevision.com/"));
                startActivity(i);
                break;
            }
            case R.id.visit_fb: {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
            }
            case R.id.send_news: {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "news@ststelevision.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "News for STS TV");
                startActivity(Intent.createChooser(emailIntent, null));
                break;
            }
            case R.id.share: {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Download STS TV app : http://www.ststelevision.com/download/";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share STS TV App"));

                break;
            }
            case R.id.advert: {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://ststelevision.com/advertisement/"));
                startActivity(i);
                break;
            }

        }


        return true;
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawermenu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Facebook Visit Page Function
    private static String FACEBOOK_URL = "https://www.facebook.com/STSTELEVISION/";
    private static String FACEBOOK_PAGE_ID = "STSTELEVISION";

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return ("fb://facewebmodal/f?href=" + FACEBOOK_URL);
            } else { //older versions of fb app
                return ("fb://page/" + FACEBOOK_PAGE_ID);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return (FACEBOOK_URL); //normal web url
        }
    }


    }

