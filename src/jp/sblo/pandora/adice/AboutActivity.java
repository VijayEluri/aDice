package jp.sblo.pandora.adice;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class AboutActivity extends Activity
{

    final static String ABOUT_PAGE = "file:///android_asset/about.html";
    public final static String EXTRA_URL = "URL";
    public final static String EXTRA_TITLE = "TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            SupportActionBar.addBackButton(this);
        }

        String url = ABOUT_PAGE;

        Intent it = getIntent();
        if ( it != null ){
            Bundle extras = it.getExtras();
            if ( extras !=null ){
                String iturl = extras.getString(EXTRA_URL);
                if ( iturl !=null ){
                    url = iturl;
                }
                String ittitle = extras.getString(EXTRA_TITLE);
                if ( ittitle !=null ){
                    setTitle( ittitle );
                }
            }
        }

        WebView webview = (WebView)findViewById(R.id.WebView01);
        webview.loadUrl( url );

        final JsCallbackObj jsobj = new JsCallbackObj();
        webview.addJavascriptInterface(jsobj, "jscallback");

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setFocusable(true);
        webview.setFocusableInTouchMode(true);

    }

    class JsCallbackObj
    {

        public JsCallbackObj()
        {
        }

        @JavascriptInterface
        public String getAboutStrings(String key)
        {
            if (key.equals("version")) {

                String versionName = "-.-";
                int versionCode = 0;
                PackageManager pm = getPackageManager();
                try {
                    PackageInfo info = null;
                    info = pm.getPackageInfo("jp.sblo.pandora.adice", 0);
                    versionName = info.versionName;
                    versionCode = info.versionCode;
                } catch (NameNotFoundException e) {
                }
                return "Ver. " + String.format("%s (%d)", versionName, versionCode);
            } else if (key.equals("description")) {
                return getResources().getString(R.string.description);
            } else if (key.equals("manual")) {
                return getResources().getString(R.string.manual);
            } else {
                return "";
            }
        }

        @JavascriptInterface
        public void throwIntentByUrl( String url , int requestcode )
        {
            if ( url!=null && url.length()>0 ){
                Intent it = new Intent( Intent.ACTION_VIEW , Uri.parse(url) );
                startActivityForResult(it, requestcode);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK && requestCode==1000 ){		// DL rerquest
            setResult( RESULT_OK , data );
            finish();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        if ( itemId == android.R.id.home ){
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
