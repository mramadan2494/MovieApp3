package com.example.android.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MainFragment.Communicator {
    MainFragment mainFragment;
    DetailFragment detailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

        mainFragment=(MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment1);
        mainFragment.setCommunicator(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {

            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void respond(Movie movie) {

    detailFragment=(DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment2);

        if(detailFragment!=null && detailFragment.isVisible()){

             detailFragment.updateData(movie);

        }

        else{


                Intent intent=new Intent(this,MovieDetailActivity.class);
                intent.putExtra("movieClass",movie);
                startActivity(intent);
        }

    }


}
