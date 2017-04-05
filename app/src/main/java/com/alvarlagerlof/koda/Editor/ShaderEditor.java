package com.alvarlagerlof.koda.Editor;

/**
 * Created by alvar on 2016-07-07.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import android.os.Handler;


import com.alvarlagerlof.koda.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderEditor extends EditText {

    public interface OnTextChangedListener {
        public void onTextChanged( String text );
    }

    private static final Pattern PATTERN_LINE = Pattern.compile(
            ".*\\n" );

    private static final Pattern PATTERN_NUMBERS = Pattern.compile(
            "\\b(\\d*[.]?\\d+)\\b" );

    private static final Pattern PATTERN_PREPROCESSOR = Pattern.compile(
            "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|"+
                    "#error|#pragma|#extension|#version|#line)\\b",
            Pattern.MULTILINE );

    private static final Pattern PATTERN_KEYWORDS = Pattern.compile(
            "\\b(abstract|arguments|boolean|break|byte" +
                    "|case|catch|char|class|const" +
                    "|continue|debugger|default|delete|do" +
                    "|double|else|enum|eval|export" +
                    "|extends|false|final|finally|float" +
                    "|for|function|goto|if|implements" +
                    "|import|in|instanceof|int|interface" +
                    "|let|long|native|new|null" +
                    "|package|private|protected|public|return" +
                    "|short|static|super|switch|synchronized" +
                    "|this|throw|throws|transient|true" +
                    "|try|typeof|var|void|volatile|while|with|yield)\\b" );


    private static final Pattern PATTERN_VARIABLE = Pattern.compile(
            "(var [A-Za-z'-]+)|(function [A-Za-z'-]+)");

    private static final Pattern PATTERN_VARIABLE_CONTENT = Pattern.compile(
            "([\"'])(?:(?=(\\\\?))\\2.)*?\\1" );

    private static final Pattern PATTERN_SCRIPT_TAG_START = Pattern.compile(
            "^.*(<script)." );

    private static final Pattern PATTERN_SCRIPT_TAG_START_END = Pattern.compile(
            "[^\">]" );

    private static final Pattern PATTERN_SCRIPT_TAG_END = Pattern.compile(
            "^.*(<\\/script>)." );

    private static final Pattern PATTERN_COMMENTS = Pattern.compile(
            "/\\*(?:.|[\\n\\r])*?\\*/|//.*" );

    private static final Pattern PATTERN_TRAILING_WHITE_SPACE = Pattern.compile(
            "[\\t ]+$",
            Pattern.MULTILINE );

    private static final Pattern PATTERN_INSERT_UNIFORM = Pattern.compile(
            "\\b(uniform[a-zA-Z0-9_ \t;\\[\\]\r\n]+[\r\n])\\b",
            Pattern.MULTILINE );

    private static final Pattern PATTERN_ENDIF = Pattern.compile(
            "(#endif)\\b" );

    private final Handler updateHandler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    Editable e = getText();

                    if (onTextChangedListener != null )
                        onTextChangedListener.onTextChanged(e.toString());

                    highlightWithoutChange(e);
                }
            };

    private OnTextChangedListener onTextChangedListener;
    private int updateDelay = 1000;
    private int errorLine = 0;
    private boolean dirty = false;
    private boolean modified = true;
    private int colorError;
    private int colorNumber;
    private int colorKeyword;
    private int colorComment;
    private int colorVariable;
    private int colorVariableContent;
    private int tabWidthInCharacters = 0;
    private int tabWidth = 2;

    public ShaderEditor( Context context ) {
        super( context );
        init( context );
    }

    public ShaderEditor( Context context, AttributeSet attrs ) {
        super( context, attrs );
        init( context );
    }

    public void setOnTextChangedListener( OnTextChangedListener listener ) {
        onTextChangedListener = listener;
    }

    public void setUpdateDelay( int ms ) {
        updateDelay = ms;
    }

    public void setTabWidth( int characters ) {
        if (tabWidthInCharacters == characters )
            return;

        tabWidthInCharacters = characters;
        tabWidth = Math.round(getPaint().measureText("m") * characters );
    }

    public void setErrorLine( int line ) {
        errorLine = line;
        refresh();
    }

    public boolean isModified() {
        return dirty;
    }

    public void setTextHighlighted( CharSequence text ) {
        if( text == null )
            text = "";

        cancelUpdate();

        errorLine = 0;
        dirty = false;

        modified = false;
        setText( highlight( new SpannableStringBuilder( text ) ) );
        modified = true;

        if( onTextChangedListener != null )
            onTextChangedListener.onTextChanged( text.toString() );
    }

    public String getCleanText() {
        return PATTERN_TRAILING_WHITE_SPACE
                .matcher( getText() )
                .replaceAll( "" );
    }

    public void refresh() {
        highlightWithoutChange( getText() );
    }

    public void insertTab() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        getText().replace(
                Math.min( start, end ),
                Math.max( start, end ),
                "\t",
                0,
                1 );
    }

    public void addUniform( String statement ) {
        if( statement == null )
            return;

        Editable e = getText();
        Matcher m = PATTERN_INSERT_UNIFORM.matcher( e );
        int start = 0;

        if( m.find() )
            start = Math.max( 0, m.end()-1 );
        else
        {
            // projects_add an empty line between the last #endif
            // and the now following uniform
            if( (start = endIndexOfLastEndIf( e )) > -1 )
                statement = "\n"+statement;

            // move index past line break or to the start
            // of the text when no #endif was found
            ++start;
        }

        e.insert( start, statement+";\n" );
    }

    private int endIndexOfLastEndIf( Editable e ) {
        Matcher m = PATTERN_ENDIF.matcher( e );
        int idx = -1;

        while( m.find() )
            idx = m.end();

        return idx;
    }

    private void init( Context context ) {
        setHorizontallyScrolling( true );

        setFilters( new InputFilter[]{
                new InputFilter()
                {
                    @Override
                    public CharSequence filter(
                            CharSequence source,
                            int start,
                            int end,
                            Spanned dest,
                            int dstart,
                            int dend )
                    {
                        if( modified &&
                                end-start == 1 &&
                                start < source.length() &&
                                dstart < dest.length() )
                        {
                            char c = source.charAt( start );

                            if( c == '\n' )
                                return autoIndent(
                                        source,
                                        dest,
                                        dstart,
                                        dend );
                        }

                        return source;
                    }
                } } );

        addTextChangedListener(
                new TextWatcher()
                {
                    private int start = 0;
                    private int count = 0;

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count )
                    {
                        this.start = start;
                        this.count = count;
                    }

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after )
                    {
                    }

                    @Override
                    public void afterTextChanged( Editable e )
                    {
                        cancelUpdate();
                        convertTabs( e, start, count );

                        if( !modified )
                            return;

                        dirty = true;
                        updateHandler.postDelayed(
                                updateRunnable,
                                updateDelay );
                    }
                } );

        setSyntaxColors( context );
        setUpdateDelay(30);
        setTabWidth(tabWidth);
    }

    private void setSyntaxColors( Context context ) {
        colorError = ContextCompat.getColor(
                context,
                R.color.syntax_error );
        colorNumber = ContextCompat.getColor(
                context,
                R.color.syntax_number );
        colorKeyword = ContextCompat.getColor(
                context,
                R.color.syntax_keyword );

        colorComment = ContextCompat.getColor(
                context,
                R.color.syntax_comment );

        colorVariable = ContextCompat.getColor(
                context,
                R.color.syntax_varaiable );

        colorVariableContent = ContextCompat.getColor(
                context,
                R.color.syntax_varaiable_content );
    }

    private void cancelUpdate() {
        updateHandler.removeCallbacks( updateRunnable );
    }

    private void highlightWithoutChange( Editable e ) {
        modified = false;
        highlight( e );
        modified = true;
    }

    private Editable highlight( Editable e ) {
        try {
            // don't use e.clearSpans() because it will
            // remove too much
            clearSpans( e );

            if( e.length() == 0 )
                return e;

            if (errorLine > 0) {
                Matcher m = PATTERN_LINE.matcher( e );

                for (int n = errorLine;
                     n-- > 0 && m.find(); );

                e.setSpan(
                        new BackgroundColorSpan( colorError ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            }

            for( Matcher m = PATTERN_NUMBERS.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorNumber ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_PREPROCESSOR.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorKeyword ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );



            for( Matcher m = PATTERN_COMMENTS.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorComment ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );


            for( Matcher m = PATTERN_VARIABLE.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorVariable ),
                        m.start() + 4,
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_VARIABLE_CONTENT.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorVariableContent ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_KEYWORDS.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorKeyword ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_SCRIPT_TAG_START.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorNumber ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_SCRIPT_TAG_START_END.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorNumber ),
                        m.start() +1,
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = PATTERN_SCRIPT_TAG_END.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( colorNumber ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        }
        catch (IllegalStateException ex) {
            // raised by Matcher.start()/.end() when
            // no successful match has been made what
            // shouldn't ever happen because of find()
        }

        return e;
    }

    private void clearSpans( Editable e ) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class );

            for( int n = spans.length; n-- > 0; )
                e.removeSpan( spans[n] );
        }

        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    BackgroundColorSpan.class );

            for( int n = spans.length; n-- > 0; )
                e.removeSpan( spans[n] );
        }
    }

    private CharSequence autoIndent(
            CharSequence source,
            Spanned dest,
            int dstart,
            int dend )
    {
        String indent = "";
        int istart = dstart-1;
        int iend = -1;

        // find start of this line
        boolean dataBefore = false;
        int pt = 0;

        for ( ; istart > -1; --istart ) {
            char c = dest.charAt( istart );

            if ( c == '\n' )
                break;

            if ( c != ' ' &&
                    c != '\t' ) {

                if ( !dataBefore ) {
                    // indent always after those characters
                    if (c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=' )
                        --pt;

                    dataBefore = true;
                }

                // parenthesis counter
                if ( c == '(' )
                    --pt;
                else if ( c == ')' )
                    ++pt;
            }
        }

        // copy indent of this line into the next
        if ( istart > -1 ) {
            char charAtCursor = dest.charAt( dstart );

            for( iend = ++istart;
                 iend < dend;
                 ++iend ) {
                char c = dest.charAt( iend );

                // auto expand comments
                if( charAtCursor != '\n' &&
                        c == '/' &&
                        iend+1 < dend &&
                        dest.charAt( iend ) == c )
                {
                    iend += 2;
                    break;
                }

                if( c != ' ' &&
                        c != '\t' )
                    break;
            }

            indent += dest.subSequence( istart, iend );
        }

        // projects_add new indent
        if( pt < 0 )
            indent += "\t";

        // append white space of previous line and new indent
        return source+indent;
    }

    private void convertTabs( Editable e, int start, int count ) {
        if( tabWidth < 1 )
            return;

        String s = e.toString();

        for ( int stop = start+count;
             (start = s.indexOf( "\t", start )) > -1 && start < stop;
             ++start )
            e.setSpan(
                    new TabWidthSpan(),
                    start,
                    start+1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
    }

    private class TabWidthSpan extends ReplacementSpan {
        @Override
        public int getSize(
                Paint paint,
                CharSequence text,
                int start,
                int end,
                Paint.FontMetricsInt fm)
        {
            return tabWidth;
        }

        @Override
        public void draw(
                Canvas canvas,
                CharSequence text,
                int start,
                int end,
                float x,
                int top,
                int y,
                int bottom,
                Paint paint )
        {
        }
    }
}