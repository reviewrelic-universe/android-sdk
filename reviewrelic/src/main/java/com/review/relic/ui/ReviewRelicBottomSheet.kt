package com.review.relic.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.review.relic.R
import com.review.relic.ReviewRelic
import com.review.relic.adapter.ReviewRelicItemAdapter
import com.review.relic.databinding.ReviewRelicBottomSheetLayoutBinding
import com.review.relic.utils.*
import com.review.relic.viewmodel.ReviewRelicSheetViewModel


class ReviewRelicBottomSheet(
    private var transactionId: String? = null,
    private var thankYouMessage: String? = null,
    private var reviewRelicBottomSheetInputs: ReviewRelicBottomSheetInputs? = null,
    private val onSubmitCallback: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: ReviewRelicBottomSheetLayoutBinding
    private lateinit var reviewRelicItemAdapter: ReviewRelicItemAdapter
    private lateinit var viewModel: ReviewRelicSheetViewModel


    // region lifecycle callbacks
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.review_relic_bottom_sheet_layout,
            null,
            false
        )

        viewModel = ViewModelProvider(this).get(ReviewRelicSheetViewModel::class.java)

        viewModel.transactionId = transactionId
        viewModel.thankYouMessage = thankYouMessage

        binding.viewModel = viewModel

        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val view = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            view?.let {
                val displayMetrics = requireActivity().resources.displayMetrics
                val height = displayMetrics.heightPixels
                val maxHeight = (height * 0.97).toInt()
                BottomSheetBehavior.from(it).setPeekHeight(maxHeight)
            }
            if (binding.nestedScroll.canScrollVertically(1)
                || binding.nestedScroll.canScrollVertically(-1)
            ) {
                binding.ivArrowDown.visibility = View.VISIBLE
            } else {
                binding.ivArrowDown.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheetInputs()

        isCancelable = ReviewRelic.reviewRelicSettings?.settings?.canSkip ?: true

        setCrossButton()
        setDownArrowButton()
        setThankYouLayout()
        setAdapter()
        setInputLayoutBorderColor()
        setSubmitButton()
        setScrollListener()
    }

    private fun setUpSheetInputs() {

        val title = if (reviewRelicBottomSheetInputs?.title.isNullOrEmpty()) {
            ReviewRelic.reviewRelicSettings?.name
        } else {
            reviewRelicBottomSheetInputs?.title
        }

        val subtitle = reviewRelicBottomSheetInputs?.subtitle

        val image = if (reviewRelicBottomSheetInputs?.image == null) {
            ReviewRelicImage(ReviewRelic.reviewRelicSettings?.settings?.appLogo, ImageType.Base64)
        } else {
            reviewRelicBottomSheetInputs?.image
        }

        viewModel.reviewRelicBottomSheetInputs =
            ReviewRelicBottomSheetInputs(title, subtitle, image)
        binding.executePendingBindings()

    }

    // endregion

    //region UI functions
    @SuppressLint("NewApi")
    private fun setThankYouLayout() {
        viewModel.selectedColor?.let {
            binding.imageViewAnimation.setColorFilter(it, PorterDuff.Mode.SRC_IN)
            binding.circularProgress.setIndeterminateTintCompat(it)
            binding.textViewThankYou.setTextColor(it)
        }
        binding.thankYouMessage = thankYouMessage
    }

    private fun setCrossButton() {
        ReviewRelic.reviewRelicSettings?.settings?.color?.let {
            val color = Color.parseColor(it)
            binding.btnCross.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        binding.btnCross.setOnClickListener {
            dismiss()
        }
    }

    private fun setDownArrowButton() {
        viewModel.selectedColor?.let {
            binding.ivArrowDown.setColorFilter(it, PorterDuff.Mode.SRC_IN)
        }

        binding.ivArrowDown.setOnClickListener {
            binding.nestedScroll.post {
                binding.nestedScroll.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun setInputLayoutBorderColor() {
        binding.tilReview.setBoxStrokeColorStateList(getTextInputLayoutBorderColor(viewModel.selectedColor))
        binding.tilReview.counterTextColor = getTextInputLayoutBorderColor(viewModel.selectedColor)
        binding.tilReview.hintTextColor = getTextInputLayoutBorderColor(viewModel.selectedColor)
    }

    private fun setSubmitButton() {
        context?.let {
            binding.buttonSubmitReview.apply {
                background = getButtonSelectorDrawable(it, viewModel.selectedColor)
                setTextColor(getButtonSelectorColor(it, viewModel.selectedColor))
            }
        }

        binding.buttonSubmitReview.setOnClickListener {
            if (viewModel.checkedPosition == -1) {
                Toast.makeText(context, getString(R.string.select_review), Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.groupMainView.visibility = View.INVISIBLE
                binding.groupLoading.visibility = View.VISIBLE
                ReviewRelic.submitReview(
                    viewModel.getBodyForSubmit(),
                    viewModel.comments.value,
                    viewModel.reviewRelicBottomSheetInputs.title,
                    viewModel.reviewRelicBottomSheetInputs.subtitle
                ) {
                    showThankYouLayout()
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun showThankYouLayout() {
        binding.groupLoading.visibility = View.GONE
        binding.groupThankYou.visibility = View.VISIBLE
        val animatedVectorDrawable =
            (binding.imageViewAnimation.drawable as AnimatedVectorDrawable)
        animatedVectorDrawable.registerAnimationCallback(object :
            Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                onSubmitCallback?.invoke()
                dismiss()
            }
        })
        animatedVectorDrawable.start()
    }

    private fun setScrollListener() {
        binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
            if (binding.nestedScroll.getChildAt(0).bottom > (binding.nestedScroll.height + binding.nestedScroll.scrollY)) {
                binding.ivArrowDown.visibility = View.GONE
            }
        })
    }

    // endregion

    //region Set Adapter

    private fun setAdapter() {
        binding.recyclerViewItems.apply {
            reviewRelicItemAdapter =
                ReviewRelicItemAdapter(
                    context = context,
                    showImage = ReviewRelic.reviewRelicSettings?.settings?.shouldShowImages,
                    emptyBitmap = getBitmapFromBase64(ReviewRelic.reviewRelicSettings?.settings?.emptyImage),
                    filledBitmap = getBitmapFromBase64(ReviewRelic.reviewRelicSettings?.settings?.fillImage),
                    selectedColor = viewModel.selectedColor
                ) { selectedPosition ->
                    viewModel.checkedPosition = selectedPosition
                    binding.buttonSubmitReview.isEnabled = true
                }
            adapter = reviewRelicItemAdapter
            if (ReviewRelic.reviewRelicSettings?.settings?.shouldShowImages == true)
                layoutManager = FlexboxLayoutManager(context).apply {
                    justifyContent = JustifyContent.SPACE_EVENLY
                    alignItems = AlignItems.CENTER
                }

            reviewRelicItemAdapter.submitList(ReviewRelic.reviewRelicSettings?.reviewSettings)
        }
    }

    //endregion
}

data class ReviewRelicBottomSheetInputs(
    /**
     * Title you want to show (will override the title set on admin panel)
     */
    val title: String? = null,
    /**
     * Subtitle  you want to show (will override the subtitle set on admin panel)
     */
    val subtitle: String? = null,
    /**
     * Icon you want to show (will override the icon set on admin panel)
     */
    val image: ReviewRelicImage? = null,
)
