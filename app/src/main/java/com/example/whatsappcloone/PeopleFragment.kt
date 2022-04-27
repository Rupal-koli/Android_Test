package com.example.whatsappcloone

import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagedList
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.paging.LoadingState
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.chatfragment.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
private const val DELETED_VIEW_TYPE = 1
private const val NORMAL_VIEW_TYPE = 1

class PeopleFragment : Fragment() {


    lateinit var mAdapter: FirestorePagingAdapter<User,RecyclerView.ViewHolder>

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()

    }
    lateinit var query: Query

    override fun onCreateView(


        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        setupAdapter()
        return inflater.inflate(R.layout.chatfragment, container, false)

        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupAdapter() {

        query = database.collection("users").orderBy("name", Query.Direction.ASCENDING)
//        val config = PagedList.Config.Builder()
//            .setPrefetchDistance(2)
//            .setPageSize(10)
//            .setEnablePlaceholders(false)
//            .build()
        val config = PagingConfig(10, 2, false)

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(query, config, User::class.java)
            .build()


        mAdapter = object : FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options) {

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if(holder is UserViewHolder ){
                    holder.bind(user = model)
                }else{
                    
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                    NORMAL_VIEW_TYPE -> UserViewHolder(
                        layoutInflater.inflate(
                            R.layout.list_item,
                            parent,
                            false
                        )
                    )
                    else -> EmptyViewHolder(layoutInflater.inflate(R.layout.empty_view, parent, false))
                }
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if(auth.uid == item!!.uId){
                    DELETED_VIEW_TYPE
                }else{
                    NORMAL_VIEW_TYPE
                }
            }


        }
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter
            }
            viewLifecycleOwner.lifecycleScope.launch{
                mAdapter.loadStateFlow.collectLatest { loadstates ->
                    when(loadstates.refresh) {
                        is LoadState.Error -> {
                        }
                        is LoadState.Loading -> {
                        }
                    }
                    when(loadstates.append){
                        is LoadState.Error -> {}
                        is LoadState.Loading -> {}
                        is LoadState.NotLoading -> {
                            if(loadstates.append.endOfPaginationReached){ }
                            if (loadstates.refresh is LoadState.NotLoading){ }
                        }
                    }
                }

            }
        }

    }

