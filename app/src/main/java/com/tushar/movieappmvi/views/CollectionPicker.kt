package com.tushar.movieappmvi.views

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.tushar.movieappmvi.R
import java.util.*

class CollectionPicker @SuppressLint("ResourceAsColor") constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) :
    LinearLayout(context, attrs, defStyle) {
    private val genresList = listOf("#febf9b", "#f47f87", "#6ac68d", "#FBC636")
    private val mViewTreeObserver: ViewTreeObserver
    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mItems: MutableList<String> =
        ArrayList()
    private var mRow: LinearLayout? = null

    /**
     * Selected flags
     */
    var checkedItems = HashMap<String, Any>()
    private var mClickListener: OnItemClickListener? = null
    private var mWidth = 0
    private var mItemMargin = 10
    private var textPaddingLeft = 5
    private var textPaddingRight = 5
    private var textPaddingTop = 5
    private var texPaddingBottom = 5
    private var mAddIcon = R.drawable.ic_add_circle_outline_white_24dp
    private var mCancelIcon = R.drawable.ic_close_white_24dp
    private var mLayoutBackgroundColorNormal: Int = R.color.blue_400
    private var mLayoutBackgroundColorPressed: Int = R.color.red_400
    private var mTextColor = R.color.grey1
    private var mRadius = 5
    private var mInitialized = false
    private val simplifiedTags: Boolean
    var isUseRandomColor = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(
        context,
        attrs,
        0
    ) {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
    }

    fun drawItemView() {
        if (!mInitialized) {
            return
        }
        clearUi()
        var totalPadding = paddingLeft + paddingRight.toFloat()
        var indexFrontView = 0
        val itemParams = itemLayoutParams
        for (i in mItems.indices) {
            val item = mItems[i]
            val position = i
            val itemLayout = createItemView(item)
            val itemTextView =
                itemLayout.findViewById<View>(R.id.item_text) as TextView
            itemTextView.isAllCaps = true
            itemTextView.textSize = 10f
            itemTextView.text = item
            itemTextView.setPadding(
                textPaddingLeft, textPaddingTop, textPaddingRight,
                texPaddingBottom
            )
            itemTextView.setTextColor(resources.getColor(mTextColor))
            var itemWidth =
                (itemTextView.paint.measureText(item) + textPaddingLeft
                        + textPaddingRight)
            itemWidth += (dpToPx(context, 20) + textPaddingLeft
                    + textPaddingRight).toFloat()
            if (mWidth <= itemWidth + totalPadding) {
                totalPadding = paddingLeft + paddingRight.toFloat()
                indexFrontView = i
                addItemView(itemLayout, itemParams, true, i)
            } else {
                if (i != indexFrontView) {
                    itemParams.rightMargin = mItemMargin
                    totalPadding += mItemMargin.toFloat()
                }
                addItemView(itemLayout, itemParams, false, i)
            }
            totalPadding += itemWidth
        }
        // }
    }

    private fun createItemView(s: String): View {
        val view: View = mInflater.inflate(R.layout.list_item_genre, this, false)
        if (isJellyBeanAndAbove) {
            view.background = getSelector(s)
        } else {
            view.setBackgroundDrawable(getSelector(s))
        }
        return view
    }

    private val itemLayoutParams: LayoutParams
        private get() {
            val itemParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            itemParams.bottomMargin = mItemMargin / 2
            itemParams.topMargin = 0
            itemParams.rightMargin = mItemMargin
            return itemParams
        }

    private fun getItemIcon(isSelected: Boolean): Int {
        return if (isSelected) mCancelIcon else mAddIcon
    }

    private fun clearUi() {
        removeAllViews()
        mRow = null
    }

    private fun addItemView(
        itemView: View, chipParams: ViewGroup.LayoutParams, newLine: Boolean,
        position: Int
    ) {
        if (mRow == null || newLine) {
            mRow = LinearLayout(context)
            mRow!!.gravity = Gravity.LEFT
            mRow!!.orientation = HORIZONTAL
            val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            mRow!!.layoutParams = params
            addView(mRow)
        }
        mRow!!.addView(itemView, chipParams)
        animateItemView(itemView, position)
    }

    private fun getSelector(s: String): StateListDrawable {
        return selectorNormal
    }

    @get:SuppressLint("ResourceAsColor")
    private val selectorNormal: StateListDrawable
        private get() {
            val states =
                StateListDrawable()
            var gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(mLayoutBackgroundColorPressed)
            gradientDrawable.cornerRadius = mRadius.toFloat()
            states.addState(intArrayOf(android.R.attr.state_pressed), gradientDrawable)
            gradientDrawable = GradientDrawable()
            val index = Random().nextInt(genresList.size)
            if (isUseRandomColor) mLayoutBackgroundColorNormal =
                Color.parseColor(genresList[index])
            gradientDrawable.setColor(mLayoutBackgroundColorNormal)
            gradientDrawable.cornerRadius = mRadius.toFloat()
            states.addState(intArrayOf(), gradientDrawable)
            return states
        }

    fun setSelector(colorCode: Int) {
        mLayoutBackgroundColorNormal = colorCode
        selectorNormal
    }

    @get:SuppressLint("ResourceAsColor")
    private val selectorSelected: StateListDrawable
        private get() {
            val states =
                StateListDrawable()
            var gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(mLayoutBackgroundColorNormal)
            gradientDrawable.cornerRadius = mRadius.toFloat()
            states.addState(intArrayOf(android.R.attr.state_pressed), gradientDrawable)
            gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(mLayoutBackgroundColorPressed)
            gradientDrawable.cornerRadius = mRadius.toFloat()
            states.addState(intArrayOf(), gradientDrawable)
            return states
        }

    val items: List<String>
        get() = mItems

    fun setItems(items: MutableList<String>) {
        mItems = items
        drawItemView()
    }

    fun clearItems() {
        mItems.clear()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener?) {
        mClickListener = clickListener
    }

    private val isJellyBeanAndAbove: Boolean
        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

    private fun animateView(view: View) {
        view.scaleY = 1f
        view.scaleX = 1f
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .setStartDelay(0)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    reverseAnimation(view)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            .start()
    }

    private fun reverseAnimation(view: View) {
        view.scaleY = 1.2f
        view.scaleX = 1.2f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(100)
            .setListener(null)
            .start()
    }

    private fun animateItemView(view: View, position: Int) {
        var animationDelay: Long = 600
        animationDelay += position * 30.toLong()
        view.scaleY = 0f
        view.scaleX = 0f
        view.animate()
            .scaleY(1f)
            .scaleX(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .setListener(null)
            .setStartDelay(animationDelay)
            .start()
    }

    interface OnItemClickListener {
        fun onClick(s: String?, position: Int)
    }

    companion object {
        private fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return Math.round(dp.toFloat() * density)
        }
    }

    init {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.CollectionPicker)
        mItemMargin = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_itemMargin,
            dpToPx(context, mItemMargin).toFloat()
        ).toInt()
        textPaddingLeft = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_textPaddingLeft,
            dpToPx(context, textPaddingLeft).toFloat()
        ).toInt()
        textPaddingRight = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_textPaddingRight,
            dpToPx(context, textPaddingRight).toFloat()
        ).toInt()
        textPaddingTop = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_textPaddingTop,
            dpToPx(context, textPaddingTop).toFloat()
        ).toInt()
        texPaddingBottom = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_textPaddingBottom,
            dpToPx(context, texPaddingBottom).toFloat()
        ).toInt()
        mAddIcon = typeArray.getResourceId(R.styleable.CollectionPicker_cp_addIcon, mAddIcon)
        mCancelIcon =
            typeArray.getResourceId(R.styleable.CollectionPicker_cp_cancelIcon, mCancelIcon)
        mLayoutBackgroundColorNormal = typeArray.getColor(
            R.styleable.CollectionPicker_cp_itemBackgroundNormal,
            mLayoutBackgroundColorNormal
        )
        mLayoutBackgroundColorPressed = typeArray.getColor(
            R.styleable.CollectionPicker_cp_itemBackgroundPressed,
            mLayoutBackgroundColorPressed
        )
        mRadius = typeArray.getDimension(
            R.styleable.CollectionPicker_cp_itemRadius,
            mRadius.toFloat()
        ).toInt()
        mTextColor =
            typeArray.getColor(R.styleable.CollectionPicker_cp_itemTextColor, mTextColor)
        simplifiedTags =
            typeArray.getBoolean(R.styleable.CollectionPicker_cp_simplified, false)
        typeArray.recycle()
        orientation = VERTICAL
        gravity = Gravity.LEFT
        mViewTreeObserver = viewTreeObserver
        mViewTreeObserver.addOnGlobalLayoutListener {
            if (!mInitialized) {
                mInitialized = true
                drawItemView()
            }
        }
    }
}