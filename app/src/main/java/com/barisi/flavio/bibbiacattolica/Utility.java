package com.barisi.flavio.bibbiacattolica;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.database.DatabaseHelper;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.interfaces.BibbiaSelectedListener;
import com.barisi.flavio.bibbiacattolica.interfaces.NotaSegnalibroListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnCambiaLinguaListener;
import com.barisi.flavio.bibbiacattolica.model.Lingua;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("deprecation")
public class Utility {

    public static void showDialog(Context c,
                                  String title,
                                  String message,
                                  String positiveButton,
                                  String negativeButton,
                                  DialogInterface.OnClickListener positiveListener,
                                  DialogInterface.OnClickListener negativeListener,
                                  boolean cancelable) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(title)
                .setCancelable(cancelable)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveListener)
                .setNegativeButton(negativeButton, negativeListener)
                .create();
        dialog.show();
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

    public static void eseguiJavascript(WebView web, String script) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            //http://stackoverflow.com/questions/37564580/webview-evaluatejavascript-on-api-19-not-supported-on-android-4-3-and-earlier
            try {
                web.evaluateJavascript(script, null);
            } catch (Exception e1) {
                try {
                    web.loadUrl("javascript:" + script);
                } catch (Exception e2) {
                    Log.e("eseguiJavascript", e2.getMessage());
                }
            }
        } else {
            web.loadUrl("javascript:" + script);
        }
    }

    public static String rimuoviAccentiTestoGreco(Context c, String testo, String versioneBibbia) {
        //Fix per problema accenti in lingua greca
        if (versioneBibbia.equals("lxx")) {
            if (!Preferenze.ottieniMostraAccentiGreco(c)) {
                return Regex.rimuoviAccenti(testo);
            }
        }
        return testo;
    }

