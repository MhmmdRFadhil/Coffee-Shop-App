package com.ryz.coffee.user.fragment.order.tab.tab5

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.ryz.coffee.R
import com.ryz.coffee.user.fragment.order.tab.tab4.DoneAdapter
import com.ryz.coffee.user.fragment.order.tab.tab4.DoneModel

class CanceledFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var canceledModel: ArrayList<CanceledModel>
    private lateinit var canceledAdapter: CanceledAdapter

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canceled, container, false)

        recyclerView = view.findViewById(R.id.rvCanceled)

        recyclerView.layoutManager = LinearLayoutManager(context)

        mAuth = Firebase.auth

        canceledModel = ArrayList()

        getCanceledData()

        return view
    }

    private fun getCanceledData() {
        val email = mAuth.currentUser!!.email
        val cleanEmail = email!!.split('.').joinToString(','.toString())
        databaseReference =
            FirebaseDatabase.getInstance().getReference("User/$cleanEmail/ProductOrder")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                canceledModel.clear()
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val key = it.key.toString()
                        databaseReference.child(key).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val address: String? =
                                    snapshot.child("Info").child("address")
                                        .getValue(String::class.java)
                                val paymentMethod: String? =
                                    snapshot.child("Info").child("paymentMethod")
                                        .getValue(String::class.java)
                                val statusOrder: String? =
                                    snapshot.child("Info").child("statusOrder")
                                        .getValue(String::class.java)
                                val subTotalDelivery: String? =
                                    snapshot.child("Info").child("subTotalDelivery")
                                        .getValue(String::class.java)
                                val subTotalProduct: String? =
                                    snapshot.child("Info").child("subTotalProduct")
                                        .getValue(String::class.java)
                                val totalPayment: String? =
                                    snapshot.child("Info").child("totalPayment")
                                        .getValue(String::class.java)
                                val emailUser: String? = snapshot.child("Info").child("email")
                                    .getValue(String::class.java)

                                for (ds in snapshot.children) {
                                    val kunci = ds.key.toString()
                                    Log.d("KUNCI", "${ds.key}")
                                    val count: String? =
                                        ds.child("count").getValue(String::class.java)
                                    val image: String? =
                                        ds.child("image").getValue(String::class.java)
                                    val title: String? =
                                        ds.child("title").getValue(String::class.java)
                                    val totalPrice: String? =
                                        ds.child("totalPrice").getValue(String::class.java)
                                    val orderId: String? =
                                        ds.child("orderId").getValue(String::class.java)

                                    val childrenCount: String = it.childrenCount.toString()
                                    Log.d("total", "$totalPayment")
                                    val cc = (childrenCount.toInt() - 1).toString()

                                    if (kunci != "Info") {
                                        if (statusOrder.toString() == "Dibatalkan") {
                                            if (cc.toInt() > 1) {
                                                canceledModel.add(CanceledModel(title,
                                                    image,
                                                    count,
                                                    totalPrice,
                                                    orderId,
                                                    address,
                                                    paymentMethod,
                                                    statusOrder,
                                                    subTotalProduct,
                                                    subTotalDelivery,
                                                    totalPayment,
                                                    cc, emailUser))
                                                break
                                            } else {
                                                canceledModel.add(CanceledModel(title,
                                                    image,
                                                    count,
                                                    totalPrice,
                                                    orderId,
                                                    address,
                                                    paymentMethod,
                                                    statusOrder,
                                                    subTotalProduct,
                                                    subTotalDelivery,
                                                    totalPayment,
                                                    cc, emailUser))
                                            }
                                        }
                                    }
                                }
                                canceledAdapter = CanceledAdapter(canceledModel)
                                //                  notYetPaidAdapter.listener = this@NotYetPaidFragment
                                canceledAdapter.notifyDataSetChanged()
                                recyclerView.setHasFixedSize(true)
                                recyclerView.adapter = canceledAdapter
                                recyclerView.isNestedScrollingEnabled = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                } else {
                    canceledModel.clear()
                    canceledAdapter = CanceledAdapter(canceledModel)
                    canceledAdapter.notifyDataSetChanged()
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = canceledAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}