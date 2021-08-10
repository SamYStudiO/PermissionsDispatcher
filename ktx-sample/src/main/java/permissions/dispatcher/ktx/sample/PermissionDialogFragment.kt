package permissions.dispatcher.ktx.sample

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult

class PermissionDialogFragment : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.button_allow) { _, _ ->
                setFragmentResult(
                    RESULT_KEY_POSITIVE,
                    bundleOf()
                )
            }
            .setNegativeButton(R.string.button_deny) { _, _ ->
                setFragmentResult(
                    RESULT_KEY_NEGATIVE,
                    bundleOf()
                )
            }
            .setCancelable(false)
            .setMessage("allow permission?")
            .show()
    }

    companion object {
        const val RESULT_KEY_POSITIVE = "positive"
        const val RESULT_KEY_NEGATIVE = "negative"
    }
}