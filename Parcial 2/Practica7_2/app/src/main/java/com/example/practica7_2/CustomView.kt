package com.example.practica7_2

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.customview.view.AbsSavedState
import com.example.practica7_2.databinding.CustomViewBinding

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.customViewStyle,
    defStyleRes: Int = R.style.Widget_Practica7_2_CustomView
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: CustomViewBinding
    private var imageDrawable: Drawable? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var titleTextColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private var subtitleTextColor: Int = ContextCompat.getColor(context, android.R.color.darker_gray)
    private var dividerHeight: Int = context.resources.getDimensionPixelSize(R.dimen.custom_view_divider_height)
    private var imageSize: Int = context.resources.getDimensionPixelSize(R.dimen.custom_view_icon_size)
    private var contentPadding: Int = context.resources.getDimensionPixelSize(R.dimen.custom_view_content_margin)
    private var isDividerVisible: Boolean = true

    init {
        binding = CustomViewBinding.inflate(LayoutInflater.from(context), this, true)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
        applyStyles()
        setupInteraction()
        setupAccessibility()
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imageDrawable = getDrawable(R.styleable.CustomView_setImageDrawable)
                title = getString(R.styleable.CustomView_setTitle)
                subtitle = getString(R.styleable.CustomView_setSubTitle)

                titleTextColor = getColor(R.styleable.CustomView_titleTextColor, titleTextColor)
                subtitleTextColor = getColor(R.styleable.CustomView_subtitleTextColor, subtitleTextColor)
                dividerHeight = getDimensionPixelSize(R.styleable.CustomView_dividerHeight, dividerHeight)
                imageSize = getDimensionPixelSize(R.styleable.CustomView_imageSize, imageSize)
                contentPadding = getDimensionPixelSize(R.styleable.CustomView_contentPadding, contentPadding)
                isDividerVisible = getBoolean(R.styleable.CustomView_dividerVisible, true)

                val imageMarginStart = getDimensionPixelSize(R.styleable.CustomView_imageMarginStart, 0)
                val titleMarginStart = getDimensionPixelSize(R.styleable.CustomView_titleMarginStart, 0)

                (binding.imageView.layoutParams as MarginLayoutParams).marginStart = imageMarginStart
                (binding.titleTextView.layoutParams as MarginLayoutParams).marginStart = titleMarginStart
            } finally {
                recycle()
            }
        }
    }

    private fun applyStyles() {
        with(binding) {
            imageView.setImageDrawable(imageDrawable)
            titleTextView.text = title
            subtitleTextView.text = subtitle

            titleTextView.setTextColor(titleTextColor)
            subtitleTextView.setTextColor(subtitleTextColor)

            imageView.layoutParams.width = imageSize
            imageView.layoutParams.height = imageSize

            root.setPadding(contentPadding, contentPadding, contentPadding, contentPadding)

            divider.layoutParams.height = dividerHeight
            divider.visibility = if (isDividerVisible) VISIBLE else GONE
        }
    }

    @Suppress("unused")
    fun setImageDrawable(drawable: Drawable?) {
        imageDrawable = drawable
        binding.imageView.setImageDrawable(drawable)
    }

    @Suppress("unused")
    fun getImageDrawable(): Drawable? = imageDrawable

    @Suppress("unused")
    fun setTitle(text: String?) {
        title = text
        binding.titleTextView.text = text
        updateContentDescription()
    }

    @Suppress("unused")
    fun getTitle(): String? = title

    fun setSubtitle(text: String?) {
        subtitle = text
        binding.subtitleTextView.text = text
        updateContentDescription()
    }

    @Suppress("unused")
    fun getSubtitle(): String? = subtitle

    // Nuevos getters y setters
    @Suppress("unused")
    fun setTitleTextColor(@ColorInt color: Int) {
        titleTextColor = color
        binding.titleTextView.setTextColor(color)
    }

    @Suppress("unused")
    fun setSubtitleTextColor(@ColorInt color: Int) {
        subtitleTextColor = color
        binding.subtitleTextView.setTextColor(color)
    }

    @Suppress("unused")
    fun setDividerHeight(height: Int) {
        dividerHeight = height
        binding.divider.layoutParams.height = height
        binding.divider.requestLayout()
    }

    @Suppress("unused")
    fun setImageSize(size: Int) {
        imageSize = size
        binding.imageView.layoutParams.width = size
        binding.imageView.layoutParams.height = size
        binding.imageView.requestLayout()
    }

    @Suppress("unused")
    fun setContentPadding(padding: Int) {
        contentPadding = padding
        binding.root.setPadding(padding, padding, padding, padding)
    }

    @Suppress("unused")
    fun setDividerVisible(visible: Boolean) {
        isDividerVisible = visible
        binding.divider.visibility = if (visible) VISIBLE else GONE
    }

    private fun setupInteraction() {
        isClickable = true
        isFocusable = true

        background = ContextCompat.getDrawable(context, android.R.drawable.list_selector_background)

        setOnClickListener {
            performInteraction()
        }
    }

    private fun performInteraction() {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        startAnimation(scaleDown)
        postDelayed({
            startAnimation(scaleUp)
        }, scaleDown.duration)

        if (title != null) {
            @Suppress("DEPRECATION")
            ViewCompat.setAccessibilityLiveRegion(this, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
            contentDescription = "$title seleccionado"
        }
    }

    private fun setupAccessibility() {
        updateContentDescription()

        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    private fun updateContentDescription() {
        val description = buildString {
            title?.let { append(it) }
            subtitle?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }
        }
        contentDescription = description.ifEmpty { "Vista personalizada" }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState).apply {
            savedTitle = title
            savedSubtitle = subtitle
            savedTitleTextColor = titleTextColor
            savedSubtitleTextColor = subtitleTextColor
            savedDividerHeight = dividerHeight
            savedImageSize = imageSize
            savedContentPadding = contentPadding
            savedIsDividerVisible = isDividerVisible
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            title = state.savedTitle
            subtitle = state.savedSubtitle
            titleTextColor = state.savedTitleTextColor
            subtitleTextColor = state.savedSubtitleTextColor
            dividerHeight = state.savedDividerHeight
            imageSize = state.savedImageSize
            contentPadding = state.savedContentPadding
            isDividerVisible = state.savedIsDividerVisible

            applyStyles()
            updateContentDescription()
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : AbsSavedState {
        var savedTitle: String? = null
        var savedSubtitle: String? = null
        var savedTitleTextColor: Int = 0
        var savedSubtitleTextColor: Int = 0
        var savedDividerHeight: Int = 0
        var savedImageSize: Int = 0
        var savedContentPadding: Int = 0
        var savedIsDividerVisible: Boolean = true

        constructor(superState: Parcelable?) : super(superState ?: EMPTY_STATE)

        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            savedTitle = source.readString()
            savedSubtitle = source.readString()
            savedTitleTextColor = source.readInt()
            savedSubtitleTextColor = source.readInt()
            savedDividerHeight = source.readInt()
            savedImageSize = source.readInt()
            savedContentPadding = source.readInt()
            savedIsDividerVisible = source.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedTitle)
            out.writeString(savedSubtitle)
            out.writeInt(savedTitleTextColor)
            out.writeInt(savedSubtitleTextColor)
            out.writeInt(savedDividerHeight)
            out.writeInt(savedImageSize)
            out.writeInt(savedContentPadding)
            out.writeInt(if (savedIsDividerVisible) 1 else 0)
        }

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR: Parcelable.ClassLoaderCreator<SavedState> = object : Parcelable.ClassLoaderCreator<SavedState> {
                override fun createFromParcel(source: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(source, loader)
                }

                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source, null)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}