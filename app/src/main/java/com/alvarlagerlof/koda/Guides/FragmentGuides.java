package com.alvarlagerlof.koda.Guides;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by alvar on 2016-07-02.
 */
public class FragmentGuides extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.guides_fragment, container, false);

        ArrayList<YouTubeObject> list = new ArrayList<>();
        list.add(new YouTubeObject("https://i.ytimg.com/vi/mvLdugZHHL8/hqdefault.jpg?custom=true&w=640&h=369&jpg444=true&jpgq=90&sp=68&sigh=DkqvRBnUGz3ZtAxYrBjJswZGKHw",
                                   "Video 1 - Enkel grafik",
                                   "https://www.youtube.com/watch?v=mvLdugZHHL8&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN"));

        list.add(new YouTubeObject("https://i.ytimg.com/vi/JBxCI5whhZ8/hqdefault.jpg?custom=true&w=640&h=369&jpg444=true&jpgq=90&sp=68&sigh=s_NC_pSJvNwRxc2aMIDx8rUPNFg",
                                   "Video 2 - Variabler",
                                   "https://www.youtube.com/watch?v=JBxCI5whhZ8&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN&index=2"));

        list.add(new YouTubeObject("https://i.ytimg.com/vi/_ZpyitroevU/hqdefault.jpg?custom=true&w=640&h=369&jpg444=true&jpgq=90&sp=68&sigh=_U-kmyXJ52MQo21up1VhW-ni5yo",
                                   "Video 3 - Animering",
                                   "https://www.youtube.com/watch?v=_ZpyitroevU&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN&index=3"));

        list.add(new YouTubeObject("https://i.ytimg.com/vi/vMAu17L6WUY/hqdefault.jpg?custom=true&w=640&h=369&jpg444=true&jpgq=90&sp=68&sigh=63JPdZcG8LQsyFxFeR23yExH1YY",
                                   "Video 4 - Interaktivitet",
                                   "https://www.youtube.com/watch?v=vMAu17L6WUY&index=4&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN"));

        list.add(new YouTubeObject("https://i.ytimg.com/vi/W4jzW1165Sc/hqdefault.jpg?custom=true&w=640&h=369&jpg444=true&jpgq=90&sp=68&sigh=xsVr8h5eTslzP1zsGgO-UsJImNA",
                                   "Video 5 - Första spelet",
                                   "https://www.youtube.com/watch?v=W4jzW1165Sc&index=5&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN"));

        list.add(new YouTubeObject("https://i.ytimg.com/vi/88gEl9sDRnI/hqdefault.jpg?custom=true&w=1640&h=369&jpg444=true&jpgq=90&sp=68&sigh=XJvW1XzQldOkZEZJ4TtUF165yO0",
                                   "Video 6 - Ett episkt Rymdspel",
                                   "https://www.youtube.com/watch?v=88gEl9sDRnI&index=6&list=PLacLTA7npkEaxlsKIL06aLqtLVKIKurRN"));

        list.add(new YouTubeObject("", "", ""));


        YouTubeAdapter youTubeAdapter = new YouTubeAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(youTubeAdapter);

        ImageView imageView = (ImageView) view.findViewById(R.id.book_image);
        TextView book_text = (TextView) view.findViewById(R.id.book_text);
        Button book_button = (Button) view.findViewById(R.id.book_button);

        Glide.with(getContext())
                .load("https://www.gleerups.se/magento/media/catalog/product/cache/3/image/166x/040ec09b1e35df139433887a97daa66f/4/0/40692320-o_166.jpg")
                .diskCacheStrategy( DiskCacheStrategy.RESULT)
                .into(imageView);


        book_text.setText(Html.fromHtml("<p><strong>Programmering för högstadiet</strong> är en grundbok i programmering för årskurs 7-9. Författarna har skapat ett läromedel som på ett enkelt och lustfyllt sätt hjälper dina elever att förstå och använda programkod. Eleverna skriver kod i programspråket JavaScript och övningarna ger omedelbara grafiska resultat. På den tillhörande hemsidan arbetar eleven i programmeringslaboratoriet. Det enda som krävs är en webbläsare.<br /><br />Boken utgår från enklare övningar, men innehåller även mer utmanande övningar i vilka eleven kan fördjupa sin kunskap. Övningarna är utformade så att eleverna lär sig programmering på ett roligt, intressant och användbart sätt.<br /><br />Programmering för högstadiet finns även i den engelska översättningen Start coding! I tillhörande Programmering för högstadiet Lärarmaterial får du som lärare hjälp med arbetsmetoder och förslag på lektionsplaneringar.</p>\n" +
                "<p><strong>Programmering för högstadiet</strong> passar dig som:<br /><br /></p>\n" +
                "<ul><li> söker ett läromedel som lär ut grunderna i programmering på ett enkelt sätt \n" +
                "</li><li> vill inspirera eleverna att vara kreativa och lösa problem \n" +
                "</li><li> vill att eleverna lär sig programmering med hjälp av ett webbaserat verktyg</li></ul><p>Författare till Programmering för högstadiet är systemadministratörerna Mikael Tylmad och Pontus Walck. Båda har med framgång undervisat i programmering både på högstadiet och gymnasiet.<br /></p>"));

        book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gleerups.se/40692320-product?category=7-9/programmering"));
                getContext().startActivity(browserIntent);
            }
        });

        return view;
    }

}
