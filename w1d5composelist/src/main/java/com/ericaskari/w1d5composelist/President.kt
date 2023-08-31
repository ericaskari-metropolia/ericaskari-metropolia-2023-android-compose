package com.ericaskari.w1d5composelist

import android.content.Intent
import android.os.Bundle

class President {
    var name: String? = null
        private set
    var dutyStartYear = 0
        private set
    var dutyEndYear = 0
        private set
    var description: String? = null
        private set

    constructor(name: String?, dutyStartYear: Int, dutyEndYear: Int, description: String?) {
        this.name = name
        this.dutyStartYear = dutyStartYear
        this.dutyEndYear = dutyEndYear
        this.description = description
    }

    private constructor()

    fun attachToIntent(intent: Intent) {
        intent.putExtra(President.Companion.NAME, name)
        intent.putExtra(President.Companion.DUTY_DESC, description)
        intent.putExtra(President.Companion.DUTY_START_YEAR, dutyStartYear)
        intent.putExtra(President.Companion.DUTY_END_YEAR, dutyEndYear)
    }

    override fun toString(): String {
        return name!!
    }

    companion object {
        private const val NAME = "PRESIDENT.NAME"
        private const val DUTY_START_YEAR = "PRESIDENT.DUTY_START_YEAR"
        private const val DUTY_END_YEAR = "PRESIDENT.DUTY_END_YEAR"
        private const val DUTY_DESC = "PRESIDENT.DESC"
        fun extractFromBundle(bundle: Bundle): President {
            return President(
                bundle.getString(President.Companion.NAME),
                bundle.getInt(President.Companion.DUTY_START_YEAR),
                bundle.getInt(President.Companion.DUTY_END_YEAR),
                bundle.getString(President.Companion.DUTY_DESC)
            )
        }
    }
}
