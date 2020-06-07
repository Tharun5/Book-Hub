package com.tharun.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.tharun.bookhub.R
import com.tharun.bookhub.adapter.FavouriteRecyclerAdapter
import com.tharun.bookhub.database.BookDatabase
import com.tharun.bookhub.database.BookEntity


class FavouritesFragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressBarLayout : RelativeLayout
    lateinit var progressBar: ProgressBar
    var dbBookList = listOf<BookEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
       recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressBar = view.findViewById(R.id.progressBar)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)

        layoutManager = GridLayoutManager(activity as Context,2)
        dbBookList =RetrieveFavourites(activity as Context).execute().get()

        if(activity!= null){
            progressBarLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbBookList)
            recyclerFavourite.adapter=recyclerAdapter
            recyclerFavourite.layoutManager=layoutManager
        }

        return view
    }
    class RetrieveFavourites(val context: Context):AsyncTask<Void,Void,List<BookEntity>>(){
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java,"book-db").build()
            return db.bookDao().getAllBooks()
        }

    }
}
