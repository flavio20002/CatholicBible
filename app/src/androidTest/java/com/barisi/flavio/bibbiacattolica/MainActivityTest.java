package com.barisi.flavio.bibbiacattolica;


import android.content.Context;
import com.google.android.material.navigation.NavigationView;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void setUp() {
        Context c = mActivityTestRule.getActivity();
        c.getDatabasePath("database.db").delete();
    }

    @Test
    public void mainActivityTest() throws InterruptedException {
        ServiziDatabase db = new ServiziDatabase(mActivityTestRule.getActivity());
        db.cancellareVersettiPreferiti();

        Preferenze.reimpostaPreferenze(mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_lista_libri", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_lista_capitoli", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_leggi_capitolo", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_lista_segnalibri", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_disclaimer_letture", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_fast_scrolling", mActivityTestRule.getActivity());
        Preferenze.settaHintVisto("hint_sintesi_vocale", mActivityTestRule.getActivity());
        Preferenze.salvaLingua(mActivityTestRule.getActivity(), "it");
        Preferenze.salvaVersioneBibbia(mActivityTestRule.getActivity(), "cei2008");
        Preferenze.salvaTema(mActivityTestRule.getActivity(), "black");
        Preferenze.salvaModalitaNotte(mActivityTestRule.getActivity(), 1);
        Cache.clearCache();
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityTestRule.getActivity().recreate();
            }
        });

        Preferenze.settaHintVisto("intro", mActivityTestRule.getActivity());

        onView(allOf(withId(R.id.cardList), withParent(withId(R.id.myFrameLayout)),
                isDisplayed())).perform(actionOnItemAtPosition(0, click()));

        onView(allOf(withId(R.id.pager), withParent(withId(R.id.coordinatorLayout)), isDisplayed())).perform(swipeLeft());
        Thread.sleep(300);

        onView(allOf(withId(R.id.cardListCategory), isDisplayed())).perform(actionOnItemAtPosition(1, click()));
        int randomNum = (int) (Math.floor(Math.random() * 10) + 1);
        for (int i = 0; i < randomNum; i++) {
            onView(allOf(withId(R.id.pager), withParent(withId(R.id.coordinatorLayout)), isDisplayed())).perform(swipeLeft());
            Thread.sleep(300);
        }

        onView(allOf(withId(R.id.cardList), isDisplayed())).perform(actionOnItemAtPosition(0, click()));

        pressBack();

        onView(allOf(withId(R.id.cardList), isDisplayed())).perform(actionOnItemAtPosition(1, click()));

        for (int i = 0; i < randomNum; i++) {
            onView(allOf(withId(R.id.pager), withParent(withId(R.id.coordinatorLayout)), isDisplayed())).perform(swipeLeft());
            Thread.sleep(300);
        }

        try {
            onView(allOf(withId(R.id.action_favourite), withContentDescription("Salva nei segnalibri"), isDisplayed())).perform(click());
        } catch (Exception e) {
            System.out.println("Non trovo l'icona dei segnalibri");
        }

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Salva intervallo di versetti"), isDisplayed())).perform(click());
        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Mostra le note"), isDisplayed())).perform(click());

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Nascondi le note"), isDisplayed())).perform(click());

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Leggi ad alta voce"), isDisplayed())).perform(click());

        Thread.sleep(2000);

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Smetti di leggere ad alta voce"), isDisplayed())).perform(click());

        cambiaVersioneBibbia("Nova Vulgata (Latino)");
        cambiaVersioneBibbia("CEI 2008 (Italiano)");

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Confronta con altra versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Cambia versione di destra"), isDisplayed())).perform(click());
        onView(allOf(withText("Cattolica di Pubblico Dominio (Inglese)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Cambia versione di destra"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Aggiungi una terza versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Cattolica di Pubblico Dominio (Inglese)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Chiudi il confronto"), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Confronta con altra versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        pressBack();

        onView(allOf(withId(R.id.action_font), withContentDescription("Modifica il carattere"), isDisplayed())).perform(click());

        pressBack();
        pressBack();
        pressBack();

        onView(allOf(withContentDescription("Apri navigation drawer"),
                withParent(allOf(withId(R.id.toolbar), withParent(withId(R.id.appBarLayout)))),
                isDisplayed())).perform(click());

        onView(allOf(withId(R.id.design_menu_item_text), withText("Segnalibri"), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.cardList), isDisplayed())).perform(actionOnItemAtPosition(0, click()));

        pressBack();

        onView(allOf(withId(R.id.action_cancella_segnalibri),
                withContentDescription("Cancella tutti i segnalibri"), isDisplayed())).perform(click());

        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(click());

        pressBack();

        onView(withId(R.id.cardList))
                .perform(scrollToPosition(4));

        onView(allOf(withId(R.id.aggiorna), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.cardList), withParent(withId(R.id.myFrameLayout)),
                isDisplayed())).perform(actionOnItemAtPosition(2, click()));

        for (int i = 0; i < randomNum; i++) {
            onView(allOf(withId(R.id.pager), withParent(withId(R.id.coordinatorLayout)), isDisplayed())).perform(swipeLeft());
            Thread.sleep(300);
        }

        cambiaVersioneBibbia("Nova Vulgata (Latino)");
        cambiaVersioneBibbia("CEI 2008 (Italiano)");

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Confronta con altra versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Cambia versione di destra"), isDisplayed())).perform(click());
        onView(allOf(withText("Cattolica di Pubblico Dominio (Inglese)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Cambia versione di destra"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Aggiungi una terza versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Cattolica di Pubblico Dominio (Inglese)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Chiudi il confronto"), isDisplayed())).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(allOf(withId(R.id.title), withText("Confronta con altra versione"), isDisplayed())).perform(click());
        onView(allOf(withText("Nova Vulgata (Latino)"), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
        pressBack();

        onView(allOf(withId(R.id.action_font), withContentDescription("Modifica il carattere"), isDisplayed())).perform(click());

        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Apri Lettura nel contesto"), isDisplayed())).perform(click());

        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(click());

        pressBack();
        pressBack();

        Preferenze.salvaLingua(mActivityTestRule.getActivity(), "en");
        Cache.clearCache();
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityTestRule.getActivity().recreate();
            }
        });

        onView(allOf(withContentDescription("Open navigation drawer"),
                withParent(allOf(withId(R.id.toolbar),
                        withParent(withId(R.id.appBarLayout)))),
                isDisplayed())).perform(click());

        onView(isAssignableFrom(NavigationView.class)).perform(swipeUp());

        onView(allOf(withId(R.id.design_menu_item_text), withText("Settings"), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(1, click()));

        onView(allOf(withId(android.R.id.text1), withText("Dark"),
                childAtPosition(
                        allOf(withId(R.id.select_dialog_listview),
                                withParent(withId(R.id.contentPanel))),
                        1),
                isDisplayed())).perform(click());

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(4, click()));

        onView(allOf(withId(android.R.id.text1), withText("CEI 2008 (Italian)"))).perform(click());

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(5, click()));

        onView(allOf(withId(android.R.id.text1), withText("Italian"),
                childAtPosition(
                        allOf(withId(R.id.select_dialog_listview),
                                withParent(withId(R.id.contentPanel))),
                        0),
                isDisplayed())).perform(click());

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(6, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(7, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(8, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(9, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(11, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(16, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(17, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(18, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(19, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(20, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(21, click()));

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(22, click()));

        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());

        onView(allOf(withId(R.id.list),
                withParent(withClassName(is("android.widget.FrameLayout"))),
                isDisplayed())).perform(actionOnItemAtPosition(23, click()));

        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void cambiaVersioneBibbia(String nome) {
        onView(allOf(withContentDescription("Altre opzioni"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.title), withText("Cambia versione della Bibbia"), isDisplayed())).perform(click());
        onView(allOf(withText(nome), withParent(allOf(withId(R.id.select_dialog_listview),
                withParent(withId(R.id.contentPanel)))), isDisplayed())).perform(click());
    }

}
