package statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.fragments

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.FragmentDirectChatBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.activities.CountrySelectorActivity
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.bottomSheet.FragmentIsdDetected
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.model.Country
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getCountryByCode
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getDrawableFromAssets
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getLaunchIntent
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getLaunchIntentForShareLink
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.launchIfResolved
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.loadCountriesFromJson
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils


class DirectChatFragment : Fragment() {

    private lateinit var binding: FragmentDirectChatBinding
    private var selectedCountry: Country? = null
    private var isBusinessWhatsApp: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDirectChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        isBusinessWhatsApp = SharedPrefUtils.getPrefBoolean(Constants.SWITCH_BUTTON_KEY, false)
    }


    private fun setClickListeners() {


        binding.selectCountry.setOnClickListener {
            startActivity(Intent(context, CountrySelectorActivity::class.java))
        }

        binding.send.setOnClickListener {
            handleSendClick(
                isBusiness = SharedPrefUtils.getPrefBoolean(
                    Constants.SWITCH_BUTTON_KEY,
                    false
                )
            )
        }

        binding.btnShareLink.setOnClickListener {
            shareLink()
        }
    }

    private fun shareLink() {

        if (binding.waNumber.text == null || binding.waNumber.text.isEmpty() || binding.waNumber.text.toString() == "+") {
            Toast.makeText(context, R.string.enter_number_warn, Toast.LENGTH_SHORT).show()
            return
        }

        val phoneNumber = binding.waNumber.text.toString()
        val message = binding.optionalMessage.text.toString()

        if (phoneNumber.contains("+")) {
            activity?.let { it ->
                FragmentIsdDetected.newInstance(phoneNumber)
                    .setDialogClickListener {
                        context?.let {
                            getLaunchIntentForShareLink(phoneNumber, message).launchIfResolved(
                                it
                            )
                        }
                    }
                    .show(it.supportFragmentManager, "ISD DETECTED")
            }
            return
        }

        val phoneNumberWithIsd = selectedCountry?.isdCode + phoneNumber
        context?.let {
            getLaunchIntentForShareLink(
                phoneNumberWithIsd,
                message
            ).launchIfResolved(it)
        }
    }

    private fun handleSendClick(isBusiness: Boolean) {

        if (binding.waNumber.text == null || binding.waNumber.text.isEmpty() || binding.waNumber.text.toString() == "+") {
            Toast.makeText(context, R.string.enter_number_warn, Toast.LENGTH_SHORT).show()
            return
        }

        val phoneNumber = binding.waNumber.text.toString()
        val message = binding.optionalMessage.text.toString()

        if (phoneNumber.contains("+")) {
            activity?.let { it ->
                FragmentIsdDetected.newInstance(phoneNumber)
                    .setDialogClickListener {
                        context?.let {
                            getLaunchIntent(phoneNumber, message, isBusiness).launchIfResolved(
                                it
                            )
                        }
                    }
                    .show(it.supportFragmentManager, "ISD DETECTED")
            }
            return
        }

        val phoneNumberWithIsd = selectedCountry?.isdCode + phoneNumber
        context?.let {
            getLaunchIntent(
                phoneNumberWithIsd,
                message,
                isBusiness
            ).launchIfResolved(it)
        }
    }

    override fun onResume() {
        super.onResume()

        val savedCountryCode =
            SharedPrefUtils.getPrefString(Constants.SELECTED_COUNTRY_PREF_KEY, "")

        try {
            if (savedCountryCode.isNullOrEmpty()) {
                val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val countryCodeValue = tm.networkCountryIso
                if (countryCodeValue.toString().isNullOrEmpty()) {
                    updateSelectedCountry("ES")
                } else {
                    Log.d("count", "onResume: " + countryCodeValue.uppercase())
                    updateSelectedCountry(countryCodeValue.uppercase())
                }
            }
            else {
                updateSelectedCountry(savedCountryCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            updateSelectedCountry("ES")
        }

        handleClipboardPaste()
    }

    private fun updateSelectedCountry(countryCode: String) {

        val country =
            context?.loadCountriesFromJson("country_data.json")?.getCountryByCode(countryCode)
        country?.let {
            binding.flag.setImageDrawable(context?.getDrawableFromAssets(it.flagResource))
            binding.isdCode.text = it.isdCode
        }
        selectedCountry = country
    }

    private fun handleClipboardPaste() {
        val clipboardManager = activity?.getSystemService(ClipboardManager::class.java)
        val clipItem = clipboardManager?.primaryClip?.getItemAt(0)

        if (clipboardManager == null || !clipboardManager.hasPrimaryClip() || clipItem?.text == null) {
            binding.paste.visibility = View.GONE
            return
        }

        val copiedText = clipItem.text.toString()
        if (!PhoneNumberUtils.isGlobalPhoneNumber(copiedText)) {
            binding.paste.visibility = View.GONE
            return
        }

        binding.paste.visibility = View.VISIBLE
        binding.paste.setOnClickListener { binding.waNumber.setText(copiedText) }
    }
}