package com.example.myclock

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.db.Device
import com.example.db.database
import org.jetbrains.anko.db.select

class CustomListAdapter(
    var context: Context,
    deviceDisplayList: ArrayList<Can>
) : BaseAdapter() {
    private var displayList = deviceDisplayList

//    fun getDisplayList() {
//        context.database.use {
//            displayList = select(Device.TABLE_NAME).parseList{
//                Device(HashMap(it))
//            }
//        }
//    }

    override fun getItem(position: Int): Any {
        return displayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return displayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var device = displayList[position]
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.linear_item, null)
            holder = ViewHolder(device, view)
            //视图持有者的内部控件对象已经在构造时一并初始化了，故这里无需再做赋值
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        initHolderContent(holder, device)
        initClickListeners(holder, position, device)
        return view!!
    }

    private fun initClickListeners(
        holder: ViewHolder,
        position: Int,
        device: Can
    ) {
        holder.start_btn.setOnClickListener {
            holder.start_btn.visibility = View.GONE
            holder.pause_btn.visibility = View.VISIBLE
//            holder.timerHandler.postDelayed(holder.countDown, 0)

            var action = device.id.toString();
            var intentFilter = IntentFilter(action)
            context.registerReceiver(NetworkStat,intentFilter)
            var intent = Intent("zhy.clock.ringing")
            intent.action = action
            var pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, device.id, intent, 0)
            holder.alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + device.remainingTime, pendingIntent)
        }

        holder.pause_btn.setOnClickListener {
            holder.pause_btn.visibility = View.GONE
            holder.start_btn.visibility = View.VISIBLE
            displayList[position] = device
            holder.timerHandler.removeCallbacks(holder.countDown)
        }

        holder.edit_btn.setOnClickListener {
            showDialog(holder, position)
        }
    }

    private fun initHolderContent(
        holder: ViewHolder,
        device: Can
    ) {
        holder.tv_id.text = device.id.toString() + "号缸"
        holder.tv_time.text = generateFormattedTime(device.remainingTime)
        if (device.isRunning) {
            holder.start_btn.visibility = View.GONE
            holder.pause_btn.visibility = View.VISIBLE
        } else {
            holder.pause_btn.visibility = View.GONE
            holder.start_btn.visibility = View.VISIBLE
        }
    }

    private fun generateFormattedTime(timeInSec: Int): String {
        var minute = timeInSec.div(60)
        var second = timeInSec.rem(60)
        var minuteStr: String = "$minute"
        var secStr: String = "$second"

        if (minute < 10) {
            minuteStr = "0$minute"
        }

        if (second < 10) {
            secStr = "0$second"
        }
        return "$minuteStr:$secStr"
    }

    private fun showDialog(holder: ViewHolder, position: Int) {
        var chosenTime = displayList[position].remainingTime
        AlertDialog.Builder(context).setTitle(R.string.dialog_title)
            .setSingleChoiceItems(R.array.time_period, 0) { _, which ->
                chosenTime = (which + 2) * 5 * 60
            }.setPositiveButton(R.string.dialog_confirm) { dialog, _ ->
                // 选择的时间，单位：s
                displayList[position].remainingTime = chosenTime
                holder.tv_time.text = generateFormattedTime(chosenTime)
                holder.start_btn.visibility = View.VISIBLE
                holder.pause_btn.visibility = View.GONE
                dialog.dismiss()
            }.setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    //ViewHolder中的属性在构造时初始化
    inner class ViewHolder(device: Can, val view: View) {
        val ll_item: LinearLayout = view.findViewById(R.id.ll_item) as LinearLayout
        val tv_id: TextView = view.findViewById(R.id.tv_id) as TextView
        val tv_time: TextView = view.findViewById(R.id.tv_time) as TextView
        val start_btn: ImageView = view.findViewById(R.id.start_btn) as ImageView
        val pause_btn: ImageView = view.findViewById(R.id.pause_btn) as ImageView
        val edit_btn: ImageView = view.findViewById(R.id.edit_btn) as ImageView
        var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timerHandler: Handler = Handler()
        var mCountTime = device.remainingTime
        val countDown = object : Runnable {
            override fun run() {
                tv_time.text = generateFormattedTime(mCountTime)

                if (mCountTime > 0) {
                    timerHandler.postDelayed(this, 1000)
                } else {
                    pause_btn.visibility = View.GONE
                    start_btn.visibility = View.VISIBLE
                    return
                }
                mCountTime--
            }
        }
    }
}