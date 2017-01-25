package com.example.lang8yoo;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;


public class MainActivity extends Activity  {
	// extends Activity instead of AppCompatActivity, and add the
	// requestWindowFeature(Window.FEATURE_NO_TITLE); line disable the title bar 

	TextView currentPageDisplay;
	EditText pageSearchLine;
	Button pageSearchButton;

	TextView textView;
	
	Button previousButton;
	Button randomButton;
	Button nextButton;

	public int current_page_index;
	public int entries_total;
	public String lastReadEntryIndex_FILE = "lastReadEntryIndex.txt";
	public int last_read_entry_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        currentPageDisplay = (TextView) findViewById(R.id.currentPageDisplay);
        pageSearchLine = (EditText) findViewById(R.id.pageSearchLine);
        pageSearchButton = (Button) findViewById(R.id.pageSearchButton);

        textView = (TextView) findViewById(R.id.textView);

        previousButton = (Button) findViewById(R.id.previousButton);
        randomButton = (Button) findViewById(R.id.randomButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        try {
            entries_total = getAssets().list("yamasvEntries").length;
	    	BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(lastReadEntryIndex_FILE)));
	    	last_read_entry_index = Integer.parseInt(br.readLine());
	    	br.close();		
	        updateScreenWith(last_read_entry_index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void randomPageDisplay(View view) throws IOException {
    	int random_page_index = new Random().nextInt(entries_total+1)+1;
    	updateScreenWith(random_page_index);

    }


    public void nextPageDisplay(View view) throws IOException {
    	int next_page_index = current_page_index < entries_total ? current_page_index + 1 : current_page_index;
    	updateScreenWith(next_page_index);
    }


    public void previousPageDisplay(View view) throws IOException {
    	int previous_page_index = current_page_index > 1 ? current_page_index - 1 : current_page_index;
    	updateScreenWith(previous_page_index);
    }


    public void searchPage(View view) throws IOException {
    	if (TextUtils.isDigitsOnly(pageSearchLine.getText())) {
	    	int page_to_search_index = Integer.parseInt(pageSearchLine.getText().toString());
    		if (1 <= page_to_search_index && page_to_search_index <= entries_total){
		    	updateScreenWith(page_to_search_index);
    		}
    	}
    }


    public void updateScreenWith(int page_to_display_index) throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("yamasvEntries/" + page_to_display_index + ".txt")));
    	String page_to_display_text = "";
    	String entry_line;
    	while ((entry_line = br.readLine()) != null ){
    		page_to_display_text += entry_line + "\n";
    	}
    	br.close();		

        textView.setTextSize(22);
        textView.setText(page_to_display_text);
        current_page_index = page_to_display_index;
        currentPageDisplay.setText(current_page_index+"/"+entries_total);

        // Android can't write the assets folder read-only files, instead it create/write file on internal storage with openFileOutput
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(lastReadEntryIndex_FILE, Context.MODE_PRIVATE);
            outputStream.write(Integer.toString(current_page_index).toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
