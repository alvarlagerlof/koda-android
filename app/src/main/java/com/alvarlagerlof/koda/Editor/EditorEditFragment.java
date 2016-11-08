package com.alvarlagerlof.koda.Editor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.ShaderEditor;

/**
 * Created by alvar on 2016-11-08.
 */

public class EditorEditFragment extends Fragment {

    private int fontSize = 15;
    private String code;

    private ShaderEditor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editor_edit, container, false);
        editor = (ShaderEditor) view.findViewById(R.id.editor_editext);

        editor.setText(code);
        editor.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"SourceCodePro-Regular.ttf"));

        return view;
    }


    public void changeFontSize(int change) {
        if (fontSize + change > 5 && fontSize - change < 40) {
            fontSize += change;
            editor.setTextSize(fontSize);
        }
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return editor.getCleanText();
    }




}
