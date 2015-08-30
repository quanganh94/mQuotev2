package com.example.image.mquotev2;

import android.content.Context;
import android.graphics.Typeface;

import java.util.LinkedList;

/**
 * Created by ZoZy on 1/29/2015.
 */
public class CustomFont {
    LinkedList<Font> fonts = new LinkedList();

    CustomFont(Context context) {
        Typeface cf;
        String name;
        cf = Typeface.createFromAsset(context.getAssets(), "helvetica-neue-regular.ttf");
        name = "Helvetica";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Light.ttf");
        name = "SourceSansPro";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "uvf-funkydori.otf");
        name = "FunkyDori";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "uvf-slimtony.ttf");
        name = "SlimTony";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "uvfnellyscriptflourish.ttf");
        name = "Nelly Script Flourish";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-champion-script-pro.ttf");
        name = "Champion Script";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-lanvanderia-regular.ttf");
        name = "Lanvanderia";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-leaguegothic-regular.ttf");
        name = "League Gothic";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-sacramento-regular.ttf");
        name = "Sacramento";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-semilla-script.ttf");
        name = "Semilla";
        fonts.add(new Font(cf, name));
        cf = Typeface.createFromAsset(context.getAssets(), "vnf-sofia-regular.ttf");
        name = "Sofia";
        fonts.add(new Font(cf, name));
    }

    public Font find_font(String t){
        for(Font f:fonts){
            if(t.equals(f.name)){
                return f;
            }
        }
        return null;
    }
    public String[] List_Font(){
        String[] temp = new String[fonts.size()];
        int count=0;
        for(Font f: fonts){
            temp[count++]=f.name;
        }
        return temp;
    }

}
