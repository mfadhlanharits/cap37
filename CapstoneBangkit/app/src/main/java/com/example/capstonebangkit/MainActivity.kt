package com.example.capstonebangkit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonebangkit.api.ApiClient
import com.example.capstonebangkit.api.UserAdapter
import com.example.capstonebangkit.api.UserResponse
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), UserAdapter.ClickedItem {
    var toolbar: Toolbar? = null
    var recyclerView: RecyclerView? = null
    var userAdapter: UserAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("SmartKet")
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        recyclerView?.setItemAnimator(SlideInUpAnimator())
        val divider =
            DividerItemDecoration(recyclerView?.getContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(baseContext, R.drawable.linedivider)!!)
        recyclerView?.addItemDecoration(divider)
        userAdapter = UserAdapter { userResponse: UserResponse? -> ClickedUser(userResponse) }
        allUsers
    }

    val allUsers: Unit
        get() {
            val userList: Call<List<UserResponse?>?>? = ApiClient.userService.allUsers
            userList?.enqueue(object : Callback<List<UserResponse?>?> {
                override fun onResponse(
                    call: Call<List<UserResponse?>?>,
                    response: Response<List<UserResponse?>?>
                ) {
                    if (response.isSuccessful) {
                        val userResponses: List<UserResponse?>? = response.body()
                        userAdapter?.setData(userResponses as MutableList<UserResponse?>?)
                        recyclerView!!.adapter = userAdapter
                    }
                }

                override fun onFailure(call: Call<List<UserResponse?>?>, t: Throwable) {
                    Log.e("failure", t.localizedMessage)
                }
            })
        }

    override fun ClickedUser(userResponse: UserResponse?) {
        startActivity(Intent(this, UserDetailActivity::class.java).putExtra("data", userResponse))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                userAdapter?.getFilter()?.filter(newText)
                return false
            }
        })
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_settings) {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_alert_dialog)
            .setTitle("Closing Application")
            .setMessage("Are you sure you want to close this application?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            .setNegativeButton("No", null)
            .show()
    }
}