package com.example.android.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

//
public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);



        Intent intent = getIntent();
        DetailFragment detailFragment=(DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment2);
        Movie movie = (Movie) intent.getSerializableExtra("movieClass");
        detailFragment.updateData(movie);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id==android.R.id.home){

            NavUtils.getParentActivityIntent(this);


        }


        return super.onOptionsItemSelected(item);
    }


}
