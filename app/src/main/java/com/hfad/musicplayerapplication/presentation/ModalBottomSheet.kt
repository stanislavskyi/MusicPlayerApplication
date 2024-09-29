package com.hfad.musicplayerapplication.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hfad.musicplayerapplication.R

class ModalBottomSheet(private val listener: BottomSheetListener) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Найти кнопку или контейнер для "Загрузить трек" и установить слушатель клика
        view.findViewById<LinearLayout>(R.id.linearBottomSheet).setOnClickListener {
            listener.onUploadTrackClicked() // Передать событие через интерфейс
            dismiss() // Закрыть BottomSheet после клика
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

