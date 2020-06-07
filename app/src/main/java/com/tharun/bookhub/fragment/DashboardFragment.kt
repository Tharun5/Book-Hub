package com.tharun.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.view.textclassifier.TextLinks
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.tharun.bookhub.R
import com.tharun.bookhub.adapter.DashboardRecyclerAdapter
import com.tharun.bookhub.model.Book
import com.tharun.bookhub.util.ConnectionManager
import kotlinx.android.synthetic.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
   lateinit var progressBarLayout : RelativeLayout
    lateinit var progressBar: ProgressBar
    val bookInfoList = arrayListOf<Book>()

    var ratingComparator = Comparator<Book>{book1,book2 ->
        if (book1.bookRating.compareTo(book2.bookRating,true) == 0){
                book1.bookName.compareTo(book2.bookName,true)
            }else{
            book1.bookRating.compareTo(book2.bookRating,true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressBarLayout.visibility = View.VISIBLE
        recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)
        recyclerDashboard.adapter = recyclerAdapter
        recyclerDashboard.layoutManager = layoutManager
        recyclerDashboard.addItemDecoration(
            DividerItemDecoration(
                recyclerDashboard.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            progressBarLayout.visibility = View.GONE
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJSONObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJSONObject.getString("book_id"),
                                    bookJSONObject.getString("name"),
                                    bookJSONObject.getString("author"),
                                    bookJSONObject.getString("rating"),
                                    bookJSONObject.getString("price"),
                                    bookJSONObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)
                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e:JSONException){
                        Toast.makeText(activity as Context,"Some Unexpected Error!",Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                   if(activity!=null){
                       Toast.makeText(activity as Context,"volley error occurred",Toast.LENGTH_SHORT).show()
                   }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "8f50909c96bbd1"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}

