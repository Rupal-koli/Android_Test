package com.example.whatsappcloone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.whatsappcloone.R.string.waiting_text
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit


const val PHONE_NUMBER = "phoneNumber"
class ExtraActivity : AppCompatActivity(), View.OnClickListener {


    private var phoneNumber : String? = null
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId:String? = null
    private var mResendToken:PhoneAuthProvider.ForceResendingToken? = null
    private var isTimerActive = false
    private var timeLeft:Long = -1
    private var mCounterDown:CountDownTimer? = null
    lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        initViews()
        startVerify()
    }



    private fun startVerify() {

//        FirebaseApp.initializeApp()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks)

        showTimer(60000)

//        progressDialog = createProgressDialog("Sending a Verification code" , false)
//        progressDialog.show()

    }

    private fun showTimer(milliSecInFuture: Long) {
        btnResendsms.isEnabled = false
        mCounterDown = object:CountDownTimer(milliSecInFuture,1000){
            override fun onTick(p0: Long) {
                tvCounter.isVisible = true
                tvCounter.text = getString(R.string.sec_remain,p0/1000)
            }

            override fun onFinish() {
                tvCounter.isVisible = false
                btnResendsms.isEnabled = true
            }
        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCounterDown != null){
            mCounterDown!!.cancel()
        }
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        tvVerify.text = getString(R.string.verify__number,phoneNumber)
        setSpannableString()

        btnVerification.setOnClickListener(this)
        btnResendsms.setOnClickListener(this)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential)

                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                val smsCode = credential.smsCode
                if(!smsCode.isNullOrBlank()){
                    etSentCode.setText(smsCode)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {

                }
//                notifyUserAndRetry()
                createProgressDialog("Your Phone Number might be wrong or connection error . Retry Again ",false)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){

                    startActivity(Intent(this,SignUpActivity::class.java))
                }else{
                    createProgressDialog("Your Phone Number verification failed ! Try Again",false)
                }
            }

    }

    //    @SuppressLint("StringFormatInvalid")
    private fun setSpannableString() {
        val span = SpannableString(getString(R.string.waitingText,phoneNumber))
        val clickableSpan = object:ClickableSpan(){
            override fun onClick(p0: View) {
                showLoginActivity()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan,span.length - 14,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvWaiting.movementMethod = LinkMovementMethod.getInstance()
        tvWaiting.text = span
    }

    private fun showLoginActivity() {
        startActivity(Intent(this,LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onBackPressed() {

    }

    private fun notifyUserAndRetry(message: String){
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("OK"){_, _ ->
                showLoginActivity()
            }
            setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            btnVerification->{
                val code = etSentCode.text.toString()
                if(code.isNotEmpty() && !mVerificationId.isNullOrBlank()){
                    progressDialog = createProgressDialog("Please wait ...",false)
                    progressDialog.show()
                    val credentials = PhoneAuthProvider.getCredential(mVerificationId!!,code)
                    signInWithPhoneAuthCredential(credentials)
                }
            }
            btnResendsms->{
                if(mResendToken != null){
                    showTimer(60000)

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber!!,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks,
                        mResendToken)
                }
            }
        }
    }
}
fun Context.createProgressDialog(message:String,isCancelable:Boolean) : ProgressDialog {
    return ProgressDialog(this).apply {
        setMessage(message)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }


}
