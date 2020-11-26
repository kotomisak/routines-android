package cz.kotox.core.arch

import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import cz.kotox.core.entity.SimpleDate
import cz.kotox.core.entity.factory.getCalendarInstanceDateOnly
import cz.kotox.core.entity.factory.getSimpleDateInstance
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Date

interface MaterialDatePickerActions {

	fun showMaterialBirthDatePicker(
			currentFragmentManager: FragmentManager,
			currentSelection: SimpleDate?,
			minimumUserAgeInYears: Int = 0,
			onDateSelected: (dateSelected: SimpleDate) -> Unit
	) {

		val endRestriction = Calendar.getInstance()
		endRestriction.add(Calendar.YEAR, -minimumUserAgeInYears + 1)

		val openAt = if (currentSelection == null) {
			Calendar.getInstance().apply { add(Calendar.YEAR, -minimumUserAgeInYears) }
		} else {
			val currentSelectionCalendar = getCalendarInstanceDateOnly(currentSelection)
			//Solve situation when incoming selection is lower than minimum user age.
			if (currentSelectionCalendar > endRestriction) endRestriction else currentSelectionCalendar
		}

		val datePicker: MaterialDatePicker<Long> =
			MaterialDatePicker.Builder.datePicker()
				.setCalendarConstraints(CalendarConstraints.Builder()
					.setEnd(endRestriction.timeInMillis)
					.setOpenAt(openAt.timeInMillis)
					.build())
				//Seems like bug in setSelection because I have to increase one day for proper selection :-(
				.setSelection(openAt.apply { this.add(DAY_OF_MONTH,1) }.timeInMillis)
				.build()

		datePicker.addOnPositiveButtonClickListener {
			onDateSelected.invoke(getSimpleDateInstance(Date(it)))
		}

		datePicker.show(currentFragmentManager, datePicker.toString())
	}

	fun showMaterialDateRangePicker(
			currentFragmentManager: FragmentManager,
			fromDateSelection: SimpleDate?,
			toDateSelection: SimpleDate?,
			onDateSelected: (fromDate: SimpleDate?, toDate: SimpleDate?) -> Unit
	) {
		//Seems like bug in setSelection because I have to increase one day for proper selection :-(
		val currentSelection = Pair(fromDateSelection?.let { getCalendarInstanceDateOnly(it).apply { this.add(DAY_OF_MONTH,1) }.timeInMillis }, toDateSelection?.let { getCalendarInstanceDateOnly(it).apply { this.add(DAY_OF_MONTH,1) }.timeInMillis })
		val datePicker: MaterialDatePicker<Pair<Long, Long>> =
			MaterialDatePicker.Builder.dateRangePicker()
				.setCalendarConstraints(CalendarConstraints.Builder()
					.build())
				.setSelection(currentSelection)
				.build()

		datePicker.addOnPositiveButtonClickListener {
			onDateSelected.invoke(
				it.first?.let { fromDate -> getSimpleDateInstance(Date(fromDate)) },
				it.second?.let { toDate -> getSimpleDateInstance(Date(toDate)) }
			)
		}

		datePicker.show(currentFragmentManager, datePicker.toString())
	}

}