/*    public static int measureContentWidth(ListAdapter adapter, Context c) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(c);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        return maxWidth;
    }*/

    public static int adjustAlpha(int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /*public static int alterColor(int color, float factor) {
        int a = (color & (0xFF << 24)) >> 24;
        int r = (int) (((color & (0xFF << 16)) >> 16) * factor);
        int g = (int) (((color & (0xFF << 8)) >> 8) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return Color.argb(a, r, g, b);
    }*/

    private static void composeEmail(Context c, String[] addresses, String subject, String testoMail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, testoMail);
        if (intent.resolveActivity(c.getPackageManager()) != null) {
            c.startActivity(intent);
        }
    }

    public static void mostraDialogoVersioniBibbia(final Context c, String versioneCorrente, String[] versioniDaEscludere, final BibbiaSelectedListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.scegliVersioneBibbia));

        String[] i = Cache.getBibbieEntries(c, versioniDaEscludere);
        final String[] itemsCode = Cache.getBibbieEntriesValues(c, versioniDaEscludere);
        int index = Arrays.asList(itemsCode).indexOf(versioneCorrente);

        builder.setSingleChoiceItems(i, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String versioneSelezionata = itemsCode[which];
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    String espansione = DatabaseHelper.databaseAddOn(c, versioneSelezionata);
                    int minVersioneEspansione = DatabaseHelper.databaseMinVersioneEspansione(c, versioneSelezionata);
                    if (Regex.stringaNonVuota(espansione)) {
                        int espansioneInstalled = Inizializzazione.isAppInstalled(c, espansione, minVersioneEspansione);
                        if (espansioneInstalled == Costanti.ESPANSIONE_MANCANTE) {
                            Utility.mostraMessaggioAvvisoEspansione(c, espansione, false);
                            return;
                        } else if (espansioneInstalled == Costanti.ESPANSIONE_NON_AGGIORNATA) {
                            Utility.mostraMessaggioAvvisoEspansione(c, espansione, true);
                            return;
                        }
                    }
                    listener.bibbiaSelected(versioneSelezionata);
                } catch (Exception e) {
                    //
                }
            }
        });

        String negativeText = c.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void mostraDialogoLinguePreghiere(final Context c, String linguaCorrente, final OnCambiaLinguaListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.cambia_lingua));

        List<Lingua> lingue = new ServiziDatabase(c).listaLinguePreghiera();
        List<String> descrizioni = new ArrayList<>();
        final List<String> codici = new ArrayList<>();
        for (Lingua lingua : lingue) {
            descrizioni.add(lingua.getDes());
            codici.add(lingua.getCod());
        }
        int index = codici.indexOf(linguaCorrente);
        builder.setSingleChoiceItems(descrizioni.toArray(new String[0]), index, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String codice = codici.get(which);
                if (dialog != null) {
                    dialog.dismiss();
                }
                listener.cambiaLingua(codice);
            }
        });

        String negativeText = c.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void mostraDialogoContattaSviluppatore(final Context c) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.inviaEmail));

        String[] i = new String[]{c.getString(R.string.segnala_anomalia), c.getString(R.string.ringrazia)};
        builder.setSingleChoiceItems(i, 0, null);

        String positiveText = c.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (selectedPosition == 0) {
                    composeEmail(c, new String[]{"flvb86@gmail.com"}, c.getString(R.string.oggettoMail), c.getString(R.string.testoMail));
                } else {
                    composeEmail(c, new String[]{"flvb86@gmail.com"}, c.getString(R.string.oggettoMail), c.getString(R.string.testoMail2));
                }

            }
        });
        String negativeText = c.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void messaggioAvviso(Context c, int titolo, int testo, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titolo);
        builder.setMessage(testo);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", listener);
        alert.show();
    }

    public static void mostraDialogoOttieniTesto(Context c, View root, String testoDefault, final NotaSegnalibroListener listener) {
        if (c != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle(c.getString(R.string.segnalibro_nota));
            View viewInflated = LayoutInflater.from(c).inflate(R.layout.dialog_chiedi_testo, (ViewGroup) root, false);
            final EditText editText1 = (EditText) viewInflated.findViewById(R.id.editText1);
            editText1.setText(testoDefault);
            editText1.setSelection(editText1.getText().length());
            builder.setView(viewInflated);
            String positiveText = c.getString(android.R.string.ok);
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.noteInserted(editText1.getText().toString().trim());
                }
            });
            String negativeText = c.getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText, null);
            final AlertDialog dialog = builder.create();
            editText1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                        return true;
                    }
                    return false;
                }
            });
            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            dialog.show();
        }
    }

    public static void mostraMessaggioAvvisoEspansione(final Context c, final String espansione, boolean daAggiornare) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(daAggiornare ? R.string.bibbia_espansione_da_aggiornare : R.string.bibbia_espansione);
        builder.setMessage(daAggiornare ? R.string.bibbia_espansione_da_aggiornare_testo : R.string.bibbia_espansione_testo);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + espansione)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + espansione)));
                }
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, c.getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    private static void mostraSnackBar(Context c, CoordinatorLayout coordinatorLayoutView, String messaggio, View.OnClickListener listener, Snackbar.Callback callBack, int messaggioAction) {
        Snackbar snack = Snackbar.make(coordinatorLayoutView, messaggio, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.setActionTextColor(c.getResources().getColor(R.color.oro));
        if (listener != null) {
            snack.setAction(messaggioAction, listener);
        }
        if (callBack != null) {
            snack.setCallback(callBack);
        }
        snack.show();
    }

    public static void mostraSnackBar(Context c, CoordinatorLayout coordinatorLayoutView, String messaggio, View.OnClickListener listener, int messaggioAction) {
        mostraSnackBar(c, coordinatorLayoutView, messaggio, listener, null, messaggioAction);
    }

    public static void mostraSnackBar(Context c, CoordinatorLayout coordinatorLayoutView, String messaggio) {
        mostraSnackBar(c, coordinatorLayoutView, messaggio, null, null, -1);
    }

    /*public static String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }*/

    public static Long getAvailableInternalMemorySize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / 1024 / 1024;
        } catch (Exception e) {
            return null;
        }
    }

    /*public static Long getTotalInternalMemorySize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize / 1024 / 1024;
        } catch (Exception e) {
            return null;
        }
    }*/

    public static void impostaLingua(Context c) {
        String lingua = Preferenze.ottieniLingua(c);
        Configuration conf = c.getResources().getConfiguration();
        conf.locale = new Locale(lingua);
        c.getResources().updateConfiguration(conf, c.getResources().getDisplayMetrics());
    }

    @SuppressWarnings("unused")
    public static boolean isTablet(Context c) {
        return c.getResources().getBoolean(R.bool.is_tablet);
    }

    public static long getInstallDate(Context c) {
        try {
            return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).firstInstallTime;
        } catch (Exception e) {
            return Calendar.getInstance().getTimeInMillis();
        }
    }

    public static void aggiornaTitoloActionBar(AppCompatActivity activity, String title, String subtitle) {
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
                actionBar.setSubtitle(subtitle);
            }
        }
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void sfondoWebView(Context c, WebView vW) {
        if (c != null) {
            int modalitaNotte = Preferenze.ottieniModalitaNotte(c);
            if (modalitaNotte == 0) {
                vW.setBackgroundColor(c.getResources().getColor(R.color.sfondo_floating_material_light));
            } else if (modalitaNotte == 1) {
                vW.setBackgroundColor(c.getResources().getColor(R.color.nero));
            } else if (modalitaNotte == 2) {
                vW.setBackgroundColor(c.getResources().getColor(R.color.bottoneGiallo));
            } else if (modalitaNotte == 3) {
                vW.setBackgroundColor(c.getResources().getColor(R.color.sfondo_floating_material_dark));
            }
        }
    }

    public static void mostraToolBar(Activity activity) {
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null && toolbar.getParent() instanceof AppBarLayout) {
                ((AppBarLayout) toolbar.getParent()).setExpanded(true, false);
            }
        }
    }

    public static boolean isDebuggable(Context c) {
        boolean isDebug = ((c.getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        return isDebug;
    }

}
