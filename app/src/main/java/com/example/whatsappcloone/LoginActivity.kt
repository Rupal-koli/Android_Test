package com.example.whatsappcloone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var phoneNumber:String
    lateinit var countryCode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Add hint request for Phone Number(Hint Request)
        etPhoneNum.addTextChangedListener {
            btnNext.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        btnNext.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + etPhoneNum.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone Number:$phoneNumber\n" +
                         "Is this Ok,or would you like to edit the number ? ")
            setPositiveButton("OK"){_,_ ->
                showOtpActivity()

            }
            setNegativeButton("Edit"){dialog,which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()

        }

    }

    private fun showOtpActivity() {
         startActivity(Intent(this,ExtraActivity::class.java).putExtra(PHONE_NUMBER,phoneNumber))
        finish()
    }
}