package com.stupidtree.style.widgets

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import com.stupidtree.style.R
import com.stupidtree.style.databinding.DialogBottomColorPickerBinding

class PopUpColorPicker: TransparentBottomSheetDialog<DialogBottomColorPickerBinding>() {
    interface OnColorSelectedListener {
        fun onSelected(color: Int)
    }

    private var onColorSelectedListener: OnColorSelectedListener? = null

    private var color = Color.CYAN

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_color_picker
    }

    override fun initViewBinding(v: View): DialogBottomColorPickerBinding {
        return DialogBottomColorPickerBinding.bind(v)
    }

    fun initColor(color: Int): PopUpColorPicker {
        this.color = color
        return this
    }
    fun setOnColorSelectListener(l: OnColorSelectedListener): PopUpColorPicker {
        this.onColorSelectedListener = l
        return this
    }

    private fun syncDemoWithColor() {
       binding.colorDemo.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun onStart() {
        super.onStart()
        syncSeekbarWithColor()
    }

    private fun syncSeekbarWithColor() {
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        binding.sbR.progress = red
        binding.sbG.progress = green
        binding.sbB.progress = blue
    }
    override fun initViews(v: View) {
        binding.sbR.max = 255
        binding.sbG.max = 255
        binding.sbB.max = 255
        val seekBarChangeListener: SeekBar.OnSeekBarChangeListener =
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    color = Color.rgb(binding.sbR.progress, binding.sbG.progress, binding.sbB.progress)
                    syncDemoWithColor()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        binding.sbG.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.sbR.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.sbB.setOnSeekBarChangeListener(seekBarChangeListener)

        binding.done.setOnClickListener {
            onColorSelectedListener?.onSelected(color)
            dismiss()
        }
    }
}