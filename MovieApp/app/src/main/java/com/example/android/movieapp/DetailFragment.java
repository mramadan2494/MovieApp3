package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.DataBase.DataBaseContract;
import com.example.android.movieapp.Model.Review;
import com.example.android.movieapp.Model.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
//////
/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    TextView date, overview, title, vote;
    ImageView imageView;
    ImageButton favorite;
    Movie movie;
    ArrayList<String> dataHeaders;
    HashMap<String, ArrayList<String>> dataItems;
    ExpandableListView expandList;
    MovieExtandableAdapter movieExtandableAdapter;
    ArrayList<String> trailersKeys;
    ArrayList<String>trailersNames;

    boolean isFavorite , hasTrailers;




    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);





         imageView = (ImageView) (rootView).findViewById(R.id.image_poster);

        date = (TextView) (rootView).findViewById(R.id.text_date);

        overview = (TextView) (rootView).findViewById(R.id.text_overview);

        title = (TextView) (rootView).findViewById(R.id.text_title);

        vote = (TextView) (rootView).findViewById(R.id.text_rate);


       favorite=(ImageButton)rootView.findViewById(R.id.favorite_button);
       expandList = (ExpandableListView) rootView.findViewById(R.id.expandableList_movie);
        dataHeaders = new ArrayList<String>();
        dataItems=new HashMap<String,ArrayList<String>>();

        movie=new Movie();
        trailersKeys=new ArrayList<>();
        trailersNames=new ArrayList<>();
        hasTrailers=false;


        if(savedInstanceState!=null
                &&savedInstanceState.getStringArrayList("headers")!=null
                &&savedInstanceState.getSerializable("items")!=null
                && savedInstanceState.getStringArrayList("trailers")!=null
                &&savedInstanceState.getStringArrayList("trailersNames")!=null
                && savedInstanceState.getSerializable("movie") !=null

                )


        {



            setHasOptionsMenu(true);
            dataHeaders=savedInstanceState.getStringArrayList("headers");
            dataItems= (HashMap<String, ArrayList<String>>) savedInstanceState.getSerializable("items");
            trailersKeys=savedInstanceState.getStringArrayList("trailers");
            trailersNames=savedInstanceState.getStringArrayList("trailersNames");
            isFavorite=savedInstanceState.getBoolean("isfavorite");
            hasTrailers=savedInstanceState.getBoolean("hastrailers");
            movie= (Movie) savedInstanceState.getSerializable("movie");
            date.setText(movie.getDate());
            overview.setText(movie.getOverview());
            title.setText(movie.getTitle());

            vote.setText(movie.getRate() + "/10");
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + movie.getImagePath()).into(imageView);
            favorite.setVisibility(View.VISIBLE);
           if( isFavorite)
               favorite.setImageResource(R.drawable.staron);
            else
               favorite.setImageResource(R.drawable.staroff);



            movieExtandableAdapter = new MovieExtandableAdapter(getActivity(), dataHeaders, dataItems);

            expandList.setAdapter(movieExtandableAdapter);








        }

        else{
            movieExtandableAdapter = new MovieExtandableAdapter(getActivity(), dataHeaders, dataItems);

            expandList.setAdapter(movieExtandableAdapter);

        }
        return rootView;
    }




    public  void updateData(Movie mov )


    {
        hasTrailers=false;
        setHasOptionsMenu(true);
        movie=mov;
        updateFavoriteImage();
        date.setText(mov.getDate());
        overview.setText(mov.getOverview());
        title.setText(mov.getTitle());

        vote.setText(mov.getRate() + "/10");
        Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + mov.getImagePath()).into(imageView);
        favorite.setVisibility(View.VISIBLE);
        dataHeaders = new ArrayList<>();
        dataItems = new HashMap<>();
        trailersKeys=new ArrayList<>();
        trailersNames=new ArrayList<>();
        movieExtandableAdapter = new MovieExtandableAdapter(getActivity(), dataHeaders, dataItems);

       expandList.setAdapter(movieExtandableAdapter);
        dataHeaders.add("Trailers" );
        dataItems.put("Trailers" , new ArrayList<String>());
        dataHeaders.add("reviews" );

        dataItems.put("reviews" , new ArrayList<String>());


        getReviews();

            getTrailers();





}

    public void updateFavoriteImage(){


        String url=DataBaseContract.URL+"/"+movie.getID();
        Uri uri=Uri.parse(url);
        Cursor cursor= getActivity().getContentResolver().query(uri,null,null,null,null);

        if(cursor.moveToNext()){
            isFavorite=true;
            favorite.setImageResource(R.drawable.staron);

        }

        else{
            isFavorite=false;
            favorite.setImageResource(R.drawable.staroff);


        }

    }
    @Override
    public void onStart() {
        super.onStart();


        favorite.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                           if(!isFavorite) {


                                               ContentValues contentValues = new ContentValues();
                                               contentValues.put(DataBaseContract.MOVIE_ID , movie.getID());
                                               contentValues.put(DataBaseContract.MOVIE_NAME,movie.getTitle());
                                               contentValues.put(DataBaseContract.MOVIE_POSTER , movie.getImagePath());
                                               contentValues.put(DataBaseContract.MOVIE_OVERVIEW , movie.getOverview());
                                               contentValues.put(DataBaseContract.MOVIE_VOTE , movie.getRate());
                                               contentValues.put(DataBaseContract.MOVIE_Date , movie.getDate());

                                               Uri uri = getActivity().getContentResolver().insert(
                                                       DataBaseContract.URI, contentValues);


                                             if(uri!=null){
                                                   isFavorite=true;
                                                   favorite.setImageResource(R.drawable.staron);

                                           }

                                           }
                                           else{

                                               String url=DataBaseContract.URL+"/"+movie.getID();
                                               Uri uri=Uri.parse(url);
                                               int affected=getActivity().getContentResolver().delete(uri,null,null);



                                               if(affected!=0){
                                                   isFavorite=false;
                                                   favorite.setImageResource(R.drawable.staroff);

                                              }

                                           }
                                        }
                                    });

        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {






                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailersKeys.get(childPosition)));
                    startActivity(intent);


                return false;
            }
        });




    }



    public void getReviews() {

        String data = new String();
        data = movie.getID() + "";

        FetchMovieData f = new FetchMovieData();
        f.execute(data);
    }

    public void getTrailers() {

        String data = new String();
        data = movie.getID() + "";

        FetchMovieTrailers f = new FetchMovieTrailers();
        f.execute(data);
    }


    private class FetchMovieData extends AsyncTask<String, Void, ArrayList> {
        ArrayList<Review> reviews;

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (arrayList != null) {

               // dataHeaders.add("reviews" );

                dataItems.put("reviews" , arrayList);

                movieExtandableAdapter.setDataItems(dataItems);
               expandList.setAdapter(movieExtandableAdapter);


            }



        }

        @Override
        protected ArrayList doInBackground(String... params) {


            String id;

           id = params[0];




            BufferedReader reader = null;
            String dataJson = null;
            try {

                HttpURLConnection urlConnection;

                String baseUrl = "http://api.themoviedb.org/3/movie/";
                baseUrl += id + "/" + "reviews" + "?";

                String api_key = "";
                Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                builder.appendQueryParameter("api_key", api_key);

                URL url = new URL(builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect(); ////////////////////

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();


                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                dataJson = buffer.toString();




                return getReviews(dataJson);


            } catch (IOException e) {
                System.out.println("error" + e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        public ArrayList getReviews(String dataJson) throws JSONException {

            JSONObject jsObject = new JSONObject(dataJson);
            JSONArray jsArray = jsObject.getJSONArray("results");
             reviews = new ArrayList<>();
            ArrayList<String> reviewsContent = new ArrayList<>();
            Review review;


            for (int i = 0; i < jsArray.length(); i++) {
                review = new Review();
                review.setAuthor(jsArray.getJSONObject(i).getString("author"));
                review.setContent(jsArray.getJSONObject(i).getString("content"));
                review.setUrl(jsArray.getJSONObject(i).getString("url"));
                reviewsContent.add("Author Name : "+review.getAuthor()+"\n \n  "+
                        jsArray.getJSONObject(i).getString("content"));
                reviews.add(review);

            }


            return reviewsContent;
        }
    }


    public class FetchMovieTrailers extends AsyncTask<String, Void, ArrayList> {
        @Override
        protected void onPostExecute(ArrayList arrayList) {

            if (arrayList != null) {
              //  dataHeaders.add("Trailers" );
                dataItems.put("Trailers" , arrayList);

                movieExtandableAdapter.setDataItems(dataItems);
                expandList.setAdapter(movieExtandableAdapter);

               if(!arrayList.isEmpty())
                   hasTrailers=true;
            }


        }


        @Override
        protected ArrayList doInBackground(String... params) {


            String id;

            id = params[0];


            BufferedReader reader = null;
            String dataJson = null;
            try {

                HttpURLConnection urlConnection;

                String baseUrl = "http://api.themoviedb.org/3/movie/";
                baseUrl += id + "/" + "videos" + "?";

                String api_key = "";
                Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                builder.appendQueryParameter("api_key", api_key);

                URL url = new URL(builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();


                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                dataJson = buffer.toString();




                    return getTrailers(dataJson);

            } catch (IOException e) {
                System.out.println("error" + e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        public ArrayList getTrailers(String dataJson) throws JSONException {

            JSONObject jsObject = new JSONObject(dataJson);
            JSONArray jsArray = jsObject.getJSONArray("results");
             trailersKeys = new ArrayList<String>();
            Trailer trailer;
             trailersNames = new ArrayList<>();


            for (int i = 0; i < jsArray.length(); i++) {

                trailersNames.add(jsArray.getJSONObject(i).getString("name"));
                trailersKeys.add(jsArray.getJSONObject(i).getString("key"));


            }


            return trailersNames;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();


        if (id == R.id.action_share) {
           if(hasTrailers) {
               Intent shareIntent = new Intent(Intent.ACTION_SEND);
               shareIntent.setType("text/plain");

               shareIntent.putExtra(Intent.EXTRA_SUBJECT,trailersNames.get(0));
               shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailersKeys.get(0));

               startActivity(Intent.createChooser(shareIntent, "Share"));


               return true;
           }
            return false;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_detail,menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (movie != null && dataHeaders != null && dataItems != null && trailersKeys != null&&trailersNames!=null){
            outState.putBoolean("isfavorite",isFavorite);
            outState.putBoolean("hastrailers", hasTrailers);
            outState.putSerializable("movie", movie);

        outState.putStringArrayList("headers", dataHeaders);

         outState.putSerializable("items",dataItems);
        outState.putStringArrayList("trailers",trailersKeys);
            outState.putStringArrayList("trailersNames",trailersNames);



            }

    }



}
