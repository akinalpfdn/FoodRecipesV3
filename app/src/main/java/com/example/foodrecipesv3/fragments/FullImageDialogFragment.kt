import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.foodrecipesv3.R

class FullImageDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String): FullImageDialogFragment {
            val fragment = FullImageDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_image_dialog, container, false)

        val imageView = view.findViewById<ImageView>(R.id.fullImageView)
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)

        // Load the image using Glide or any other library
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        return view
    }
}
