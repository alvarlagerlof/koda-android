package com.alvarlagerlof.koda.Editor;

import android.os.AsyncTask;

/**
 * Created by alvar on 08/11/16.
 */

class EditorSave extends AsyncTask<Void, Void, String> {
    public interface EditorSaveListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded(String result);
    }

    private EditorSaveListener mListener;

    final public void setListener(EditorSaveListener listener) {
        mListener = listener;
    }

    @Override
    final protected String doInBackground(Void... progress) {
        // do stuff, common to both activities in here

        return null;
    }

    @Override
    final protected void onPreExecute() {
        // common stuff

        if (mListener != null)
            mListener.onPreExecuteConcluded();
    }

    @Override
    final protected void onPostExecute(String result) {
        // common stuff

        if (mListener != null)
            mListener.onPostExecuteConcluded(result);
    }
}