package com.alvarlagerlof.koda.Editor;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

/**
 * Created by alvar on 2016-11-08.
 */

public class EditorEditFragment extends Fragment {

    private int fontSize = 15;
    private String code;

    private ShaderEditor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.editor_edit_fragment, container, false);
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


    public void chooseColor(android.content.Context context, int initColor) {

        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {}
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        editor.getText().insert(editor.getSelectionStart(), String.format("#%06X", 0xFFFFFF & selectedColor));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();

    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return editor.getCleanText();
    }




}
