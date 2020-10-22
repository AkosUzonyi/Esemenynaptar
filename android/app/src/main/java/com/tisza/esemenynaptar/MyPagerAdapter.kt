package com.tisza.esemenynaptar

import android.content.*
import android.view.*
import android.widget.*
import androidx.viewpager.widget.*
import androidx.viewpager.widget.ViewPager.*
import com.tisza.esemenynaptar.database.*
import java.util.*

class MyPagerAdapter(context: Context, private val pager: ViewPager) : PagerAdapter(), OnPageChangeListener {
    companion object {
        private const val CHILD_COUNT = 5
        private const val MIDDLE_CHILD = CHILD_COUNT / 2
    }

    private var date = Calendar.getInstance()
        set(value) {
            setDate(value[Calendar.YEAR], value[Calendar.MONTH], value[Calendar.DAY_OF_MONTH])
        }

    private val childs: Array<ListView>
    private val adapters: Array<EventListAdapter>

    init {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        childs = Array(5) {
            layoutInflater.inflate(R.layout.event_list_view, pager, false) as ListView
        }
        adapters = Array(5) {
            EventListAdapter()
        }

        for (i in 0 until CHILD_COUNT) {
            childs[i].adapter = adapters[i]
        }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        date[year, month] = day
        for (i in 0 until CHILD_COUNT)
            loadDataForChild(i)
        pager.setCurrentItem(MIDDLE_CHILD, false)
        notifyDataSetChanged() //just for the titles
    }

    private fun loadDataForChild(childPos: Int) {
        val cal = date.clone() as Calendar
        cal.add(Calendar.DAY_OF_MONTH, childPos - MIDDLE_CHILD)
        adapters[childPos].setEvents(EventDatabase.instance.eventDao().getEventsForDate(cal))
        childs[childPos].setSelection(0)
    }

    private fun shift(diff: Int) {
        require(!(diff <= -CHILD_COUNT || diff >= CHILD_COUNT)) { "cannot shift with values larger than CHILD_COUNT: $diff" }

        date.add(Calendar.DAY_OF_MONTH, diff)
        val oldAdapters = adapters.clone()
        for (i in 0 until CHILD_COUNT) {
            adapters[i] = oldAdapters[(i + diff + CHILD_COUNT) % CHILD_COUNT]
            childs[i].adapter = adapters[i]
            if (i + diff < 0 || i + diff >= CHILD_COUNT) loadDataForChild(i)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val c = date.clone() as Calendar
        c.add(Calendar.DAY_OF_MONTH, position - MIDDLE_CHILD)
        return MainActivity.Companion.dateFormat.format(c.time)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = childs[position]
        container.addView(v)
        return v
    }

    override fun getCount(): Int {
        return CHILD_COUNT
    }

    override fun isViewFromObject(v: View, o: Any): Boolean {
        return v === o
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == SCROLL_STATE_IDLE) {
            shift(pager.currentItem - MIDDLE_CHILD)
            pager.setCurrentItem(MIDDLE_CHILD, false)
        }
    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
    override fun onPageSelected(pos: Int) {}
}
