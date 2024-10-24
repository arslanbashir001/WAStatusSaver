package statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.bottomSheet

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.BottomSheetIsdDetectedAlertBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.detectCountry
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getDrawableFromAssets

class FragmentIsdDetected : BottomSheetDialogFragment() {

    private var _binding: BottomSheetIsdDetectedAlertBinding? = null
    private val binding get() = _binding!!

//    private lateinit var countryBinding: ItemIsdCodeDetectedBinding

    private lateinit var dialogListener: () -> Unit
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { phoneNumber = it.getString(ARG_PHONE_NUMBER).toString() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = BottomSheetIsdDetectedAlertBinding.inflate(inflater, container, false)
//        countryBinding = binding.detectedCountry
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Used for displaying the number as entered, without ISD code if a country is detected.
        var strippedNumber = phoneNumber
        val detectedCountry = context?.let { detectCountry(it, phoneNumber) }

        /*If the entered country code does not correspond with any of the codes we have saved,
        we can either dismiss or let the user continue anyway.
        This ensures that user can enter a code which we do not have.*/

        if (detectedCountry == null) {
            binding.message.setText(R.string.isd_detected_message_no_country)
//            binding.countryHeader.visibility = View.GONE
            binding.detectedCountry.visibility = View.GONE
            binding.okContinue.setText(R.string.ignore)
        }

        //Otherwise, we have been successful in detecting a country.
        else {
            binding.detectedFlag.setImageDrawable(context?.getDrawableFromAssets(detectedCountry.flagResource))
            binding.detectedCountryName.text = detectedCountry.name

            val code = "+${detectedCountry.isdCode}"
           binding.detectedCountryCode.text = code

            //Show the number without the ISD code separately to clarify things up.
            strippedNumber = phoneNumber
                .replace("+", "")
                .replaceFirst(detectedCountry.isdCode, "")

            if (strippedNumber.isEmpty()) {
                strippedNumber = getString(R.string.no_number_entered)
                binding.okContinue.isEnabled = false
            }
        }

        //The phoneNumber TextView should show text like a phone number. Duh!
        binding.phoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.phoneNumber.text = strippedNumber

        binding.okContinue.setOnClickListener {
            dialogListener.invoke()
            dismiss()
        }

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setDialogClickListener(listener: () -> Unit): FragmentIsdDetected {
        dialogListener = listener
        return this
    }

    companion object {
        private const val ARG_PHONE_NUMBER = "phoneNumber"
        fun newInstance(phoneNumber: String?): FragmentIsdDetected {
            return FragmentIsdDetected().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHONE_NUMBER, phoneNumber)
                }
            }
        }
    }
}