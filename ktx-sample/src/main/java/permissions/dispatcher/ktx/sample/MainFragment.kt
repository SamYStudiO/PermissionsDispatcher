package permissions.dispatcher.ktx.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class MainFragment : Fragment() {
    private lateinit var permissionsRequester: PermissionsRequester
    private lateinit var fileManagerRequester: PermissionsRequester
    private var permissionRequest: PermissionRequest? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionsRequester = constructPermissionsRequest(
            Manifest.permission.CAMERA,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain,
            requiresPermission = ::openCamera
        )
        fileManagerRequester = constructPermissionsRequest(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            requiresPermission = ::openFileManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button_camera).setOnClickListener {
            permissionsRequester.launch()
        }
        view.findViewById<Button>(R.id.button_file_manager).setOnClickListener {
            fileManagerRequester.launch()
        }

        setFragmentResultListener(PermissionDialogFragment.RESULT_KEY_POSITIVE) { _, _ ->
            val scaledBitmap: Bitmap = Bitmap.createBitmap( 5000, 5000, Bitmap.Config.ARGB_8888)
            scaledBitmap.recycle()
            System.gc()
            requireView().postDelayed({
                permissionRequest?.proceed()
                permissionRequest = null
            }, 1000)
        }

        setFragmentResultListener(PermissionDialogFragment.RESULT_KEY_NEGATIVE) { _, _ ->
            permissionRequest?.cancel()
            permissionRequest = null
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
            MediaStore.EXTRA_OUTPUT,
            Environment.DIRECTORY_DOWNLOADS + "/temp.png"
        )
        startActivityForResult(intent, 1)
    }

    private fun openFileManager() {
        val intent = Intent().setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose file"), 2)
    }

    private fun onCameraDenied() {
        Toast.makeText(requireContext(), R.string.permission_camera_denied, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onCameraShowRationale(request: PermissionRequest) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        permissionRequest = request
        showRationaleDialog(R.string.permission_camera_rationale, request)
    }

    private fun onCameraNeverAskAgain() {
        Toast.makeText(
            requireContext(),
            R.string.permission_camera_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        val dialog = PermissionDialogFragment()
        dialog.show(parentFragmentManager, "PermissionDialog")
    }
}
